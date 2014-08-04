package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.keys.PexpireOperation;
import com.zibobo.yedis.protocol.unified.BooleanReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.util.LongToBytesUtils;

public class PexpireOperationImpl extends BooleanReplyOperationImpl implements
        PexpireOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("PEXPIRE",
            2);

    private final byte[] key;
    private final long ttl;

    public PexpireOperationImpl(byte[] key, long ttl, BooleanReplyCallback cb) {
        super(cb);
        this.key = key;
        this.ttl = ttl;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, LongToBytesUtils.toBytes(ttl));
    }

}
