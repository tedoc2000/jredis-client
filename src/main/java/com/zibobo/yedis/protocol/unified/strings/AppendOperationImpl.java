package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.IntegerReplyCallback;
import com.zibobo.yedis.ops.strings.AppendOperation;
import com.zibobo.yedis.protocol.unified.ArgsCommand;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.IntReplyOperationImpl;

public class AppendOperationImpl extends IntReplyOperationImpl implements
        AppendOperation {

    private static final ArgsCommand CMD = new FixedArgsCommand("APPEND", 2);

    private final byte[] key;
    private final byte[] data;

    public AppendOperationImpl(byte[] key, byte[] data, IntegerReplyCallback cb) {
        super(cb);
        this.key = key;
        this.data = data;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, data);
    }

}
