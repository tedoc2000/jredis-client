package com.zibobo.yedis.internal;

import java.util.concurrent.Future;

import com.zibobo.yedis.ops.Operation;

public class OpFuture<T> {

    public final Operation op;
    public final Future<T> future;
    public final long timeout;

    public OpFuture(Operation op, Future<T> future, long timeout) {
        this.op = op;
        this.future = future;
        this.timeout = timeout;
    }

}
