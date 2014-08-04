package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.SortOptions;
import com.zibobo.yedis.ops.BytesListReplyCallback;
import com.zibobo.yedis.ops.keys.SortOperation;
import com.zibobo.yedis.protocol.unified.BytesListReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;
import com.zibobo.yedis.util.IntegerToBytesUtils;

public class SortOperationImpl extends BytesListReplyOperationImpl implements
        SortOperation {
    private static final byte[] ALPHA = toBytes("ALPHA");
    private static final byte[] DESC = toBytes("DESC");
    private static final byte[] LIMIT = toBytes("LIMIT");
    private static final byte[] BY = toBytes("BY");
    private static final byte[] GET = toBytes("GET");

    private static final VarArgsCommand CMD = new VarArgsCommand("SORT");
    private final byte[] key;
    private final boolean alpha;
    private final SortOptions options;

    public SortOperationImpl(byte[] key, boolean alpha, SortOptions options,
            BytesListReplyCallback cb) {
        super(cb);
        this.key = key;
        this.alpha = alpha;
        this.options = options;
    }

    @Override
    public void initialize() {
        int argCount = 0;
        if (alpha) {
            argCount++;
        }
        if (options.desc) {
            argCount++;
        }
        if (options.offset != null) {
            argCount += 3;
        }
        if (options.by != null) {
            argCount += 2;
        }
        if (options.get != null) {
            argCount += 2;
        } else if (options.multiGet != null) {
            argCount += options.multiGet.size() + 1;
        }

        byte[][] args = new byte[argCount][];
        int argc = 0;
        if (alpha) {
            args[argc++] = ALPHA;
        }
        if (options.desc) {
            args[argc++] = DESC;
        }
        if (options.offset != null) {
            args[argc++] = LIMIT;
            args[argc++] = IntegerToBytesUtils.toBytes(options.offset);
            args[argc++] = IntegerToBytesUtils.toBytes(options.count);
        }
        if (options.by != null) {
            args[argc++] = BY;
            args[argc++] = toBytes(options.by);
        }
        if (options.get != null) {
            args[argc++] = GET;
            args[argc++] = toBytes(options.get);
        } else if (options.multiGet != null) {
            args[argc++] = GET;
            for (String get : options.multiGet) {
                args[argc++] = toBytes(get);
            }
        }

        setVargsArguments(CMD, args, key);
    }

}
