package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.StringReplyCallback;
import com.zibobo.yedis.ops.keys.RandomkeyOperation;
import com.zibobo.yedis.protocol.unified.NoArgsCommand;
import com.zibobo.yedis.protocol.unified.StringReplyOperationImpl;

public class RandomkeyOperationImpl extends StringReplyOperationImpl implements
        RandomkeyOperation {
    private static final NoArgsCommand CMD = new NoArgsCommand("RANDOMKEY");

    public RandomkeyOperationImpl(StringReplyCallback cb) {
        super(cb);
    }

    @Override
    public void initialize() {
        setArguments(CMD);

    }

}
