package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.Exclusiveness;
import com.zibobo.yedis.ExpirationType;
import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.strings.SetOperation;
import com.zibobo.yedis.protocol.unified.OKBooleanReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.OptionalArgsCommand;
import com.zibobo.yedis.util.LongToBytesUtils;

public class SetOperationImpl extends OKBooleanReplyOperationImpl implements
        SetOperation {

    private static final OptionalArgsCommand CMD = new OptionalArgsCommand(
            "SET");

    private final byte[] key;
    private final byte[] value;
    private final Exclusiveness exclusiveness;
    private final ExpirationType expirationType;
    private final long expire;

    public SetOperationImpl(byte[] key, byte[] value,
            Exclusiveness exclusiveness, ExpirationType expirationType,
            long expire, BooleanReplyCallback cb) {
        super(cb);
        this.key = key;
        this.value = value;
        this.exclusiveness = exclusiveness;
        this.expirationType = expirationType;
        this.expire = expire;
    }

    @Override
    public void initialize() {
        if (exclusiveness != null && expirationType != null) {
            setArguments(CMD, key, value, exclusiveness.value,
                    expirationType.value, LongToBytesUtils.toBytes(expire));
        } else if (exclusiveness != null) {
            setArguments(CMD, key, value, exclusiveness.value);
        } else if (expirationType != null) {
            setArguments(CMD, key, value, expirationType.value,
                    LongToBytesUtils.toBytes(expire));
        } else {
            setArguments(CMD, key, value);
        }
    }

}
