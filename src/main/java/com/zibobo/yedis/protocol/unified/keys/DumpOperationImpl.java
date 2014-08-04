package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.BytesReplyCallback;
import com.zibobo.yedis.ops.keys.DumpOperation;
import com.zibobo.yedis.protocol.unified.BytesReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class DumpOperationImpl extends BytesReplyOperationImpl implements
        DumpOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("DUMP", 1);

    private final byte[] key;

    public DumpOperationImpl(byte[] key, BytesReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
