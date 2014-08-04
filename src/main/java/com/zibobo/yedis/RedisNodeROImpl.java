/**
 * Copyright (C) 2006-2009 Dustin Sallings
 * Copyright (C) 2009-2011 Couchbase, Inc.
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
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Collection;

import com.zibobo.yedis.ops.Operation;

class RedisNodeROImpl implements RedisNode {

    private final RedisNode root;

    public RedisNodeROImpl(RedisNode n) {
        super();
        root = n;
    }

    @Override
    public String toString() {
        return root.toString();
    }

    @Override
    public void addOp(Operation op) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertOp(Operation op) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void connected() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copyInputQueue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fillWriteBuffer(boolean optimizeGets) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fixupOps() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBytesRemainingToWrite() {
        return root.getBytesRemainingToWrite();
    }

    @Override
    public SocketChannel getChannel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Operation getCurrentReadOp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Operation getCurrentWriteOp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuffer getRbuf() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getReconnectCount() {
        return root.getReconnectCount();
    }

    @Override
    public int getSelectionOps() {
        return root.getSelectionOps();
    }

    @Override
    public SelectionKey getSk() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SocketAddress getSocketAddress() {
        return root.getSocketAddress();
    }

    @Override
    public ByteBuffer getWbuf() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasReadOp() {
        return root.hasReadOp();
    }

    @Override
    public boolean hasWriteOp() {
        return root.hasReadOp();
    }

    @Override
    public boolean isActive() {
        return root.isActive();
    }

    @Override
    public void reconnecting() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerChannel(SocketChannel ch, SelectionKey selectionKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Operation removeCurrentReadOp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Operation removeCurrentWriteOp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChannel(SocketChannel to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSk(SelectionKey to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setupResend() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transitionWriteItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int writeSome() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Operation> destroyInputQueue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getContinuousTimeout() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContinuousTimeout(boolean isIncrease) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long lastReadDelta() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void completedRead() {
        throw new UnsupportedOperationException();
    }

}
