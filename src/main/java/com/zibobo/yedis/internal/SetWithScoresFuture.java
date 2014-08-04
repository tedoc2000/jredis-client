package com.zibobo.yedis.internal;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.zibobo.yedis.SortedSetCreator;
import com.zibobo.yedis.SortedSetEntry;
import com.zibobo.yedis.ops.Operation;
import com.zibobo.yedis.ops.OperationStatus;

public class SetWithScoresFuture<T, U extends SortedSetEntry<T>> extends
        AbstractListenableFuture<Set<U>, SetWithScoresCompletionListener>
        implements Future<Set<U>> {

    private final OperationFuture<List<Future<T>>> rv;
    private final SortedSetCreator<T, U> creator;
    private List<Double> scores;

    public SetWithScoresFuture(CountDownLatch l, long opTimeout, ExecutorService service,
            SortedSetCreator<T, U> creator) {
        super(service);
        this.creator = creator;
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

    public void set(List<Future<T>> d, List<Double> scores, OperationStatus s) {
        this.scores = scores;
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
    public Set<U> get() throws InterruptedException, ExecutionException {
        try {
            return get(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException("Timed out waiting forever", e);
        }
    }

    @Override
    public Set<U> get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        List<Future<T>> futures = rv.get(timeout, unit);
        if (futures == null) {
            return null;
        }
        creator.create();
        if (!futures.isEmpty()) {
            Iterator<Double> scoreIter = scores.iterator();
            for (Future<T> future : futures) {
                creator.addElement(future.get(), scoreIter.next());
            }
        }
        return creator.finish();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SetWithScoresFuture<T, U> addListener(
            SetWithScoresCompletionListener listener) {
        super.addToListeners((GenericCompletionListener<? extends Future<Set<U>>>) listener);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SetWithScoresFuture<T, U> removeListener(
            SetWithScoresCompletionListener listener) {
        super.removeFromListeners((GenericCompletionListener<? extends Future<Set<U>>>) listener);
        return this;
    }

}
