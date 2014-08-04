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
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import net.spy.memcached.metrics.MetricCollector;
import net.spy.memcached.metrics.MetricType;

import com.zibobo.yedis.ops.Operation;

/**
 * Factory for creating instances of RedisConnection. This is used to provide
 * more fine-grained configuration of connections.
 */
public interface ConnectionFactory {

    /**
     * Create a RedisConnection for the given SocketAddresses.
     * @param writeAddr TODO
     * @param addrs
     *            the addresses of the redis servers
     *
     * @return a new RedisConnection connected to those addresses
     * @throws IOException
     *             for problems initializing the redis connections
     */
    RedisConnection createConnection(InetSocketAddress writeAddr, List<InetSocketAddress> addrs)
            throws IOException;

    /**
     * Create a new redis node.
     */
    RedisNode createRedisNode(SocketAddress sa, SocketChannel c, int bufSize);

    /**
     * Create a BlockingQueue for operations for a connection.
     */
    BlockingQueue<Operation> createOperationQueue();

    /**
     * Create a BlockingQueue for the operations currently expecting to read
     * responses from redis.
     */
    BlockingQueue<Operation> createReadOperationQueue();

    /**
     * Create a BlockingQueue for the operations currently expecting to write
     * requests to redis.
     */
    BlockingQueue<Operation> createWriteOperationQueue();

    /**
     * Get the maximum amount of time (in milliseconds) a client is willing to
     * wait to add a new item to a queue.
     */
    long getOpQueueMaxBlockTime();

    /**
     * Get the ExecutorService which is used to asynchronously execute listeners
     * on futures.
     */
    ExecutorService getListenerExecutorService();

    /**
     * Create a NodeLocator instance for the given list of nodes.
     * @param writeNode TODO
     */
    NodeLocator createLocator(RedisNode writeNode, List<RedisNode> nodes);

    /**
     * Get the operation timeout used by this connection.
     */
    long getOperationTimeout();

    /**
     * If true, the IO thread should be a daemon thread.
     */
    boolean isDaemon();

    /**
     * If true, the nagle algorithm will be used on connected sockets.
     *
     * <p>
     * See {@link java.net.Socket#setTcpNoDelay(boolean)} for more information.
     * </p>
     */
    boolean useNagleAlgorithm();

    /**
     * Get the default failure mode for the underlying connection.
     */
    FailureMode getFailureMode();

    /*
     * Get the read buffer size set at construct time.
     */
    int getReadBufSize();

    /**
     * Get the hash algorithm to be used.
     */
    HashAlgorithm getHashAlg();

    /**
     * Maximum number of milliseconds to wait between reconnect attempts.
     */
    long getMaxReconnectDelay();

    /**
     * Maximum number of timeout exception for shutdown connection.
     */
    int getTimeoutExceptionThreshold();

    /**
     * If true, metric collections are enabled.
     */
    MetricType enableMetrics();

    /**
     * The currently active {@link MetricCollector}.
     */
    MetricCollector getMetricCollector();
}
