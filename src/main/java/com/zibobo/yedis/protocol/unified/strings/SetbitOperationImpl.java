package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.strings.SetbitOperation;
import com.zibobo.yedis.protocol.unified.BooleanReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.util.LongToBytesUtils;

public class SetbitOperationImpl extends BooleanReplyOperationImpl implements
        SetbitOperation {
    private final FixedArgsCommand CMD = new FixedArgsCommand("SETBIT", 2);

    private final byte[] key;
    private final long offset;

    public SetbitOperationImpl(byte[] key, long offset, BooleanReplyCallback cb) {
        super(cb);
        this.key = key;
        this.offset = offset;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, LongToBytesUtils.toBytes(offset));
    }

}
