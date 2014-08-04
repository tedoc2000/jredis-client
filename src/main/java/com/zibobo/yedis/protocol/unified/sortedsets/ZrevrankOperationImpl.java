package com.zibobo.yedis.protocol.unified.sortedsets;

import com.zibobo.yedis.ops.NullableLongReplyCallback;
import com.zibobo.yedis.ops.sortedsets.ZrevrankOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.OperationImpl;
import com.zibobo.yedis.protocol.unified.response.BulkReply;
import com.zibobo.yedis.protocol.unified.response.IntegerReply;

public class ZrevrankOperationImpl extends OperationImpl implements
        ZrevrankOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand(
            "ZREVRANK", 2);

    private final byte[] key;
    private final byte[] member;
    private final NullableLongReplyCallback cb;

    public ZrevrankOperationImpl(byte[] key, byte[] member,
            NullableLongReplyCallback cb) {
        super(cb);
        this.key = key;
        this.member = member;
        this.cb = cb;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, member);
    }

    @Override
    protected void handleIntegerReply(IntegerReply reply) {
        cb.onReply(reply.reply);
    }

    @Override
    protected void handleBulkReply(BulkReply reply) {
        cb.onNull();
    }

}
