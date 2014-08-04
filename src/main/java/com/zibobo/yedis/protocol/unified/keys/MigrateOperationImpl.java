package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.OperationCallback;
import com.zibobo.yedis.ops.keys.MigrateOperation;
import com.zibobo.yedis.protocol.unified.IgnoreStatusReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.OptionalArgsCommand;
import com.zibobo.yedis.util.IntegerToBytesUtils;

public class MigrateOperationImpl extends IgnoreStatusReplyOperationImpl
        implements MigrateOperation {
    private static final OptionalArgsCommand CMD = new OptionalArgsCommand(
            "MIGRATE");
    private static final byte[] COPY = toBytes("COPY");
    private static final byte[] REPLACE = toBytes("REPLACE");

    private final byte[] host;
    private final int port;
    private final byte[] key;
    private final int db;
    private final int timeout;
    private final boolean copy;
    private final boolean replace;

    public MigrateOperationImpl(byte[] host, int port, byte[] key, int db,
            int timeout, boolean copy, boolean replace, OperationCallback cb) {
        super(cb);
        this.host = host;
        this.port = port;
        this.key = key;
        this.db = db;
        this.timeout = timeout;
        this.copy = copy;
        this.replace = replace;
    }

    @Override
    public void initialize() {
        if (copy && replace) {
            setArguments(CMD, host, IntegerToBytesUtils.toBytes(port), key,
                    IntegerToBytesUtils.toBytes(db),
                    IntegerToBytesUtils.toBytes(timeout), COPY, REPLACE);
        } else if (replace) {
            setArguments(CMD, host, IntegerToBytesUtils.toBytes(port), key,
                    IntegerToBytesUtils.toBytes(db),
                    IntegerToBytesUtils.toBytes(timeout), REPLACE);
        } else if (copy) {
            setArguments(CMD, host, IntegerToBytesUtils.toBytes(port), key,
                    IntegerToBytesUtils.toBytes(db),
                    IntegerToBytesUtils.toBytes(timeout), COPY);
        } else {
            setArguments(CMD, host, IntegerToBytesUtils.toBytes(port), key,
                    IntegerToBytesUtils.toBytes(db),
                    IntegerToBytesUtils.toBytes(timeout));
        }
    }
}
