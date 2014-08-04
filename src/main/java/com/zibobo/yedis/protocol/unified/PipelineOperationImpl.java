package com.zibobo.yedis.protocol.unified;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.zibobo.yedis.exception.RedisException;
import com.zibobo.yedis.ops.Operation;
import com.zibobo.yedis.ops.OperationCallback;
import com.zibobo.yedis.ops.OperationState;
import com.zibobo.yedis.ops.OperationStatus;
import com.zibobo.yedis.ops.PipelineOperation;
import com.zibobo.yedis.protocol.BaseOperationImpl;

public class PipelineOperationImpl extends BaseOperationImpl implements
        PipelineOperation {

    private final List<Operation> operations = new ArrayList<Operation>();

    private int currentReadIdx = 0;

    private final AtomicLong timeout = new AtomicLong();

    private long executeTime = 0;
    public PipelineOperationImpl() {
        super();
        setCallback(new OperationCallback() {

            @Override
            public void receivedStatus(OperationStatus status) {

            }

            @Override
            public void complete() {

            }
        });
    }

    @Override
    public void initialize() {
        ByteBuffer[] bbs = new ByteBuffer[operations.size()];
        int index = 0;
        int size = 0;
        for (Operation operation : operations) {
            operation.initialize();
            bbs[index++] = operation.getBuffer();
            operation.writeComplete();
            size += bbs[index - 1].limit();
        }
        ByteBuffer b = ByteBuffer.allocate(size);
        for (ByteBuffer bb : bbs) {
            b.put(bb);
        }
        b.flip();
        setBuffer(b);
    }

    @Override
    public void readFromBuffer(ByteBuffer data) throws RedisException {
        while (getState() != OperationState.COMPLETE && data.remaining() > 0) {
            Operation currOp = operations.get(currentReadIdx);
            currOp.readFromBuffer(data);
            if (currOp.getState() == OperationState.COMPLETE) {
                currentReadIdx++;
                if (currentReadIdx == operations.size()) {
                    transitionState(OperationState.COMPLETE);
                }
            }
        }
    }

    @Override
    public AtomicLong addOperation(Operation operation, long timeout) {
        operations.add(operation);
        if (timeout > this.timeout.get()) {
            this.timeout.set(timeout);
        }
        return this.timeout;
    }

    @Override
    public void execute() {
        executeTime = System.nanoTime();
    }

    @Override
    public synchronized boolean isTimedOut(long ttlMillis) {
        long elapsed = System.nanoTime();
        long ttlNanos = ttlMillis * 1000 * 1000;
        if (elapsed - executeTime > ttlNanos) {
            timeOut();
        }
        return isTimedOut();
    }


}
