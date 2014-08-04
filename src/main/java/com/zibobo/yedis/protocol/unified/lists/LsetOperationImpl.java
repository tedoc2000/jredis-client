package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.OperationCallback;
import com.zibobo.yedis.ops.lists.LsetOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.IgnoreStatusReplyOperationImpl;
import com.zibobo.yedis.util.LongToBytesUtils;

public class LsetOperationImpl extends IgnoreStatusReplyOperationImpl implements
        LsetOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand("LTRIM", 3);
    private final byte[] key;
    private final long index;
    private final byte[] value;

    public LsetOperationImpl(byte[] key, long index, byte[] value,
            OperationCallback cb) {
        super(cb);
        this.key = key;
        this.index = index;
        this.value = value;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, LongToBytesUtils.toBytes(index), value);
    }

}
