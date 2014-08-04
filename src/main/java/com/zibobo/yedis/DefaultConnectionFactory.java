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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.compat.SpyObject;
import net.spy.memcached.metrics.DefaultMetricCollector;
import net.spy.memcached.metrics.MetricCollector;
import net.spy.memcached.metrics.MetricType;
import net.spy.memcached.metrics.NoopMetricCollector;

import com.zibobo.yedis.ops.Operation;
import com.zibobo.yedis.protocol.unified.UnifiedOperationFactory;
import com.zibobo.yedis.protocol.unified.UnifiedRedisNodeImpl;

/**
 * Default implementation of ConnectionFactory.
 *
 * <p>
 * This implementation creates connections where the operation queue is an
 * ArrayBlockingQueue and the read and write queues are unbounded
 * LinkedBlockingQueues. The <code>Redistribute</code> FailureMode is always
 * used. If other FailureModes are needed, look at the ConnectionFactoryBuilder.
 *
 * </p>
 */
public class DefaultConnectionFactory extends SpyObject implements
        ConnectionFactory {

    /**
     * Default failure mode.
     */
    public static final FailureMode DEFAULT_FAILURE_MODE =
            FailureMode.Redistribute;

    /**
     * Default hash algorithm.
     */
    public static final HashAlgorithm DEFAULT_HASH =
            DefaultHashAlgorithm.NATIVE_HASH;

    /**
     * Maximum length of the operation queue returned by this connection
     * factory.
     */
    public static final int DEFAULT_OP_QUEUE_LEN = 16384;

    /**
     * The maximum time to block waiting for op queue operations to complete, in
     * milliseconds. The default has been set with the expectation that most
     * requests are interactive and waiting for more than a few seconds is thus
     * more undesirable than failing the request.
     */
    public static final long DEFAULT_OP_QUEUE_MAX_BLOCK_TIME = TimeUnit.SECONDS
            .toMillis(10);

    /**
     * The read buffer size for each server connection from this factory.
     */
    public static final int DEFAULT_READ_BUFFER_SIZE = 16384;

    /**
     * Default operation timeout in milliseconds.
     */
    public static final long DEFAULT_OPERATION_TIMEOUT = 2500;

    /**
     * Maximum amount of time (in seconds) to wait between reconnect attempts.
     */
    public static final long DEFAULT_MAX_RECONNECT_DELAY = 30;

    /**
     * Maximum number + 2 of timeout exception for shutdown connection.
     */
    public static final int DEFAULT_MAX_TIMEOUTEXCEPTION_THRESHOLD = 998;

    /**
     * Turn off metric collection by default.
     */
    public static final MetricType DEFAULT_METRIC_TYPE = MetricType.OFF;

    /**
     * The ExecutorService in which the listener callbacks will be executed.
     */
    public static final ExecutorService DEFAULT_LISTENER_EXECUTOR_SERVICE =
            Executors.newFixedThreadPool(Runtime.getRuntime()
                    .availableProcessors());

    protected final int opQueueLen;
    private final int readBufSize;
    private final HashAlgorithm hashAlg;

    private MetricCollector metrics;

    /**
     * Construct a DefaultConnectionFactory with the given parameters.
     *
     * @param qLen
     *            the queue length.
     * @param bufSize
     *            the buffer size
     * @param hash
     *            the algorithm to use for hashing
     */
    public DefaultConnectionFactory(int qLen, int bufSize, HashAlgorithm hash) {
        super();
        opQueueLen = qLen;
        readBufSize = bufSize;
        hashAlg = hash;
        metrics = null;
    }

    /**
     * Create a DefaultConnectionFactory with the given maximum operation queue
     * length, and the given read buffer size.
     */
    public DefaultConnectionFactory(int qLen, int bufSize) {
        this(qLen, bufSize, DEFAULT_HASH);
    }

    /**
     * Create a DefaultConnectionFactory with the default parameters.
     */
    public DefaultConnectionFactory() {
        this(DEFAULT_OP_QUEUE_LEN, DEFAULT_READ_BUFFER_SIZE);
    }

    @Override
    public RedisNode createRedisNode(SocketAddress sa, SocketChannel c,
            int bufSize) {

        return new UnifiedRedisNodeImpl(sa, c, bufSize,
                createReadOperationQueue(), createWriteOperationQueue(),
                createOperationQueue(), getOpQueueMaxBlockTime(),
                getOperationTimeout());
    }

    @Override
    public RedisConnection createConnection(InetSocketAddress writeAddr, List<InetSocketAddress> addrs)
            throws IOException {
        return new RedisConnection(getReadBufSize(), this, writeAddr, addrs,
                getFailureMode(), new UnifiedOperationFactory());
    }

    @Override
    public FailureMode getFailureMode() {
        return DEFAULT_FAILURE_MODE;
    }

    @Override
    public BlockingQueue<Operation> createOperationQueue() {
        return new ArrayBlockingQueue<Operation>(getOpQueueLen());
    }

    @Override
    public BlockingQueue<Operation> createReadOperationQueue() {
        return new LinkedBlockingQueue<Operation>();
    }

    @Override
    public BlockingQueue<Operation> createWriteOperationQueue() {
        return new LinkedBlockingQueue<Operation>();
    }

    @Override
    public NodeLocator createLocator(RedisNode writeNode, List<RedisNode> nodes) {
        return new ArrayModNodeLocator(writeNode, nodes, getHashAlg());
    }

    /**
     * Get the op queue length set at construct time.
     */
    public int getOpQueueLen() {
        return opQueueLen;
    }

    /**
     * @return the maximum time to block waiting for op queue operations to
     *         complete, in milliseconds, or null for no waiting.
     */
    @Override
    public long getOpQueueMaxBlockTime() {
        return DEFAULT_OP_QUEUE_MAX_BLOCK_TIME;
    }

    @Override
    public ExecutorService getListenerExecutorService() {
        return DEFAULT_LISTENER_EXECUTOR_SERVICE;
    }

    @Override
    public int getReadBufSize() {
        return readBufSize;
    }

    @Override
    public HashAlgorithm getHashAlg() {
        return hashAlg;
    }

    @Override
    public long getOperationTimeout() {
        return DEFAULT_OPERATION_TIMEOUT;
    }

    @Override
    public boolean isDaemon() {
        return false;
    }

    @Override
    public boolean useNagleAlgorithm() {
        return false;
    }

    @Override
    public long getMaxReconnectDelay() {
        return DEFAULT_MAX_RECONNECT_DELAY;
    }

    @Override
    public int getTimeoutExceptionThreshold() {
        return DEFAULT_MAX_TIMEOUTEXCEPTION_THRESHOLD;
    }

    @Override
    public MetricType enableMetrics() {
        String metricType = System.getProperty("com.zibobo.yedis.metrics.type");
        return metricType == null ? DEFAULT_METRIC_TYPE : MetricType
                .valueOf(metricType.toUpperCase());
    }

    @Override
    public MetricCollector getMetricCollector() {
        if (metrics != null) {
            return metrics;
        }

        String enableMetrics = System.getProperty("com.zibobo.yedis.metrics.enable");
        if (enableMetrics().equals(MetricType.OFF) || enableMetrics == "false") {
            getLogger().debug("Metric collection disabled.");
            metrics = new NoopMetricCollector();
        } else {
            getLogger().info(
                    "Metric collection enabled (Profile " + enableMetrics()
                            + ").");
            metrics = new DefaultMetricCollector();
        }

        return metrics;
    }

    protected String getName() {
        return "DefaultConnectionFactory";
    }

    @Override
    public String toString() {
        return "Failure Mode: " + getFailureMode().name()
                + ", Hash Algorithm: "
                + ((DefaultHashAlgorithm) getHashAlg()).name()
                + " Max Reconnect Delay: " + getMaxReconnectDelay()
                + ", Max Op Timeout: " + getOperationTimeout()
                + ", Op Queue Length: " + getOpQueueLen()
                + ", Op Max Queue Block Time" + getOpQueueMaxBlockTime()
                + ", Max Timeout Exception Threshold: "
                + getTimeoutExceptionThreshold() + ", Read Buffer Size: "
                + getReadBufSize() + ", isDaemon: " + isDaemon()
                + ", Using Nagle: " + useNagleAlgorithm()
                + ", ConnectionFactory: " + getName();
    }
}
