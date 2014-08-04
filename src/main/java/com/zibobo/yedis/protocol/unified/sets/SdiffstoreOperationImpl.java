package com.zibobo.yedis.protocol.unified.sets;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.sets.SdiffstoreOperation;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class SdiffstoreOperationImpl extends LongReplyOperationImpl
        implements SdiffstoreOperation {
    private static final VarArgsCommand CMD = new VarArgsCommand("SDIFFSTORE");
    private final byte[] destination;
    private final byte[][] keys;

    public SdiffstoreOperationImpl(byte[] destination, byte[][] keys,
            LongReplyCallback cb) {
        super(cb);
        this.destination = destination;
        this.keys = keys;
    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, keys, destination);
    }

}
