package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.IntegerReplyCallback;
import com.zibobo.yedis.ops.strings.SetrangeOperation;
import com.zibobo.yedis.protocol.unified.ArgsCommand;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.IntReplyOperationImpl;
import com.zibobo.yedis.util.IntegerToBytesUtils;

public class SetrangeOperationImpl extends IntReplyOperationImpl implements
        SetrangeOperation {

    private static final ArgsCommand CMD = new FixedArgsCommand("SETRANGE", 3);

    private final byte[] key;
    private final int offset;
    private final byte[] data;

    public SetrangeOperationImpl(byte[] key, int offset, byte[] data,
            IntegerReplyCallback cb) {
        super(cb);
        this.key = key;
        this.offset = offset;
        this.data = data;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, IntegerToBytesUtils.toBytes(offset), data);
    }

}
