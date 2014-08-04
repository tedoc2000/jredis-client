package com.zibobo.yedis.protocol.unified.connection;

import com.zibobo.yedis.ops.connection.AuthOperation;
import com.zibobo.yedis.protocol.unified.ArgsCommand;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.StatusReplyOperationImpl;

public class AuthOperationImpl extends StatusReplyOperationImpl implements
        AuthOperation {

    private static final ArgsCommand CMD = new FixedArgsCommand("AUTH", 1);

    private final AuthOperation.Callback cb;

    private final byte[] password;

    public AuthOperationImpl(byte[] password, Callback cb) {
        super(cb);
        this.cb = cb;
        this.password = password;
    }

    @Override
    public void initialize() {
        setArguments(CMD, password);
    }

    @Override
    protected void handleStatus(String status) {
        cb.authed();
    }

}
