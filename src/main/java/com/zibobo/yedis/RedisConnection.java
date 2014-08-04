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
package com.zibobo.yedis;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.compat.SpyThread;
import net.spy.memcached.compat.log.LoggerFactory;
import net.spy.memcached.metrics.MetricCollector;
import net.spy.memcached.metrics.MetricType;

import com.zibobo.yedis.exception.RedisException;
import com.zibobo.yedis.internal.OperationFuture;
import com.zibobo.yedis.ops.Operation;
import com.zibobo.yedis.ops.OperationState;
import com.zibobo.yedis.ops.OperationStatus;
import com.zibobo.yedis.ops.StringReplyCallback;
import com.zibobo.yedis.ops.connection.PingOperation;

/**
 * Connection to a cluster of redis servers.
 */
public class RedisConnection extends SpyThread {

    // The number of empty selects we'll allow before assuming we may have
    // missed one and should check the current selectors. This generally
    // indicates a bug, but we'll check it nonetheless.
    private static final int DOUBLE_CHECK_EMPTY = 256;
    // The number of empty selects we'll allow before blowing up. It's too
    // easy to write a bug that causes it to loop uncontrollably. This helps
    // find those bugs and often works around them.
    private static final int EXCESSIVE_EMPTY = 0x1000000;

    private static final String RECON_QUEUE_METRIC =
            "[MEM] Reconnecting Nodes (ReconnectQueue)";
    private static final String SHUTD_QUEUE_METRIC =
            "[MEM] Shutting Down Nodes (NodesToShutdown)";
    private static final String OVERALL_REQUEST_METRIC =
            "[MEM] Request Rate: All";
    private static final String OVERALL_AVG_BYTES_WRITE_METRIC =
            "[MEM] Average Bytes written to OS per write";
    private static final String OVERALL_AVG_BYTES_READ_METRIC =
            "[MEM] Average Bytes read from OS per read";
    private static final String OVERALL_AVG_TIME_ON_WIRE_METRIC =
            "[MEM] Average Time on wire for operations (µs)";
    private static final String OVERALL_RESPONSE_METRIC =
            "[MEM] Response Rate: All (Failure + Success + Retry)";
    private static final String OVERALL_RESPONSE_RETRY_METRIC =
            "[MEM] Response Rate: Retry";
    private static final String OVERALL_RESPONSE_FAIL_METRIC =
            "[MEM] Response Rate: Failure";
    private static final String OVERALL_RESPONSE_SUCC_METRIC =
            "[MEM] Response Rate: Success";

    protected volatile boolean shutDown = false;
    protected Selector selector = null;
    protected final NodeLocator locator;
    protected final FailureMode failureMode;
    // maximum amount of time to wait between reconnect attempts
    private final long maxDelay;
    private int emptySelects = 0;
    private final int bufSize;
    private final ConnectionFactory connectionFactory;
    // AddedQueue is used to track the QueueAttachments for which operations
    // have recently been queued.
    protected final ConcurrentLinkedQueue<RedisNode> addedQueue;
    // reconnectQueue contains the attachments that need to be reconnected
    // The key is the time at which they are eligible for reconnect
    private final SortedMap<Long, RedisNode> reconnectQueue;

    protected volatile boolean running = true;

    private final OperationFactory opFact;
    private final int timeoutExceptionThreshold;
    private final Collection<Operation> retryOps;
    protected final ConcurrentLinkedQueue<RedisNode> nodesToShutdown;
    private final boolean verifyAliveOnConnect;
    private final ExecutorService listenerExecutorService;

    protected final MetricCollector metrics;
    protected final MetricType metricType;

    /**
     * Construct a redis connection.
     *
     * @param bufSize
     *            the size of the buffer used for reading from the server
     * @param f
     *            the factory that will provide an operation queue
     * @param a
     *            the addresses of the servers to connect to
     *
     * @throws IOException
     *             if a connection attempt fails early
     */
    public RedisConnection(int bufSize, ConnectionFactory f,
            InetSocketAddress writeAddress, List<InetSocketAddress> a,
            FailureMode fm, OperationFactory opfactory) throws IOException {
        reconnectQueue = new TreeMap<Long, RedisNode>();
        addedQueue = new ConcurrentLinkedQueue<RedisNode>();
        failureMode = fm;
        maxDelay = f.getMaxReconnectDelay();
        opFact = opfactory;
        timeoutExceptionThreshold = f.getTimeoutExceptionThreshold();
        selector = Selector.open();
        retryOps = new ArrayList<Operation>();
        nodesToShutdown = new ConcurrentLinkedQueue<RedisNode>();
        listenerExecutorService = f.getListenerExecutorService();
        this.bufSize = bufSize;
        this.connectionFactory = f;

        String verifyAlive = System.getProperty("net.spy.verifyAliveOnConnect");
        if (verifyAlive != null && verifyAlive.equals("true")) {
            verifyAliveOnConnect = true;
        } else {
            verifyAliveOnConnect = false;
        }

        List<RedisNode> connections = createConnections(a);
        RedisNode writeNode = createConnection(writeAddress);
        locator = f.createLocator(writeNode, connections);

        metrics = f.getMetricCollector();
        metricType = f.enableMetrics();

        registerMetrics();

        setName("Redis IO over " + this);
        setDaemon(f.isDaemon());
        start();
    }

    /**
     * Register Metrics for collection.
     *
     * Note that these Metrics may or may not take effect, depending on the
     * {@link MetricCollector} implementation. This can be controlled from the
     * {@link DefaultConnectionFactory}.
     */
    protected void registerMetrics() {
        if (metricType.equals(MetricType.DEBUG)
                || metricType.equals(MetricType.PERFORMANCE)) {
            metrics.addHistogram(OVERALL_AVG_BYTES_READ_METRIC);
            metrics.addHistogram(OVERALL_AVG_BYTES_WRITE_METRIC);
            metrics.addHistogram(OVERALL_AVG_TIME_ON_WIRE_METRIC);
            metrics.addMeter(OVERALL_RESPONSE_METRIC);
            metrics.addMeter(OVERALL_REQUEST_METRIC);

            if (metricType.equals(MetricType.DEBUG)) {
                metrics.addCounter(RECON_QUEUE_METRIC);
                metrics.addCounter(SHUTD_QUEUE_METRIC);
                metrics.addMeter(OVERALL_RESPONSE_RETRY_METRIC);
                metrics.addMeter(OVERALL_RESPONSE_SUCC_METRIC);
                metrics.addMeter(OVERALL_RESPONSE_FAIL_METRIC);
            }
        }
    }

    protected List<RedisNode> createConnections(
            final Collection<InetSocketAddress> a) throws IOException {
        List<RedisNode> connections = new ArrayList<RedisNode>(a.size());
        for (SocketAddress sa : a) {
            RedisNode qa = createConnection(sa);
            connections.add(qa);
        }
        return connections;
    }

    protected RedisNode createConnection(SocketAddress sa) throws IOException {

        SocketChannel ch = SocketChannel.open();
        ch.configureBlocking(false);
        RedisNode qa = this.connectionFactory.createRedisNode(sa, ch, bufSize);
        int ops = 0;
        ch.socket().setTcpNoDelay(!this.connectionFactory.useNagleAlgorithm());
        // Initially I had attempted to skirt this by queueing every
        // connect, but it considerably slowed down start time.
        try {
            if (ch.connect(sa)) {
                getLogger().info("Connected to %s immediately", qa);
                connected(qa);
            } else {
                getLogger().info("Added %s to connect queue", qa);
                ops = SelectionKey.OP_CONNECT;
            }

            selector.wakeup();
            qa.setSk(ch.register(selector, ops, qa));

            assert ch.isConnected()
                    || qa.getSk().interestOps() == SelectionKey.OP_CONNECT : "Not connected, and not wanting to connect";
        } catch (SocketException e) {
            getLogger().warn("Socket error on initial connect", e);
            queueReconnect(qa);
        }
        return qa;
    }

    private boolean selectorsMakeSense() {
        for (RedisNode qa : locator.getAll()) {
            if (qa.getSk() != null && qa.getSk().isValid()) {
                if (qa.getChannel().isConnected()) {
                    int sops = qa.getSk().interestOps();
                    int expected = 0;
                    if (qa.hasReadOp()) {
                        expected |= SelectionKey.OP_READ;
                    }
                    if (qa.hasWriteOp()) {
                        expected |= SelectionKey.OP_WRITE;
                    }
                    if (qa.getBytesRemainingToWrite() > 0) {
                        expected |= SelectionKey.OP_WRITE;
                    }
                    assert sops == expected : "Invalid ops:  " + qa
                            + ", expected " + expected + ", got " + sops;
                } else {
                    int sops = qa.getSk().interestOps();
                    assert sops == SelectionKey.OP_CONNECT : "Not connected, and not watching for connect: "
                            + sops;
                }
            }
        }
        getLogger().debug("Checked the selectors.");
        return true;
    }

    /**
     * MemcachedClient calls this method to handle IO over the connections.
     */
    public void handleIO() throws IOException {
        if (shutDown) {
            throw new IOException("No IO while shut down");
        }

        // Deal with all of the stuff that's been added, but may not be marked
        // writable.
        handleInputQueue();
        getLogger().debug("Done dealing with queue.");

        long delay = 0;
        if (!reconnectQueue.isEmpty()) {
            long now = System.currentTimeMillis();
            long then = reconnectQueue.firstKey();
            delay = Math.max(then - now, 1);
        }
        getLogger().debug("Selecting with delay of %sms", delay);
        assert selectorsMakeSense() : "Selectors don't make sense.";
        int selected = selector.select(delay);
        Set<SelectionKey> selectedKeys = selector.selectedKeys();

        if (selectedKeys.isEmpty() && !shutDown) {
            getLogger().debug(
                    "No selectors ready, interrupted: " + Thread.interrupted());
            if (++emptySelects > DOUBLE_CHECK_EMPTY) {
                for (SelectionKey sk : selector.keys()) {
                    getLogger().debug("%s has %s, interested in %s", sk,
                            sk.readyOps(), sk.interestOps());
                    if (sk.readyOps() != 0) {
                        getLogger().debug("%s has a ready op, handling IO", sk);
                        handleIO(sk);
                    } else {
                        lostConnection((RedisNode) sk.attachment());
                    }
                }
                assert emptySelects < EXCESSIVE_EMPTY : "Too many empty selects";
            }
        } else {
            getLogger().debug("Selected %d, selected %d keys", selected,
                    selectedKeys.size());
            emptySelects = 0;

            for (SelectionKey sk : selectedKeys) {
                handleIO(sk);
            }
            selectedKeys.clear();
        }

        // see if any connections blew up with large number of timeouts
        boolean stillCheckingTimeouts = true;
        while (stillCheckingTimeouts) {
            try {
                for (SelectionKey sk : selector.keys()) {
                    RedisNode mn = (RedisNode) sk.attachment();
                    if (mn.getContinuousTimeout() > timeoutExceptionThreshold) {
                        getLogger().warn(
                                "%s exceeded continuous timeout threshold", sk);
                        lostConnection(mn);
                    }
                }
                stillCheckingTimeouts = false;
            } catch (ConcurrentModificationException e) {
                getLogger().warn(
                        "Retrying selector keys after "
                                + "ConcurrentModificationException caught", e);
                continue;
            }
        }

        if (!shutDown && !reconnectQueue.isEmpty()) {
            attemptReconnects();
        }
        // rehash any operations that are in retry state
        redistributeOperations(retryOps);
        retryOps.clear();

        // try to shutdown odd nodes
        for (RedisNode qa : nodesToShutdown) {
            if (!addedQueue.contains(qa)) {
                nodesToShutdown.remove(qa);
                metrics.decrementCounter(SHUTD_QUEUE_METRIC);
                Collection<Operation> notCompletedOperations =
                        qa.destroyInputQueue();
                if (qa.getChannel() != null) {
                    qa.getChannel().close();
                    qa.setSk(null);
                    if (qa.getBytesRemainingToWrite() > 0) {
                        getLogger().warn(
                                "Shut down with %d bytes remaining to write",
                                qa.getBytesRemainingToWrite());
                    }
                    getLogger().debug("Shut down channel %s", qa.getChannel());
                }
                redistributeOperations(notCompletedOperations);
            }
        }
    }

    // Handle any requests that have been made against the client.
    private void handleInputQueue() {
        if (!addedQueue.isEmpty()) {
            getLogger().debug("Handling queue");
            // If there's stuff in the added queue. Try to process it.
            Collection<RedisNode> toAdd = new HashSet<RedisNode>();
            // Transfer the queue into a hashset. There are very likely more
            // additions than there are nodes.
            Collection<RedisNode> todo = new HashSet<RedisNode>();
            RedisNode qaNode = null;
            while ((qaNode = addedQueue.poll()) != null) {
                todo.add(qaNode);
            }

            // Now process the queue.
            for (RedisNode qa : todo) {
                boolean readyForIO = false;
                if (qa.isActive()) {
                    if (qa.getCurrentWriteOp() != null) {
                        readyForIO = true;
                        getLogger().debug("Handling queued write %s", qa);
                    }
                } else {
                    toAdd.add(qa);
                }
                qa.copyInputQueue();
                if (readyForIO) {
                    try {
                        if (qa.getWbuf().hasRemaining()) {
                            handleWrites(qa.getSk(), qa);
                        }
                    } catch (IOException e) {
                        getLogger().warn("Exception handling write", e);
                        lostConnection(qa);
                    }
                }
                qa.fixupOps();
            }
            addedQueue.addAll(toAdd);
        }
    }

    private void connected(RedisNode node) {
        assert node.getChannel().isConnected() : "Not connected.";
        node.connected();
    }

    private void lostConnection(RedisNode qa) {
        queueReconnect(qa);
    }

    /**
     * Makes sure that the given SelectionKey belongs to the current cluster.
     *
     * Before trying to connect to a node, make sure it actually belongs to the
     * currently connected cluster.
     */
    boolean belongsToCluster(RedisNode node) {
        for (RedisNode n : locator.getAll()) {
            if (n.getSocketAddress().equals(node.getSocketAddress())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handle IO for a specific selector. Any IOException will cause a
     * reconnect.
     *
     * Note that this code makes sure that the corresponding node is not only
     * able to connect, but also able to respond in a correct fashion. This is
     * handled by issuing a dummy version/noop call and making sure it returns
     * in a correct and timely fashion.
     *
     * @param sk
     *            the selector to handle IO against.
     */
    private void handleIO(SelectionKey sk) {
        RedisNode node = (RedisNode) sk.attachment();
        try {
            getLogger().debug("Handling IO for:  %s (r=%s, w=%s, c=%s, op=%s)",
                    sk, sk.isReadable(), sk.isWritable(), sk.isConnectable(),
                    sk.attachment());
            if (sk.isConnectable() && belongsToCluster(node)) {
                getLogger().info("Connection state changed for %s", sk);
                final SocketChannel channel = node.getChannel();
                if (channel.finishConnect()) {

                    if (verifyAliveOnConnect) {
                        // Test to see if it's truly alive, could be a hung
                        // process, OS
                        final CountDownLatch latch = new CountDownLatch(1);
                        final OperationFuture<Boolean> rv =
                                new OperationFuture<Boolean>(latch, 2500,
                                        listenerExecutorService);
                        PingOperation testOp =
                                opFact.ping(new StringReplyCallback() {

                                    @Override
                                    public void receivedStatus(
                                            OperationStatus status) {
                                        rv.set(status.isSuccess(), status);
                                    }

                                    @Override
                                    public void complete() {
                                        latch.countDown();
                                    }

                                    @Override
                                    public void onReply(String pong) {

                                    }
                                });

                        testOp.setHandlingNode(node);
                        testOp.initialize();

                        checkState();
                        insertOperation(node, testOp);
                        node.copyInputQueue();

                        boolean done = false;
                        if (sk.isValid()) {
                            long timeout =
                                    TimeUnit.MILLISECONDS
                                            .toNanos(connectionFactory
                                                    .getOperationTimeout());
                            for (long stop = System.nanoTime() + timeout; stop > System
                                    .nanoTime();) {
                                handleWrites(sk, node);
                                handleReads(sk, node);
                                if (done = (latch.getCount() == 0)) {
                                    break;
                                }
                            }
                        }

                        if (!done || testOp.isCancelled()
                                || testOp.hasErrored() || testOp.isTimedOut()) {
                            throw new ConnectException(
                                    "Could not send noop upon connect! "
                                            + "This may indicate a running, but not responding memcached "
                                            + "instance.");
                        }
                    }

                    connected(node);
                    addedQueue.offer(node);
                    if (node.getWbuf().hasRemaining()) {
                        handleWrites(sk, node);
                    }
                } else {
                    assert !channel.isConnected() : "connected";
                }
            } else {
                if (sk.isValid() && sk.isReadable()) {
                    handleReads(sk, node);
                }
                if (sk.isValid() && sk.isWritable()) {
                    handleWrites(sk, node);
                }
            }
        } catch (ClosedChannelException e) {
            // Note, not all channel closes end up here
            if (!shutDown) {
                getLogger().info(
                        "Closed channel and not shutting down. Queueing"
                                + " reconnect on %s", node, e);
                lostConnection(node);
            }
        } catch (ConnectException e) {
            // Failures to establish a connection should attempt a reconnect
            // without signaling the observers.
            getLogger().info("Reconnecting due to failure to connect to %s",
                    node, e);
            queueReconnect(node);
        } catch (RedisException e) {
            throw e;
        } catch (Exception e) {
            // Any particular error processing an item should simply
            // cause us to reconnect to the server.
            //
            getLogger().info("Reconnecting due to exception on %s", node, e);
            lostConnection(node);
        }
        node.fixupOps();
    }

    private void handleWrites(SelectionKey sk, RedisNode qa) throws IOException {
        qa.fillWriteBuffer(false);
        boolean canWriteMore = qa.getBytesRemainingToWrite() > 0;
        while (canWriteMore) {
            int wrote = qa.writeSome();
            metrics.updateHistogram(OVERALL_AVG_BYTES_WRITE_METRIC, wrote);
            qa.fillWriteBuffer(false);
            canWriteMore = wrote > 0 && qa.getBytesRemainingToWrite() > 0;
        }
    }

    private void handleReads(SelectionKey sk, RedisNode qa) throws IOException {
        Operation currentOp = qa.getCurrentReadOp();
        // If it's a tap ack there is no response

        ByteBuffer rbuf = qa.getRbuf();
        final SocketChannel channel = qa.getChannel();
        int read = channel.read(rbuf);
        metrics.updateHistogram(OVERALL_AVG_BYTES_READ_METRIC, read);
        if (read < 0) {
            // our model is to keep the connection alive for future ops
            // so we'll queue a reconnect if disconnected via an IOException
            throw new IOException("Disconnected unexpected, will reconnect.");
        }
        while (read > 0) {
            getLogger().debug("Read %d bytes", read);
            rbuf.flip();
            while (rbuf.remaining() > 0) {
                if (currentOp == null) {
                    throw new IllegalStateException("No read operation.");
                }
                synchronized (currentOp) {
                    currentOp.readFromBuffer(rbuf);
                    if (currentOp.getState() == OperationState.COMPLETE) {
                        long timeOnWire =
                                System.nanoTime()
                                        - currentOp.getWriteCompleteTimestamp();
                        metrics.updateHistogram(
                                OVERALL_AVG_TIME_ON_WIRE_METRIC,
                                (int) (timeOnWire / 1000));
                        getLogger().debug(
                                "Completed read op: %s and giving the next %d "
                                        + "bytes", currentOp, rbuf.remaining());
                        Operation op = qa.removeCurrentReadOp();
                        assert op == currentOp : "Expected to pop " + currentOp
                                + " got " + op;
                        metrics.markMeter(OVERALL_RESPONSE_METRIC);
                        if (op.hasErrored()) {
                            metrics.markMeter(OVERALL_RESPONSE_FAIL_METRIC);
                        } else {
                            metrics.markMeter(OVERALL_RESPONSE_SUCC_METRIC);
                        }
                    }
                }
                currentOp = qa.getCurrentReadOp();
            }
            rbuf.clear();
            read = channel.read(rbuf);
            qa.completedRead();
        }
    }

    // Make a debug string out of the given buffer's values
    static String dbgBuffer(ByteBuffer b, int size) {
        StringBuilder sb = new StringBuilder();
        byte[] bytes = b.array();
        for (int i = 0; i < size; i++) {
            char ch = (char) bytes[i];
            if (Character.isWhitespace(ch) || Character.isLetterOrDigit(ch)) {
                sb.append(ch);
            } else {
                sb.append("\\x");
                sb.append(Integer.toHexString(bytes[i] & 0xff));
            }
        }
        return sb.toString();
    }

    protected void queueReconnect(RedisNode qa) {
        if (!shutDown) {
            getLogger().warn("Closing, and reopening %s, attempt %d.", qa,
                    qa.getReconnectCount());
            if (qa.getSk() != null) {
                qa.getSk().cancel();
                assert !qa.getSk().isValid() : "Cancelled selection key is valid";
            }
            qa.reconnecting();
            try {
                if (qa.getChannel() != null && qa.getChannel().socket() != null) {
                    qa.getChannel().socket().close();
                } else {
                    getLogger().info("The channel or socket was null for %s",
                            qa);
                }
            } catch (IOException e) {
                getLogger().warn("IOException trying to close a socket", e);
            }
            qa.setChannel(null);

            long delay =
                    (long) Math.min(maxDelay,
                            Math.pow(2, qa.getReconnectCount())) * 1000;
            long reconTime = System.currentTimeMillis() + delay;

            // Avoid potential condition where two connections are scheduled
            // for reconnect at the exact same time. This is expected to be
            // a rare situation.
            while (reconnectQueue.containsKey(reconTime)) {
                reconTime++;
            }

            reconnectQueue.put(reconTime, qa);
            metrics.incrementCounter(RECON_QUEUE_METRIC);

            // Need to do a little queue management.
            qa.setupResend();

            if (failureMode == FailureMode.Redistribute) {
                redistributeOperations(qa.destroyInputQueue());
            } else if (failureMode == FailureMode.Cancel) {
                cancelOperations(qa.destroyInputQueue());
            }
        }
    }

    private void cancelOperations(Collection<Operation> ops) {
        for (Operation op : ops) {
            op.cancel();
        }
    }

    private void redistributeOperations(Collection<Operation> ops) {
        /*
         * for (Operation op : ops) { if (op.isCancelled() || op.isTimedOut()) {
         * continue; } if (op instanceof KeyedOperation) { KeyedOperation ko =
         * (KeyedOperation) op; int added = 0; for (String k : ko.getKeys()) {
         * for (Operation newop : opFact.clone(ko)) { addOperation(k, newop);
         * added++; } } assert added > 0 :
         * "Didn't add any new operations when redistributing"; } else { //
         * Cancel things that don't have definite targets. op.cancel(); } }
         */
    }

    private void attemptReconnects() throws IOException {
        final long now = System.currentTimeMillis();
        final Map<RedisNode, Boolean> seen =
                new IdentityHashMap<RedisNode, Boolean>();
        final List<RedisNode> rereQueue = new ArrayList<RedisNode>();
        SocketChannel ch = null;
        for (Iterator<RedisNode> i =
                reconnectQueue.headMap(now).values().iterator(); i.hasNext();) {
            final RedisNode qa = i.next();
            i.remove();
            metrics.decrementCounter(RECON_QUEUE_METRIC);
            try {
                if (!belongsToCluster(qa)) {
                    getLogger().debug(
                            "Node does not belong to cluster anymore, "
                                    + "skipping reconnect: %s", qa);
                    continue;
                }
                if (!seen.containsKey(qa)) {
                    seen.put(qa, Boolean.TRUE);
                    getLogger().info("Reconnecting %s", qa);
                    ch = SocketChannel.open();
                    ch.configureBlocking(false);
                    int ops = 0;
                    if (ch.connect(qa.getSocketAddress())) {
                        connected(qa);
                        addedQueue.offer(qa);
                        getLogger().info("Immediately reconnected to %s", qa);
                        assert ch.isConnected();
                    } else {
                        ops = SelectionKey.OP_CONNECT;
                    }
                    qa.registerChannel(ch, ch.register(selector, ops, qa));
                    assert qa.getChannel() == ch : "Channel was lost.";
                } else {
                    getLogger().debug(
                            "Skipping duplicate reconnect request for %s", qa);
                }
            } catch (SocketException e) {
                getLogger().warn("Error on reconnect", e);
                rereQueue.add(qa);
            } catch (Exception e) {
                getLogger()
                        .error("Exception on reconnect, lost node %s", qa, e);
            } finally {
                // it's possible that above code will leak file descriptors
                // under
                // abnormal
                // conditions (when ch.open() fails and throws IOException.
                // always close non connected channel
                if (ch != null && !ch.isConnected()
                        && !ch.isConnectionPending()) {
                    try {
                        ch.close();
                    } catch (IOException x) {
                        getLogger().error("Exception closing channel: %s", qa,
                                x);
                    }
                }
            }
        }
        // Requeue any fast-failed connects.
        for (RedisNode n : rereQueue) {
            queueReconnect(n);
        }
    }

    /**
     * Get the node locator used by this connection.
     */
    public NodeLocator getLocator() {
        return locator;
    }

    public void enqueueOperation(String key, Operation o) {
        checkState();
        addOperation(key, o);
    }

    /**
     * Add an operation to the given connection.
     *
     * @param key
     *            the key the operation is operating upon
     * @param o
     *            the operation
     */
    protected void addOperation(final String key, final Operation o) {

        RedisNode placeIn = null;
        RedisNode primary =
              locator.getPrimary(key);
        if (primary.isActive() || failureMode == FailureMode.Retry) {
            placeIn = primary;
        } else if (failureMode == FailureMode.Cancel) {
            o.cancel();
        } else {
            // Look for another node in sequence that is ready.
            for (Iterator<RedisNode> i = locator.getSequence(key); placeIn == null
                    && i.hasNext();) {
                RedisNode n = i.next();
                if (n.isActive()) {
                    placeIn = n;
                }
            }
            // If we didn't find an active node, queue it in the primary node
            // and wait for it to come back online.
            if (placeIn == null) {
                placeIn = primary;
                this.getLogger()
                        .warn("Could not redistribute "
                                + "to another node, retrying primary node for %s.",
                                key);
            }
        }

        assert o.isCancelled() || placeIn != null : "No node found for key "
                + key;
        if (placeIn != null) {
            addOperation(placeIn, o);
        } else {
            assert o.isCancelled() : "No node found for " + key
                    + " (and not immediately cancelled)";
        }
    }

    public void addWriteOperation(final Operation o) {
        RedisNode placeIn = null;
        RedisNode primary = locator.getWrite();
        if (primary.isActive() || failureMode == FailureMode.Retry) {
            placeIn = primary;
        } else if (failureMode == FailureMode.Cancel) {
            o.cancel();
        }
        assert o.isCancelled() || placeIn != null : "No node found to write";

        if (placeIn != null) {
            addOperation(placeIn, o);
        } else {
            assert o.isCancelled() : "No node found for write operation (and not immediately cancelled)";
        }
    }

    public void insertOperation(final RedisNode node, final Operation o) {
        o.setHandlingNode(node);
        o.initialize();
        node.insertOp(o);
        addedQueue.offer(node);
        metrics.markMeter(OVERALL_REQUEST_METRIC);
        Selector s = selector.wakeup();
        assert s == selector : "Wakeup returned the wrong selector.";
        getLogger().debug("Added %s to %s", o, node);
    }

    protected void addOperation(final RedisNode node, final Operation o) {
        o.setHandlingNode(node);
        o.initialize();
        node.addOp(o);
        addedQueue.offer(node);
        metrics.markMeter(OVERALL_REQUEST_METRIC);
        Selector s = selector.wakeup();
        assert s == selector : "Wakeup returned the wrong selector.";
        getLogger().debug("Added %s to %s", o, node);
    }

    public void addOperations(final Map<RedisNode, Operation> ops) {

        for (Map.Entry<RedisNode, Operation> me : ops.entrySet()) {
            final RedisNode node = me.getKey();
            Operation o = me.getValue();
            o.setHandlingNode(node);
            o.initialize();
            node.addOp(o);
            addedQueue.offer(node);
            metrics.markMeter(OVERALL_REQUEST_METRIC);
        }
        Selector s = selector.wakeup();
        assert s == selector : "Wakeup returned the wrong selector.";
    }

    /**
     * Broadcast an operation to all nodes.
     */
    public CountDownLatch broadcastOperation(BroadcastOpFactory of) {
        return broadcastOperation(of, locator.getAll());
    }

    /**
     * Broadcast an operation to a specific collection of nodes.
     */
    public CountDownLatch broadcastOperation(final BroadcastOpFactory of,
            Collection<RedisNode> nodes) {
        final CountDownLatch latch = new CountDownLatch(nodes.size());
        for (RedisNode node : nodes) {
            getLogger().debug("broadcast Operation: node = " + node);
            Operation op = of.newOp(node, latch);
            op.initialize();
            node.addOp(op);
            op.setHandlingNode(node);
            addedQueue.offer(node);
            metrics.markMeter(OVERALL_REQUEST_METRIC);
        }
        Selector s = selector.wakeup();
        assert s == selector : "Wakeup returned the wrong selector.";
        return latch;
    }

    /**
     * Shut down all of the connections.
     */
    public void shutdown() throws IOException {
        shutDown = true;
        Selector s = selector.wakeup();
        assert s == selector : "Wakeup returned the wrong selector.";
        for (RedisNode qa : locator.getAll()) {
            if (qa.getChannel() != null) {
                qa.getChannel().close();
                qa.setSk(null);
                if (qa.getBytesRemainingToWrite() > 0) {
                    getLogger().warn(
                            "Shut down with %d bytes remaining to write",
                            qa.getBytesRemainingToWrite());
                }
                getLogger().debug("Shut down channel %s", qa.getChannel());
            }
        }
        running = false;
        selector.close();
        getLogger().debug("Shut down selector %s", selector);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{MemcachedConnection to");
        for (RedisNode qa : locator.getAll()) {
            sb.append(" ");
            sb.append(qa.getSocketAddress());
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Get information about connections and their active status.
     */
    public String connectionsStatus() {
        StringBuilder connStatus = new StringBuilder();
        connStatus.append("Connection Status {");
        for (RedisNode node : locator.getAll()) {
            connStatus.append(" ");
            connStatus
                    .append(node.getSocketAddress())
                    .append(" active: ")
                    .append(node.isActive())
                    .append(MessageFormat.format(", last read: {0} ms ago",
                            node.lastReadDelta()));
        }

        connStatus.append(" }");
        return connStatus.toString();
    }

    /**
     * helper method: increase timeout count on node attached to this op.
     *
     * @param op
     */
    public static void opTimedOut(Operation op) {
        RedisConnection.setTimeout(op, true);
    }

    /**
     * helper method: reset timeout counter.
     *
     * @param op
     */
    public static void opSucceeded(Operation op) {
        RedisConnection.setTimeout(op, false);
    }

    /**
     * helper method: do some error checking and set timeout boolean.
     *
     * @param op
     * @param isTimeout
     */
    private static void setTimeout(Operation op, boolean isTimeout) {
        try {
            if (op == null || op.isTimedOutUnsent()) {
                return; // op may be null in some cases, e.g. flush
            }
            RedisNode node = op.getHandlingNode();
            if (node == null) {
                LoggerFactory.getLogger(RedisConnection.class).warn(
                        "handling node for operation is not set");
            } else {
                node.setContinuousTimeout(isTimeout);
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(RedisConnection.class)
                    .error(e.getMessage());
        }
    }

    /**
     * Check to see if this connection is shutting down.
     */
    protected void checkState() {
        if (shutDown) {
            throw new IllegalStateException("Shutting down");
        }
        assert isAlive() : "IO Thread is not running.";
    }

    /**
     * Infinitely loop processing IO.
     */
    @Override
    public void run() {
        while (running) {
            try {
                handleIO();
            } catch (IOException e) {
                logRunException(e);
            } catch (CancelledKeyException e) {
                logRunException(e);
            } catch (ClosedSelectorException e) {
                logRunException(e);
            } catch (IllegalStateException e) {
                logRunException(e);
            } catch (ConcurrentModificationException e) {
                logRunException(e);
            }
        }
        getLogger().info("Shut down memcached client");
    }

    private void logRunException(Exception e) {
        if (shutDown) {
            // There are a couple types of errors that occur during the
            // shutdown sequence that are considered OK. Log at debug.
            getLogger().debug("Exception occurred during shutdown", e);
        } else {
            getLogger().warn("Problem handling memcached IO", e);
        }
    }

}
