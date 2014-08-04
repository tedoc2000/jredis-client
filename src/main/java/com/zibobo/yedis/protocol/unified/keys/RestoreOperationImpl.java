package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.keys.RestoreOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.OKBooleanReplyOperationImpl;
import com.zibobo.yedis.util.LongToBytesUtils;

public class RestoreOperationImpl extends OKBooleanReplyOperationImpl implements
        RestoreOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand("RESTORE", 3);

    private final byte[] key;
    private final long ttl;
    private final byte[] data;

    public RestoreOperationImpl(byte[] key, long ttl, byte[] data,
            BooleanReplyCallback cb) {
        super(cb);
        this.key = key;
        this.ttl = ttl;
        this.data = data;
    }

    @Override
    public void initialize() {
       setArguments(CMD, key, LongToBytesUtils.toBytes(ttl), data);
    }

}
