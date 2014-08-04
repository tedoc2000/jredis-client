package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.BytesReplyCallback;
import com.zibobo.yedis.ops.strings.GetrangeOperation;
import com.zibobo.yedis.protocol.unified.BytesReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.util.IntegerToBytesUtils;

public class GetrangeOperationImpl extends BytesReplyOperationImpl implements
        GetrangeOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand(
            "GETRANGE", 3);

    private final byte[] key;
    private final int start;
    private final int end;

    public GetrangeOperationImpl(byte[] key, int start, int end,
            BytesReplyCallback cb) {
        super(cb);
        this.key = key;
        this.start = start;
        this.end = end;

    }

    @Override
    public void initialize() {
        setArguments(CMD, key, IntegerToBytesUtils.toBytes(start),
                IntegerToBytesUtils.toBytes(end));
    }

}
