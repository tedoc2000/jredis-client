package com.zibobo.yedis.protocol.unified;

import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

import com.zibobo.yedis.ops.Operation;
import com.zibobo.yedis.protocol.TCPRedisNodeImpl;

public class UnifiedRedisNodeImpl extends TCPRedisNodeImpl {

    public UnifiedRedisNodeImpl(SocketAddress sa, SocketChannel c, int bufSize,
            BlockingQueue<Operation> rq, BlockingQueue<Operation> wq,
            BlockingQueue<Operation> iq, long opQueueMaxBlockTime, long dt) {
        super(sa, c, bufSize, rq, wq, iq, opQueueMaxBlockTime, dt);
    }

    @Override
    protected void optimize() {
        // TODO Auto-generated method stub

    }

}
