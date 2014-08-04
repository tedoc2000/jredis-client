package com.zibobo.yedis.protocol.unified.sets;

import com.zibobo.yedis.ops.BytesSetReplyCallback;
import com.zibobo.yedis.ops.sets.SrandmemberCountOperation;
import com.zibobo.yedis.protocol.unified.ArgsCommand;
import com.zibobo.yedis.protocol.unified.BytesSetReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.util.LongToBytesUtils;

public class SrandmemberCountOperationImpl extends BytesSetReplyOperationImpl
        implements SrandmemberCountOperation {

    private final ArgsCommand CMD = new FixedArgsCommand("SRANDMEMBER", 2);
    private final byte[] key;
    private final long count;

    public SrandmemberCountOperationImpl(byte[] key, long count,
            BytesSetReplyCallback cb) {
        super(cb);
        this.key = key;
        this.count = count;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, LongToBytesUtils.toBytes(count));
    }

}
