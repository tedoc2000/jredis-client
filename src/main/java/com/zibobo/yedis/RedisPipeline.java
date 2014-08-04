package com.zibobo.yedis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.zibobo.yedis.internal.OpFuture;
import com.zibobo.yedis.internal.PipelineFuture;
import com.zibobo.yedis.ops.Operation;
import com.zibobo.yedis.ops.PipelineOperation;

public class RedisPipeline extends AsyncRedisOperator implements
        RedisPipelineIF {

    protected final AtomicBoolean executed = new AtomicBoolean();
    private final ConcurrentLinkedQueue<OperationInfo> ops;
    private final AtomicBoolean writeOp;

    public RedisPipeline(ConnectionFactory cf, RedisConnection rconn) {
        super(cf, rconn);
        this.ops = new ConcurrentLinkedQueue<OperationInfo>();
        this.writeOp = new AtomicBoolean();
    }

    protected void checkExecuted() {
        if (executed.get()) {
            throw new IllegalStateException("Pipeline already executed.");
        }
    }

    @Override
    protected <T> Future<T> addOperation(String key, OpFuture<T> opFuture) {
        PipelineFuture<T> future = new PipelineFuture<T>(opFuture.future, this);
        OperationInfo oi =
                new OperationInfo(key, opFuture.op, opFuture.timeout, future);
        ops.add(oi);
        return future;
    }

    @Override
    protected <T> Future<T> addWriteOperation(OpFuture<T> opFuture) {
        checkExecuted();
        writeOp.set(true);
        return addOperation(null, opFuture);
    }

    public void execute() {
        if (!executed.getAndSet(true)) {
            if (!writeOp.get()) {
                Map<RedisNode, PipelineOperation> operations =
                        new HashMap<RedisNode, PipelineOperation>();
                for (OperationInfo opInfo : ops) {
                    addToPipelineOp(findNode(opInfo.key), opInfo, operations);
                }
                for (Map.Entry<RedisNode, PipelineOperation> entry : operations
                        .entrySet()) {
                    PipelineOperation p = entry.getValue();
                    p.execute();
                    rconn.addOperation(entry.getKey(), p);
                }
            } else {
                RedisNode writeNode = rconn.getLocator().getWrite();
                PipelineOperation pipelineOp = opFact.pipeline();
                for (OperationInfo opInfo : ops) {
                    setUpPipelineOperation(writeNode, opInfo, pipelineOp);
                }
                pipelineOp.execute();
                rconn.addOperation(writeNode, pipelineOp);
            }
        }
    }

    protected void addToPipelineOp(RedisNode node, OperationInfo opInfo,
            Map<RedisNode, PipelineOperation> operations) {
        PipelineOperation pipelineOp = operations.get(node);
        if (pipelineOp == null) {
            pipelineOp = opFact.pipeline();
            operations.put(node, pipelineOp);
        }
        setUpPipelineOperation(node, opInfo, pipelineOp);
    }

    protected void setUpPipelineOperation(RedisNode node, OperationInfo opInfo,
            PipelineOperation pipelineOp) {
        opInfo.op.setHandlingNode(node);
        opInfo.future.setTimeout(pipelineOp.addOperation(opInfo.op,
                opInfo.timeout));
    }

    protected RedisNode findNode(String key) {
        RedisNode primaryNode = rconn.getLocator().getPrimary(key);
        RedisNode node = null;
        if (primaryNode.isActive()) {
            node = primaryNode;
        } else {
            for (Iterator<RedisNode> i = rconn.getLocator().getSequence(key); node == null
                    && i.hasNext();) {
                RedisNode n = i.next();
                if (n.isActive()) {
                    node = n;
                }
            }
            if (node == null) {
                node = primaryNode;
            }
        }
        assert node != null : "Didn't find a node for " + key;
        return node;
    }

    private static class OperationInfo {
        public final String key;
        public final Operation op;
        public final long timeout;
        public final PipelineFuture<?> future;

        public OperationInfo(String key, Operation op, long timeout,
                PipelineFuture<?> future) {
            this.key = key;
            this.op = op;
            this.timeout = timeout;
            this.future = future;
        }
    }
}
