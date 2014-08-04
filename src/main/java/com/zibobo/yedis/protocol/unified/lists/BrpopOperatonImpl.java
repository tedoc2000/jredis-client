package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.BlockingPopReplyCallback;
import com.zibobo.yedis.ops.lists.BrpopOperation;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;
import com.zibobo.yedis.util.IntegerToBytesUtils;

public class BrpopOperatonImpl extends BlockingPopReplyOperationImpl implements
        BrpopOperation {

    private static final VarArgsCommand CMD = new VarArgsCommand("BRPOP");
    private final byte[][] keys;
    private final int timeout;

    public BrpopOperatonImpl(byte[][] keys, int timeout,
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
