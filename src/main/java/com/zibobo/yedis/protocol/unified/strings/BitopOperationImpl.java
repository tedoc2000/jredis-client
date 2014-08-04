package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.IntegerReplyCallback;
import com.zibobo.yedis.ops.strings.BitopOperation;
import com.zibobo.yedis.protocol.unified.IntReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class BitopOperationImpl extends IntReplyOperationImpl implements
        BitopOperation {

    private static final VarArgsCommand CMD = new VarArgsCommand("BITOP");
    private final byte[] op;
    private final byte[] dstKey;
    private final byte[][] srcKeys;

    public BitopOperationImpl(byte[] op, byte[] dstKey, byte[][] srcKeys,
            IntegerReplyCallback cb) {
        super(cb);
        this.op = op;
        this.dstKey = dstKey;
        this.srcKeys = srcKeys;

    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, srcKeys, op, dstKey);
    }

}
