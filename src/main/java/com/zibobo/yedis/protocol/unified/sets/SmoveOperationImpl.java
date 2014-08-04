package com.zibobo.yedis.protocol.unified.sets;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.sets.SmoveOperation;
import com.zibobo.yedis.protocol.unified.ArgsCommand;
import com.zibobo.yedis.protocol.unified.BooleanReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class SmoveOperationImpl extends BooleanReplyOperationImpl implements
        SmoveOperation {

    private final ArgsCommand CMD = new FixedArgsCommand("SMOVE", 3);
    private final byte[] source;
    private final byte[] destination;
    private final byte[] value;

    public SmoveOperationImpl(byte[] source, byte[] destination, byte[] value,
            BooleanReplyCallback cb) {
        super(cb);
        this.source = source;
        this.destination = destination;
        this.value = value;
    }

    @Override
    public void initialize() {
        setArguments(CMD, source, destination, value);
    }

}
