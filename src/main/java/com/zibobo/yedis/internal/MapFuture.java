package com.zibobo.yedis.internal;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.zibobo.yedis.ops.Operation;
import com.zibobo.yedis.ops.OperationStatus;

public class MapFuture<T> extends
        AbstractListenableFuture<Map<String, T>, MapCompletionListener>
        implements Future<Map<String, T>> {

    private final OperationFuture<Map<String, Future<T>>> rv;

    public MapFuture(CountDownLatch l, long opTimeout, ExecutorService service) {
        super(service);
        this.rv =
                new OperationFuture<Map<String, Future<T>>>(l, opTimeout, service);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        @SuppressWarnings("deprecation")
        boolean result = rv.cancel(mayInterruptIfRunning);
        notifyListeners();
        return result;
    }

    public OperationStatus getStatus() {
        return rv.getStatus();
    }

    public void set(Map<String, Future<T>> d, OperationStatus s) {
        rv.set(d, s);
        notifyListeners();
    }

    public void setOperation(Operation to) {
        rv.setOperation(to);
    }

    @Override
    public boolean isCancelled() {
        return rv.isCancelled();
    }

    @Override
    public boolean isDone() {
        return rv.isDone();
    }

    @Override
    public Map<String, T> get() throws InterruptedException, ExecutionException {
        try {
            return get(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException("Timed out waiting forever", e);
        }
    }

    @Override
    public Map<String, T> get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        Map<String, Future<T>> futures = rv.get(timeout, unit);
        if (futures == null) {
            return null;
        }
        Map<String, T> retVal = new LinkedHashMap<String, T>(futures.size());
        for (Map.Entry<String, Future<T>> future : futures.entrySet()) {
            retVal.put(future.getKey(), future.getValue().get());
        }
        return retVal;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MapFuture<T> addListener(MapCompletionListener listener) {
        super.addToListeners((GenericCompletionListener<? extends Future<Map<String, T>>>) listener);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MapFuture<T> removeListener(MapCompletionListener listener) {
        super.removeFromListeners((GenericCompletionListener<? extends Future<Map<String, T>>>) listener);
        return this;
    }

}
