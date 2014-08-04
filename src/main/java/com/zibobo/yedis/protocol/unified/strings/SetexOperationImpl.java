package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.OperationCallback;
import com.zibobo.yedis.ops.strings.SetexOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.IgnoreStatusReplyOperationImpl;
import com.zibobo.yedis.util.LongToBytesUtils;

public class SetexOperationImpl extends IgnoreStatusReplyOperationImpl
        implements SetexOperation {

    private static final FixedArgsCommand CMD =
            new FixedArgsCommand("SETEX", 3);

    private final byte[] key;
    private final byte[] value;
    private final long expire;

    public SetexOperationImpl(byte[] key, byte[] value, long expire,
            OperationCallback cb) {
        super(cb);
        this.key = key;
        this.value = value;
        this.expire = expire;

    }

    @Override
    public void initialize() {
        setArguments(CMD, key, value, LongToBytesUtils.toBytes(expire));
    }

}
