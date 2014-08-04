package com.zibobo.yedis.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.zibobo.yedis.ops.Operation;
import com.zibobo.yedis.ops.OperationStatus;

public class ListFuture<T> extends
        AbstractListenableFuture<List<T>, ListCompletionListener> implements
        Future<List<T>> {

    private final OperationFuture<List<Future<T>>> rv;

    public ListFuture(CountDownLatch l, long opTimeout, ExecutorService service) {
        super(service);
        this.rv =
                new OperationFuture<List<Future<T>>>(l, opTimeout, service);
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

    public void set(List<Future<T>> d, OperationStatus s) {
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
    public List<T> get() throws InterruptedException, ExecutionException {
        try {
            return get(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException("Timed out waiting forever", e);
        }
    }

    @Override
    public List<T> get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        List<Future<T>> futures = rv.get(timeout, unit);
        if (futures == null) {
            return null;
        }
        List<T> retVal = new ArrayList<T>(futures.size());
        for (Future<T> future : futures) {
            retVal.add(future.get());
        }
        return retVal;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListFuture<T> addListener(ListCompletionListener listener) {
        super.addToListeners((GenericCompletionListener<? extends Future<List<T>>>) listener);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListFuture<T> removeListener(ListCompletionListener listener) {
        super.removeFromListeners((GenericCompletionListener<? extends Future<List<T>>>) listener);
        return this;
    }

}
