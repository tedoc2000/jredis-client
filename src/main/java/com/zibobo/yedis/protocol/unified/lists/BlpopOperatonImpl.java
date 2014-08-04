package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.BlockingPopReplyCallback;
import com.zibobo.yedis.ops.lists.BlpopOperation;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;
import com.zibobo.yedis.util.IntegerToBytesUtils;

public class BlpopOperatonImpl extends BlockingPopReplyOperationImpl implements
        BlpopOperation {

    private static final VarArgsCommand CMD = new VarArgsCommand("BLPOP");
    private final byte[][] keys;
    private final int timeout;

    public BlpopOperatonImpl(byte[][] keys, int timeout,
            BlockingPopReplyCallback cb) {
        super(cb);
        this.keys = keys;
        this.timeout = timeout;
    }

    @Override
    public void initialize() {
        setVargsPostArgsArguments(CMD, keys,
                IntegerToBytesUtils.toBytes(timeout));
    }

}
