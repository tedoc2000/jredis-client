package com.zibobo.yedis.protocol.unified.connection;

import com.zibobo.yedis.ops.StringReplyCallback;
import com.zibobo.yedis.ops.connection.PingOperation;
import com.zibobo.yedis.protocol.unified.ArgsCommand;
import com.zibobo.yedis.protocol.unified.NoArgsCommand;
import com.zibobo.yedis.protocol.unified.StringStatusReplyOperationImpl;

public class PingOperationImpl extends StringStatusReplyOperationImpl implements
        PingOperation {

    private static final ArgsCommand CMD = new NoArgsCommand("PING");

    public PingOperationImpl(StringReplyCallback cb) {
        super(cb);
    }

    @Override
    public void initialize() {
        setArguments(CMD);
    }

}
