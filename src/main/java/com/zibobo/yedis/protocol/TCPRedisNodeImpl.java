/**
 * Copyright (C) 2006-2009 Dustin Sallings
 * Copyright (C) 2009-2013 Couchbase, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALING
 * IN THE SOFTWARE.
 */

package com.zibobo.yedis.protocol;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.spy.memcached.compat.SpyObject;

import com.zibobo.yedis.RedisNode;
import com.zibobo.yedis.ops.Operation;
import com.zibobo.yedis.ops.OperationState;

/**
 * Represents a node with the redis cluster, along with buffering and operation
 * queues.
 */
public abstract class TCPRedisNodeImpl extends SpyObject implements RedisNode {

    private final SocketAddress socketAddress;
    private final ByteBuffer rbuf;
    private final ByteBuffer wbuf;
    protected final BlockingQueue<Operation> writeQ;
    private final BlockingQueue<Operation> readQ;
    private final BlockingQueue<Operation> inputQueue;
    private final long opQueueMaxBlockTime;
    private final AtomicInteger reconnectAttempt = new AtomicInteger(1);
    private SocketChannel channel;
    private int toWrite = 0;
    protected Operation optimizedOp = null;
    private volatile SelectionKey sk = null;
    private final long defaultOpTimeout;
    private long lastReadTimestamp = System.currentTimeMillis();

    // operation Future.get timeout counter
    private final AtomicInteger continuousTimeout = new AtomicInteger(0);

    public TCPRedisNodeImpl(SocketAddress sa, SocketChannel c, int bufSize,
            BlockingQueue<Operation> rq, BlockingQueue<Operation> wq,
            BlockingQueue<Operation> iq, long opQueueMaxBlockTime, long dt) {
        super();
        assert sa != null : "No SocketAddress";
        assert c != null : "No SocketChannel";
        assert bufSize > 0 : "Invalid buffer size: " + bufSize;
        assert rq != null : "No operation read queue";
        assert wq != null : "No operation write queue";
        assert iq != null : "No input queue";
        socketAddress = sa;
        setChannel(c);
        // Since these buffers are allocated rarely (only on client creation
        // or reconfigure), and are passed to Channel.read() and
        // Channel.write(),
        // use direct buffers to avoid
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6214569
        rbuf = ByteBuffer.allocateDirect(bufSize);
        wbuf = ByteBuffer.allocateDirect(bufSize);
        getWbuf().clear();
        readQ = rq;
        writeQ = wq;
        inputQueue = iq;
        this.opQueueMaxBlockTime = opQueueMaxBlockTime;
        defaultOpTimeout = dt;
    }

    @Override
    public final void copyInputQueue() {
        Collection<Operation> tmp = new ArrayList<Operation>();

        // don't drain more than we have space to place
        inputQueue.drainTo(tmp, writeQ.remainingCapacity());
        writeQ.addAll(tmp);
    }


    @Override
    public Collection<Operation> destroyInputQueue() {
        Collection<Operation> rv = new ArrayList<Operation>();
        inputQueue.drainTo(rv);
        return rv;
    }


    @Override
    public final void setupResend() {

        Operation op = getCurrentWriteOp();

        if (op != null) {
            ByteBuffer buf = op.getBuffer();
            if (buf != null) {
                buf.reset();
            } else {
                getLogger().info("No buffer for current write op, removing");
                removeCurrentWriteOp();
            }
        }
        // Now cancel all the pending read operations. Might be better to
        // to requeue them.
        while (hasReadOp()) {
            op = removeCurrentReadOp();
            if (op != getCurrentWriteOp()) {
                getLogger().warn("Discarding partially completed op: %s", op);
                op.cancel();
            }
        }

        getWbuf().clear();
        getRbuf().clear();
        toWrite = 0;
    }

    // Prepare the pending operations. Return true if there are any pending
    // ops
    private boolean preparePending() {
        // Copy the input queue into the write queue.
        copyInputQueue();

        // Now check the ops
        Operation nextOp = getCurrentWriteOp();
        while (nextOp != null && nextOp.isCancelled()) {
            getLogger().info("Removing cancelled operation: %s", nextOp);
            removeCurrentWriteOp();
            nextOp = getCurrentWriteOp();
        }
        return nextOp != null;
    }

    @Override
    public final void fillWriteBuffer(boolean shouldOptimize) {
        if (toWrite == 0 && readQ.remainingCapacity() > 0) {
            getWbuf().clear();
            Operation o = getNextWritableOp();

            while (o != null && toWrite < getWbuf().capacity()) {
                synchronized (o) {
                    assert o.getState() == OperationState.WRITING;

                    ByteBuffer obuf = o.getBuffer();
                    assert obuf != null : "Didn't get a write buffer from " + o;
                    int bytesToCopy =
                            Math.min(getWbuf().remaining(), obuf.remaining());
                    byte[] b = new byte[bytesToCopy];
                    obuf.get(b);
                    getWbuf().put(b);
                    getLogger().debug("After copying stuff from %s: %s", o,
                            getWbuf());
                    if (!o.getBuffer().hasRemaining()) {
                        o.writeComplete();
                        transitionWriteItem();

                        preparePending();
                        if (shouldOptimize) {
                            optimize();
                        }

                        o = getNextWritableOp();
                    }
                    toWrite += bytesToCopy;
                }
            }
            getWbuf().flip();
            assert toWrite <= getWbuf().capacity() : "toWrite exceeded capacity: "
                    + this;
            assert toWrite == getWbuf().remaining() : "Expected " + toWrite
                    + " remaining, got " + getWbuf().remaining();
        } else {
            getLogger().debug("Buffer is full, skipping");
        }
    }

    private Operation getNextWritableOp() {
        Operation o = getCurrentWriteOp();
        while (o != null && o.getState() == OperationState.WRITE_QUEUED) {
            synchronized (o) {
                if (o.isCancelled()) {
                    getLogger().debug("Not writing cancelled op.");
                    Operation cancelledOp = removeCurrentWriteOp();
                    assert o == cancelledOp;
                } else if (o.isTimedOut(defaultOpTimeout)) {
                    getLogger().debug("Not writing timed out op.");
                    Operation timedOutOp = removeCurrentWriteOp();
                    assert o == timedOutOp;
                } else {
                    o.writing();
                    readQ.add(o);
                    return o;
                }
                o = getCurrentWriteOp();
            }
        }
        return o;
    }


    @Override
    public final void transitionWriteItem() {
        Operation op = removeCurrentWriteOp();
        assert op != null : "There is no write item to transition";
        getLogger().debug("Finished writing %s", op);
    }

    protected abstract void optimize();


    @Override
    public final Operation getCurrentReadOp() {
        return readQ.peek();
    }


    @Override
    public final Operation removeCurrentReadOp() {
        return readQ.remove();
    }


    @Override
    public final Operation getCurrentWriteOp() {
        return optimizedOp == null ? writeQ.peek() : optimizedOp;
    }

    @Override
    public final Operation removeCurrentWriteOp() {
        Operation rv = optimizedOp;
        if (rv == null) {
            rv = writeQ.remove();
        } else {
            optimizedOp = null;
        }
        return rv;
    }


    @Override
    public final boolean hasReadOp() {
        return !readQ.isEmpty();
    }


    @Override
    public final boolean hasWriteOp() {
        return !(optimizedOp == null && writeQ.isEmpty());
    }


    @Override
    public final void addOp(Operation op) {
        try {
            if (!inputQueue.offer(op, opQueueMaxBlockTime,
                    TimeUnit.MILLISECONDS)) {
                throw new IllegalStateException("Timed out waiting to add "
                        + op + "(max wait=" + opQueueMaxBlockTime + "ms)");
            }
        } catch (InterruptedException e) {
            // Restore the interrupted status
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting to add "
                    + op);
        }
    }


    @Override
    public final void insertOp(Operation op) {
        ArrayList<Operation> tmp =
                new ArrayList<Operation>(inputQueue.size() + 1);
        tmp.add(op);
        inputQueue.drainTo(tmp);
        inputQueue.addAll(tmp);
    }


    @Override
    public final int getSelectionOps() {
        int rv = 0;
        if (getChannel().isConnected()) {
            if (hasReadOp()) {
                rv |= SelectionKey.OP_READ;
            }
            if (toWrite > 0 || hasWriteOp()) {
                rv |= SelectionKey.OP_WRITE;
            }
        } else {
            rv = SelectionKey.OP_CONNECT;
        }
        return rv;
    }


    @Override
    public final ByteBuffer getRbuf() {
        return rbuf;
    }


    @Override
    public final ByteBuffer getWbuf() {
        return wbuf;
    }


    @Override
    public final SocketAddress getSocketAddress() {
        return socketAddress;
    }


    @Override
    public final boolean isActive() {
        return reconnectAttempt.get() == 0 && getChannel() != null
                && getChannel().isConnected();
    }


    @Override
    public final void reconnecting() {
        reconnectAttempt.incrementAndGet();
        continuousTimeout.set(0);
    }


    @Override
    public final void connected() {
        reconnectAttempt.set(0);
        continuousTimeout.set(0);
    }


    @Override
    public final int getReconnectCount() {
        return reconnectAttempt.get();
    }


    @Override
    public final String toString() {
        int sops = 0;
        if (getSk() != null && getSk().isValid()) {
            sops = getSk().interestOps();
        }
        int rsize = readQ.size() + (optimizedOp == null ? 0 : 1);
        int wsize = writeQ.size();
        int isize = inputQueue.size();
        return "{QA sa=" + getSocketAddress() + ", #Rops=" + rsize + ", #Wops="
                + wsize + ", #iq=" + isize + ", topRop=" + getCurrentReadOp()
                + ", topWop=" + getCurrentWriteOp() + ", toWrite=" + toWrite
                + ", interested=" + sops + "}";
    }


    @Override
    public final void registerChannel(SocketChannel ch, SelectionKey skey) {
        setChannel(ch);
        setSk(skey);
    }


    @Override
    public final void setChannel(SocketChannel to) {
        assert channel == null || !channel.isOpen() : "Attempting to overwrite channel";
        channel = to;
    }


    @Override
    public final SocketChannel getChannel() {
        return channel;
    }


    @Override
    public final void setSk(SelectionKey to) {
        sk = to;
    }


    @Override
    public final SelectionKey getSk() {
        return sk;
    }


    @Override
    public final int getBytesRemainingToWrite() {
        return toWrite;
    }


    @Override
    public final int writeSome() throws IOException {
        int wrote = channel.write(wbuf);
        assert wrote >= 0 : "Wrote negative bytes?";
        toWrite -= wrote;
        assert toWrite >= 0 : "toWrite went negative after writing " + wrote
                + " bytes for " + this;
        getLogger().debug("Wrote %d bytes", wrote);
        return wrote;
    }


    @Override
    public void setContinuousTimeout(boolean timedOut) {
        if (timedOut && isActive()) {
            continuousTimeout.incrementAndGet();
        } else {
            continuousTimeout.set(0);
        }
    }


    @Override
    public int getContinuousTimeout() {
        return continuousTimeout.get();
    }

    @Override
    public final void fixupOps() {
        // As the selection key can be changed at any point due to node
        // failure, we'll grab the current volatile value and configure it.
        SelectionKey s = sk;
        if (s != null && s.isValid()) {
            int iops = getSelectionOps();
            getLogger().debug("Setting interested opts to %d", iops);
            s.interestOps(iops);
        } else {
            getLogger().debug("Selection key is not valid.");
        }
    }

    /**
     * Number of milliseconds since the last read of this node completed.
     *
     * @return milliseconds since last read.
     */
    @Override
    public long lastReadDelta() {
        return System.currentTimeMillis() - lastReadTimestamp;
    }

    /**
     * Mark this node as having just completed a read.
     */
    @Override
    public void completedRead() {
        lastReadTimestamp = System.currentTimeMillis();
    }

}
