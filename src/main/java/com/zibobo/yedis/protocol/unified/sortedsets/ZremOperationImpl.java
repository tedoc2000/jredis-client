package com.zibobo.yedis.protocol.unified.sortedsets;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.sortedsets.ZremOperation;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class ZremOperationImpl extends LongReplyOperationImpl implements
        ZremOperation {
    private final VarArgsCommand CMD = new VarArgsCommand("ZREM");
    private final byte[] key;
    private final byte[][] members;

    public ZremOperationImpl(byte[] key, byte[][] members, LongReplyCallback cb) {
        super(cb);
        this.key = key;
        this.members = members;
    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, members, key);
    }

}
