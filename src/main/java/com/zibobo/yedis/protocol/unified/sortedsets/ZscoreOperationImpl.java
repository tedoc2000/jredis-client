package com.zibobo.yedis.protocol.unified.sortedsets;

import com.zibobo.yedis.ops.NullableDoubleReplyCallback;
import com.zibobo.yedis.ops.sortedsets.ZscoreOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.OperationImpl;
import com.zibobo.yedis.protocol.unified.response.BulkReply;

public class ZscoreOperationImpl extends OperationImpl implements
        ZscoreOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("ZSCORE",
            2);

    private final byte[] key;
    private final byte[] member;
    private final NullableDoubleReplyCallback cb;

    public ZscoreOperationImpl(byte[] key, byte[] member,
            NullableDoubleReplyCallback cb) {
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
    protected void handleBulkReply(BulkReply reply) {
        if (reply == null) {
            cb.onNull();
        } else {
            cb.onReply(Double.parseDouble(new String(reply.reply, CHARSET)));
        }
    }

}
