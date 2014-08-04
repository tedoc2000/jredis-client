package com.zibobo.yedis.protocol.unified.sortedsets;

import java.util.List;

import com.zibobo.yedis.BytesSortedSetEntry;
import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.sortedsets.ZaddOperation;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;
import com.zibobo.yedis.util.DoubleToBytesUtils;

public class ZaddOperationImpl extends LongReplyOperationImpl implements
        ZaddOperation {

    private static final VarArgsCommand CMD = new VarArgsCommand("ZADD");
    private final byte[] key;
    private final List<BytesSortedSetEntry> entries;

    public ZaddOperationImpl(byte[] key, List<BytesSortedSetEntry> entries,
            LongReplyCallback cb) {
        super(cb);
        this.key = key;
        this.entries = entries;
    }

    @Override
    public void initialize() {
        byte[][] varargs = new byte[entries.size() * 2][];
        int count = 0;
        for (BytesSortedSetEntry entry : entries) {
            varargs[count++] = DoubleToBytesUtils.toBytes(entry.score);
            varargs[count++] = entry.value;
        }
        setVargsArguments(CMD, varargs, key);
    }

}
