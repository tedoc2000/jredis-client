package com.zibobo.yedis.internal;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import net.spy.memcached.compat.SpyObject;

import com.zibobo.yedis.RedisPipeline;

public class PipelineFuture<T> extends SpyObject implements Future<T> {

    private final Future<T> future;

    private AtomicLong timeout = null;
    private final RedisPipeline pipeline;

    public PipelineFuture(Future<T> future, RedisPipeline pipeline) {
        this.future = future;
        this.pipeline = pipeline;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    public void setTimeout(AtomicLong timeout) {
        this.timeout = timeout;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        pipeline.execute();
        long useTimeout = timeout.get();
        getLogger().debug(String.format("Waiting for timeout: %s", useTimeout));
        try {
            return get(useTimeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException("Timed out waiting for operation", e);
        }
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        pipeline.execute();
        return future.get(timeout, unit);
    }

}
