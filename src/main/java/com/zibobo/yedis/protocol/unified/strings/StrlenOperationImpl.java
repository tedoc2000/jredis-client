package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.IntegerReplyCallback;
import com.zibobo.yedis.ops.strings.StrlenOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.IntReplyOperationImpl;

public class StrlenOperationImpl extends IntReplyOperationImpl implements
        StrlenOperation {
    private final static FixedArgsCommand CMD = new FixedArgsCommand("STRLEN",
            1);

    private final byte[] key;

    public StrlenOperationImpl(byte[] key, IntegerReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
