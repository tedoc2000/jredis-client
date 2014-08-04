package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.strings.BitcountOperation;
import com.zibobo.yedis.protocol.unified.ArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.OptionalArgsCommand;
import com.zibobo.yedis.util.IntegerToBytesUtils;

public class BitcountOperationImpl extends LongReplyOperationImpl implements
        BitcountOperation {

    private static final ArgsCommand CMD = new OptionalArgsCommand("BITCOUNT");

    private final byte[] key;
    private final Integer start;
    private final Integer end;

    public BitcountOperationImpl(byte[] key, Integer start, Integer end,
            LongReplyCallback cb) {
        super(cb);
        this.key = key;

        this.start = start;
        this.end = end;

    }

    @Override
    public void initialize() {
        if (start != null) {
            setArguments(CMD, key, IntegerToBytesUtils.toBytes(start),
                    IntegerToBytesUtils.toBytes(end));
        } else {
            setArguments(CMD, key);
        }

    }

}
