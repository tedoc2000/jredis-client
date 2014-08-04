package com.zibobo.yedis.protocol.unified;

import java.util.List;

import com.zibobo.yedis.Aggregation;
import com.zibobo.yedis.BytesSortedSetEntry;
import com.zibobo.yedis.Exclusiveness;
import com.zibobo.yedis.ExpirationType;
import com.zibobo.yedis.IntervalValue;
import com.zibobo.yedis.OperationFactory;
import com.zibobo.yedis.SortOptions;
import com.zibobo.yedis.ops.BlockingPopReplyCallback;
import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.BytesListReplyCallback;
import com.zibobo.yedis.ops.BytesReplyCallback;
import com.zibobo.yedis.ops.BytesSetReplyCallback;
import com.zibobo.yedis.ops.BytesSetWithScoresReplyCallback;
import com.zibobo.yedis.ops.DoubleReplyCallback;
import com.zibobo.yedis.ops.IntegerReplyCallback;
import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.NullableDoubleReplyCallback;
import com.zibobo.yedis.ops.NullableLongReplyCallback;
import com.zibobo.yedis.ops.OperationCallback;
import com.zibobo.yedis.ops.PipelineOperation;
import com.zibobo.yedis.ops.StringListReplyCallback;
import com.zibobo.yedis.ops.StringReplyCallback;
import com.zibobo.yedis.ops.connection.PingOperation;
import com.zibobo.yedis.ops.hashes.HdelOperation;
import com.zibobo.yedis.ops.hashes.HexistsOperation;
import com.zibobo.yedis.ops.hashes.HgetOperation;
import com.zibobo.yedis.ops.hashes.HgetallOperation;
import com.zibobo.yedis.ops.hashes.HincrByFloatOperation;
import com.zibobo.yedis.ops.hashes.HincrOperation;
import com.zibobo.yedis.ops.hashes.HkeysOperation;
import com.zibobo.yedis.ops.hashes.HlenOperation;
import com.zibobo.yedis.ops.hashes.HmgetOperation;
import com.zibobo.yedis.ops.hashes.HmsetOperation;
import com.zibobo.yedis.ops.hashes.HsetOperation;
import com.zibobo.yedis.ops.hashes.HsetnxOperation;
import com.zibobo.yedis.ops.hashes.HvalsOperation;
import com.zibobo.yedis.ops.keys.DelOperation;
import com.zibobo.yedis.ops.keys.DumpOperation;
import com.zibobo.yedis.ops.keys.ExistsOperation;
import com.zibobo.yedis.ops.keys.ExpireAtOperation;
import com.zibobo.yedis.ops.keys.ExpireOperation;
import com.zibobo.yedis.ops.keys.KeysOperation;
import com.zibobo.yedis.ops.keys.MigrateOperation;
import com.zibobo.yedis.ops.keys.MoveOperation;
import com.zibobo.yedis.ops.keys.ObjectIntegerOperation;
import com.zibobo.yedis.ops.keys.ObjectStringOperation;
import com.zibobo.yedis.ops.keys.PersistOperation;
import com.zibobo.yedis.ops.keys.PexpireAtOperation;
import com.zibobo.yedis.ops.keys.PexpireOperation;
import com.zibobo.yedis.ops.keys.PttlOperation;
import com.zibobo.yedis.ops.keys.RandomkeyOperation;
import com.zibobo.yedis.ops.keys.RenameOperation;
import com.zibobo.yedis.ops.keys.RenamenxOperation;
import com.zibobo.yedis.ops.keys.RestoreOperation;
import com.zibobo.yedis.ops.keys.SortOperation;
import com.zibobo.yedis.ops.keys.TtlOperation;
import com.zibobo.yedis.ops.keys.TypeOperation;
import com.zibobo.yedis.ops.lists.BlpopOperation;
import com.zibobo.yedis.ops.lists.BrpopOperation;
import com.zibobo.yedis.ops.lists.BrpoplpushOperation;
import com.zibobo.yedis.ops.lists.LindexOperation;
import com.zibobo.yedis.ops.lists.LinsertOperation;
import com.zibobo.yedis.ops.lists.LlenOperation;
import com.zibobo.yedis.ops.lists.LpopOperation;
import com.zibobo.yedis.ops.lists.LpushOperation;
import com.zibobo.yedis.ops.lists.LpushxOperation;
import com.zibobo.yedis.ops.lists.LrangeOperation;
import com.zibobo.yedis.ops.lists.LremOperation;
import com.zibobo.yedis.ops.lists.LsetOperation;
import com.zibobo.yedis.ops.lists.LtrimOperation;
import com.zibobo.yedis.ops.lists.RpopOperation;
import com.zibobo.yedis.ops.lists.RpoplpushOperation;
import com.zibobo.yedis.ops.lists.RpushOperation;
import com.zibobo.yedis.ops.lists.RpushxOperation;
import com.zibobo.yedis.ops.sets.SaddOperation;
import com.zibobo.yedis.ops.sets.ScardOperation;
import com.zibobo.yedis.ops.sets.SdiffOperation;
import com.zibobo.yedis.ops.sets.SdiffstoreOperation;
import com.zibobo.yedis.ops.sets.SinterOperation;
import com.zibobo.yedis.ops.sets.SinterstoreOperation;
import com.zibobo.yedis.ops.sets.SismembersOperation;
import com.zibobo.yedis.ops.sets.SmembersOperation;
import com.zibobo.yedis.ops.sets.SmoveOperation;
import com.zibobo.yedis.ops.sets.SpopOperation;
import com.zibobo.yedis.ops.sets.SrandmemberCountOperation;
import com.zibobo.yedis.ops.sets.SrandmemberOperation;
import com.zibobo.yedis.ops.sets.SremOperation;
import com.zibobo.yedis.ops.sets.SunionOperation;
import com.zibobo.yedis.ops.sets.SunionstoreOperation;
import com.zibobo.yedis.ops.sortedsets.ZaddOperation;
import com.zibobo.yedis.ops.sortedsets.ZcardOperation;
import com.zibobo.yedis.ops.sortedsets.ZcountOperation;
import com.zibobo.yedis.ops.sortedsets.ZincrbyOperation;
import com.zibobo.yedis.ops.sortedsets.ZinterstoreOperation;
import com.zibobo.yedis.ops.sortedsets.ZrangeOperation;
import com.zibobo.yedis.ops.sortedsets.ZrangeWithScoresOperation;
import com.zibobo.yedis.ops.sortedsets.ZrangebyscoreOperation;
import com.zibobo.yedis.ops.sortedsets.ZrangebyscoreWithScoresOperation;
import com.zibobo.yedis.ops.sortedsets.ZrankOperation;
import com.zibobo.yedis.ops.sortedsets.ZremOperation;
import com.zibobo.yedis.ops.sortedsets.ZremrangebyrankOperation;
import com.zibobo.yedis.ops.sortedsets.ZremrangebyscoreOperation;
import com.zibobo.yedis.ops.sortedsets.ZrevrangeOperation;
import com.zibobo.yedis.ops.sortedsets.ZrevrangeWithScoresOperation;
import com.zibobo.yedis.ops.sortedsets.ZrevrangebyscoreOperation;
import com.zibobo.yedis.ops.sortedsets.ZrevrangebyscoreWithScoresOperation;
import com.zibobo.yedis.ops.sortedsets.ZrevrankOperation;
import com.zibobo.yedis.ops.sortedsets.ZscoreOperation;
import com.zibobo.yedis.ops.sortedsets.ZunionstoreOperation;
import com.zibobo.yedis.ops.strings.AppendOperation;
import com.zibobo.yedis.ops.strings.BitcountOperation;
import com.zibobo.yedis.ops.strings.BitopOperation;
import com.zibobo.yedis.ops.strings.DecrOperation;
import com.zibobo.yedis.ops.strings.GetOperation;
import com.zibobo.yedis.ops.strings.GetbitOperation;
import com.zibobo.yedis.ops.strings.GetrangeOperation;
import com.zibobo.yedis.ops.strings.GetsetOperation;
import com.zibobo.yedis.ops.strings.IncrByFloatOperation;
import com.zibobo.yedis.ops.strings.IncrOperation;
import com.zibobo.yedis.ops.strings.MgetOperation;
import com.zibobo.yedis.ops.strings.MsetOperation;
import com.zibobo.yedis.ops.strings.MsetnxOperation;
import com.zibobo.yedis.ops.strings.PsetexOperation;
import com.zibobo.yedis.ops.strings.SetOperation;
import com.zibobo.yedis.ops.strings.SetbitOperation;
import com.zibobo.yedis.ops.strings.SetexOperation;
import com.zibobo.yedis.ops.strings.SetnxOperation;
import com.zibobo.yedis.ops.strings.SetrangeOperation;
import com.zibobo.yedis.ops.strings.StrlenOperation;
import com.zibobo.yedis.protocol.unified.connection.PingOperationImpl;
import com.zibobo.yedis.protocol.unified.hashes.HdelOperationImpl;
import com.zibobo.yedis.protocol.unified.hashes.HexistsOperationImpl;
import com.zibobo.yedis.protocol.unified.hashes.HgetOperationImpl;
import com.zibobo.yedis.protocol.unified.hashes.HgetallOperationImpl;
import com.zibobo.yedis.protocol.unified.hashes.HincrByFloatOperationImpl;
import com.zibobo.yedis.protocol.unified.hashes.HincrByOperationImpl;
import com.zibobo.yedis.protocol.unified.hashes.HkeysOperationImpl;
import com.zibobo.yedis.protocol.unified.hashes.HlenOperationImpl;
import com.zibobo.yedis.protocol.unified.hashes.HmgetOperationImpl;
import com.zibobo.yedis.protocol.unified.hashes.HmsetOperationImpl;
import com.zibobo.yedis.protocol.unified.hashes.HsetOperationImpl;
import com.zibobo.yedis.protocol.unified.hashes.HsetnxOperationImpl;
import com.zibobo.yedis.protocol.unified.hashes.HvalsOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.DelOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.DumpOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.ExistsOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.ExpireAtOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.ExpireOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.KeysOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.MigrateOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.MoveOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.ObjectIntegerOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.ObjectStringOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.PersistOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.PexpireAtOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.PexpireOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.PttlOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.RandomkeyOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.RenameOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.RenamenxOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.RestoreOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.SortOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.SortStoreOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.TtlOperationImpl;
import com.zibobo.yedis.protocol.unified.keys.TypeOperationImpl;
import com.zibobo.yedis.protocol.unified.lists.BlpopOperatonImpl;
import com.zibobo.yedis.protocol.unified.lists.BrpopOperatonImpl;
import com.zibobo.yedis.protocol.unified.lists.BrpoplpushOperatonImpl;
import com.zibobo.yedis.protocol.unified.lists.LindexOperationImpl;
import com.zibobo.yedis.protocol.unified.lists.LinsertOperationImpl;
import com.zibobo.yedis.protocol.unified.lists.LlenOperationImpl;
import com.zibobo.yedis.protocol.unified.lists.LpopOperationImpl;
import com.zibobo.yedis.protocol.unified.lists.LpushOperationImpl;
import com.zibobo.yedis.protocol.unified.lists.LpushxOperationImpl;
import com.zibobo.yedis.protocol.unified.lists.LrangeOperationImpl;
import com.zibobo.yedis.protocol.unified.lists.LremOperationImpl;
import com.zibobo.yedis.protocol.unified.lists.LsetOperationImpl;
import com.zibobo.yedis.protocol.unified.lists.LtrimOperationImpl;
import com.zibobo.yedis.protocol.unified.lists.RpopOperationImpl;
import com.zibobo.yedis.protocol.unified.lists.RpoplpushOperationImpl;
import com.zibobo.yedis.protocol.unified.lists.RpushOperationImpl;
import com.zibobo.yedis.protocol.unified.lists.RpushxOperationImpl;
import com.zibobo.yedis.protocol.unified.sets.SaddOperationImpl;
import com.zibobo.yedis.protocol.unified.sets.ScardOperationImpl;
import com.zibobo.yedis.protocol.unified.sets.SdiffOperationImpl;
import com.zibobo.yedis.protocol.unified.sets.SdiffstoreOperationImpl;
import com.zibobo.yedis.protocol.unified.sets.SinterOperationImpl;
import com.zibobo.yedis.protocol.unified.sets.SinterstoreOperationImpl;
import com.zibobo.yedis.protocol.unified.sets.SismemberOperationImpl;
import com.zibobo.yedis.protocol.unified.sets.SmembersOperationImpl;
import com.zibobo.yedis.protocol.unified.sets.SmoveOperationImpl;
import com.zibobo.yedis.protocol.unified.sets.SpopOperationImpl;
import com.zibobo.yedis.protocol.unified.sets.SrandmemberCountOperationImpl;
import com.zibobo.yedis.protocol.unified.sets.SrandmemberOperationImpl;
import com.zibobo.yedis.protocol.unified.sets.SremOperationImpl;
import com.zibobo.yedis.protocol.unified.sets.SunionOperationImpl;
import com.zibobo.yedis.protocol.unified.sets.SunionstoreOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZaddOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZcardOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZcountOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZincrbyOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZinterstoreOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZrangeOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZrangeWithScoresOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZrangebyscoreOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZrangebyscoreWithScoresOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZrankOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZremOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZremrangebyrankOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZremrangebyscoreOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZrevrangeOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZrevrangeWithScoresOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZrevrangebyscoreOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZrevrangebyscoreWithScoresOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZrevrankOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZscoreOperationImpl;
import com.zibobo.yedis.protocol.unified.sortedsets.ZunionstoreOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.AppendOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.BitcountOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.BitopOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.DecrByOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.DecrOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.GetOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.GetbitOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.GetrangeOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.GetsetOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.IncrByFloatOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.IncrByOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.IncrOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.MgetOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.MsetOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.MsetnxOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.PsetexOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.SetOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.SetbitOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.SetexOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.SetnxOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.SetrangeOperationImpl;
import com.zibobo.yedis.protocol.unified.strings.StrlenOperationImpl;

public class UnifiedOperationFactory implements OperationFactory {

    @Override
    public AppendOperation append(byte[] key, byte[] data,
            IntegerReplyCallback callback) {
        return new AppendOperationImpl(key, data, callback);
    }

    @Override
    public BitcountOperation bitcount(byte[] key, Integer start, Integer end,
            LongReplyCallback callback) {
        return new BitcountOperationImpl(key, start, end, callback);
    }

    @Override
    public BitopOperation bitop(byte[] op, byte[] dstKey, byte[][] srcKeys,
            IntegerReplyCallback callback) {
        return new BitopOperationImpl(op, dstKey, srcKeys, callback);
    }

    @Override
    public DecrOperation decr(byte[] key, LongReplyCallback callback) {
        return new DecrOperationImpl(key, callback);
    }

    @Override
    public DecrOperation decrBy(byte[] key, long value,
            LongReplyCallback callback) {
        return new DecrByOperationImpl(key, value, callback);
    }

    @Override
    public GetOperation get(byte[] key, BytesReplyCallback callback) {
        return new GetOperationImpl(key, callback);
    }

    @Override
    public GetbitOperation getbit(byte[] key, long offset,
            BooleanReplyCallback callback) {
        return new GetbitOperationImpl(key, offset, callback);
    }

    @Override
    public GetrangeOperation getrange(byte[] key, int start, int end,
            BytesReplyCallback callback) {
        return new GetrangeOperationImpl(key, start, end, callback);
    }

    @Override
    public GetsetOperation getset(byte[] key, byte[] value,
            BytesReplyCallback callback) {
        return new GetsetOperationImpl(key, value, callback);
    }

    @Override
    public IncrOperation incr(byte[] key, LongReplyCallback callback) {
        return new IncrOperationImpl(key, callback);
    }

    @Override
    public IncrOperation incrBy(byte[] key, long value,
            LongReplyCallback callback) {
        return new IncrByOperationImpl(key, value, callback);
    }

    @Override
    public IncrByFloatOperation incrByFloat(byte[] key, double value,
            DoubleReplyCallback callback) {
        return new IncrByFloatOperationImpl(key, value, callback);
    }

    @Override
    public MgetOperation mget(byte[][] keys,
            BytesListReplyCallback callback) {
        return new MgetOperationImpl(keys, callback);
    }

    @Override
    public MsetOperation mset(byte[][] keyValues, OperationCallback callback) {
        return new MsetOperationImpl(keyValues, callback);
    }

    @Override
    public MsetnxOperation msetnx(byte[][] keyValues,
            BooleanReplyCallback callback) {
        return new MsetnxOperationImpl(keyValues, callback);
    }

    @Override
    public PsetexOperation psetex(byte[] key, byte[] value, long expire,
            OperationCallback callback) {
        return new PsetexOperationImpl(key, value, expire, callback);
    }

    @Override
    public SetOperation set(byte[] key, byte[] value,
            ExpirationType expirationType, long expire,
            BooleanReplyCallback callback) {
        return new SetOperationImpl(key, value, null, expirationType, expire,
                callback);
    }

    @Override
    public SetOperation set(byte[] key, byte[] value,
            Exclusiveness exclusiveness, ExpirationType expirationType,
            long expire, BooleanReplyCallback callback) {
        return new SetOperationImpl(key, value, exclusiveness, expirationType,
                expire, callback);
    }

    @Override
    public SetbitOperation setbit(byte[] key, long offset,
            BooleanReplyCallback callback) {
        return new SetbitOperationImpl(key, offset, callback);
    }

    @Override
    public SetexOperation setex(byte[] key, byte[] value, long expire,
            OperationCallback callback) {
        return new SetexOperationImpl(key, value, expire, callback);
    }

    @Override
    public SetnxOperation setnx(byte[] key, byte[] value,
            BooleanReplyCallback callback) {
        return new SetnxOperationImpl(key, value, callback);
    }

    @Override
    public SetrangeOperation setrange(byte[] key, int offset, byte[] data,
            IntegerReplyCallback callback) {
        return new SetrangeOperationImpl(key, offset, data, callback);
    }

    @Override
    public StrlenOperation strlen(byte[] key, IntegerReplyCallback callback) {
        return new StrlenOperationImpl(key, callback);
    }

    @Override
    public DelOperation del(byte[][] keys, IntegerReplyCallback callback) {
        return new DelOperationImpl(keys, callback);
    }

    @Override
    public DumpOperation dump(byte[] key, BytesReplyCallback callback) {
        return new DumpOperationImpl(key, callback);
    }

    @Override
    public ExistsOperation exists(byte[] key, BooleanReplyCallback callback) {
        return new ExistsOperationImpl(key, callback);
    }

    @Override
    public ExpireOperation expire(byte[] key, int seconds,
            BooleanReplyCallback callback) {
        return new ExpireOperationImpl(key, seconds, callback);
    }

    @Override
    public ExpireAtOperation expireat(byte[] key, int timestamp,
            BooleanReplyCallback callback) {
        return new ExpireAtOperationImpl(key, timestamp, callback);
    }

    @Override
    public KeysOperation keys(byte[] pattern, BytesSetReplyCallback callback) {
        return new KeysOperationImpl(pattern, callback);
    }

    @Override
    public MigrateOperation migrate(byte[] host, int port, byte[] key, int db,
            int timeout, boolean copy, boolean replace,
            OperationCallback callback) {
        return new MigrateOperationImpl(host, port, key, db, timeout, copy,
                replace, callback);
    }

    @Override
    public MoveOperation
            move(byte[] key, int db, BooleanReplyCallback callback) {
        return new MoveOperationImpl(key, db, callback);
    }

    @Override
    public ObjectStringOperation object(byte[] cmd, byte[] key,
            StringReplyCallback callback) {
        return new ObjectStringOperationImpl(cmd, key, callback);
    }

    @Override
    public ObjectIntegerOperation object(byte[] cmd, byte[] key,
            IntegerReplyCallback callback) {
        return new ObjectIntegerOperationImpl(cmd, key, callback);
    }

    @Override
    public PersistOperation persist(byte[] key, BooleanReplyCallback callback) {
        return new PersistOperationImpl(key, callback);
    }

    @Override
    public PexpireOperation pexpire(byte[] key, long ttl,
            BooleanReplyCallback callback) {
        return new PexpireOperationImpl(key, ttl, callback);
    }

    @Override
    public PexpireAtOperation pexpireat(byte[] key, long timestamp,
            BooleanReplyCallback callback) {
        return new PexpireAtOperationImpl(key, timestamp, callback);
    }

    @Override
    public PttlOperation pttl(byte[] key, LongReplyCallback callback) {
        return new PttlOperationImpl(key, callback);
    }

    @Override
    public RandomkeyOperation randomkey(StringReplyCallback callback) {
        return new RandomkeyOperationImpl(callback);
    }

    @Override
    public RenameOperation rename(byte[] key, byte[] newKey,
            BooleanReplyCallback callback) {
        return new RenameOperationImpl(key, newKey, callback);
    }

    @Override
    public RenamenxOperation renamenx(byte[] key, byte[] newKey,
            BooleanReplyCallback callback) {
        return new RenamenxOperationImpl(key, newKey, callback);
    }

    @Override
    public RestoreOperation restore(byte[] key, long ttl, byte[] data,
            BooleanReplyCallback callback) {
        return new RestoreOperationImpl(key, ttl, data, callback);
    }

    @Override
    public SortOperation sort(byte[] key, boolean alpha, SortOptions options,
            BytesListReplyCallback callback) {
        return new SortOperationImpl(key, alpha, options, callback);
    }

    @Override
    public SortOperation sort(byte[] key, byte[] store, boolean alpha,
            SortOptions options, LongReplyCallback callback) {
        return new SortStoreOperationImpl(key, store, alpha, options, callback);
    }

    @Override
    public TtlOperation ttl(byte[] key, IntegerReplyCallback callback) {
        return new TtlOperationImpl(key, callback);
    }

    @Override
    public TypeOperation type(byte[] key, StringReplyCallback callback) {
        return new TypeOperationImpl(key, callback);
    }

    @Override
    public HdelOperation hdel(byte[] key, byte[][] fields,
            IntegerReplyCallback callback) {
        return new HdelOperationImpl(key, fields, callback);
    }

    @Override
    public HexistsOperation hexists(byte[] key, byte[] field,
            BooleanReplyCallback callback) {
        return new HexistsOperationImpl(key, field, callback);
    }

    @Override
    public HgetOperation hget(byte[] key, byte[] field,
            BytesReplyCallback callback) {
        return new HgetOperationImpl(key, field, callback);
    }

    @Override
    public HgetallOperation
            hgetall(byte[] key, BytesListReplyCallback callback) {
        return new HgetallOperationImpl(key, callback);
    }

    @Override
    public HincrOperation hincrBy(byte[] key, byte[] field, long value,
            LongReplyCallback callback) {
        return new HincrByOperationImpl(key, field, value, callback);
    }

    @Override
    public HincrByFloatOperation hincrByFloat(byte[] key, byte[] field,
            double value, DoubleReplyCallback callback) {
        return new HincrByFloatOperationImpl(key, field, value, callback);
    }

    @Override
    public HkeysOperation hkeys(byte[] key, StringListReplyCallback callback) {
        return new HkeysOperationImpl(key, callback);
    }

    @Override
    public HlenOperation hlen(byte[] key, IntegerReplyCallback callback) {
        return new HlenOperationImpl(key, callback);
    }

    @Override
    public HmgetOperation hmget(byte[] key, byte[][] fields,
            BytesListReplyCallback callback) {
        return new HmgetOperationImpl(key, fields, callback);
    }

    @Override
    public HmsetOperation hmset(byte[] key, byte[][] fieldValues,
            OperationCallback callback) {
        return new HmsetOperationImpl(key, fieldValues, callback);
    }

    @Override
    public HsetOperation hset(byte[] key, byte[] field, byte[] value,
            BooleanReplyCallback callback) {
        return new HsetOperationImpl(key, field, value, callback);
    }

    @Override
    public HsetnxOperation hsetnx(byte[] key, byte[] field, byte[] value,
            BooleanReplyCallback callback) {
        return new HsetnxOperationImpl(key, field, value, callback);
    }

    @Override
    public HvalsOperation hvals(byte[] key, BytesListReplyCallback callback) {
        return new HvalsOperationImpl(key, callback);
    }

    @Override
    public BlpopOperation blpop(byte[][] keys, int timeout,
            BlockingPopReplyCallback callback) {
        return new BlpopOperatonImpl(keys, timeout, callback);
    }

    @Override
    public BrpopOperation brpop(byte[][] keys, int timeout,
            BlockingPopReplyCallback callback) {
        return new BrpopOperatonImpl(keys, timeout, callback);
    }

    @Override
    public BrpoplpushOperation brpoplpush(byte[] source, byte[] destination,
            int timeout, BytesReplyCallback callback) {
        return new BrpoplpushOperatonImpl(source, destination, timeout,
                callback);
    }

    @Override
    public LindexOperation lindex(byte[] key, long index,
            BytesReplyCallback callback) {
        return new LindexOperationImpl(key, index, callback);
    }

    @Override
    public LinsertOperation linsert(byte[] key, byte[] position, byte[] pivot,
            byte[] value, LongReplyCallback callback) {
        return new LinsertOperationImpl(key, position, pivot, value, callback);
    }

    @Override
    public LlenOperation llen(byte[] key, LongReplyCallback callback) {
        return new LlenOperationImpl(key, callback);
    }

    @Override
    public LpopOperation lpop(byte[] key, BytesReplyCallback callback) {
        return new LpopOperationImpl(key, callback);
    }

    @Override
    public LpushOperation lpush(byte[] key, byte[][] values,
            LongReplyCallback callback) {
        return new LpushOperationImpl(key, values, callback);
    }

    @Override
    public LpushxOperation lpushx(byte[] key, byte[] value,
            LongReplyCallback callback) {
        return new LpushxOperationImpl(key, value, callback);
    }

    @Override
    public LrangeOperation lrange(byte[] key, long start, long stop,
            BytesListReplyCallback callback) {
        return new LrangeOperationImpl(key, start, stop, callback);
    }

    @Override
    public LremOperation lrem(byte[] key, long count, byte[] value,
            LongReplyCallback callback) {
        return new LremOperationImpl(key, count, value, callback);
    }

    @Override
    public LsetOperation lset(byte[] key, long index, byte[] value,
            OperationCallback callback) {
        return new LsetOperationImpl(key, index, value, callback);
    }

    @Override
    public LtrimOperation ltrim(byte[] key, long start, long stop,
            OperationCallback callback) {
        return new LtrimOperationImpl(key, start, stop, callback);
    }

    @Override
    public RpopOperation rpop(byte[] key, BytesReplyCallback callback) {
        return new RpopOperationImpl(key, callback);
    }

    @Override
    public RpoplpushOperation rpoplpush(byte[] source, byte[] destination,
            BytesReplyCallback callback) {
        return new RpoplpushOperationImpl(source, destination, callback);
    }

    @Override
    public RpushOperation rpush(byte[] key, byte[][] values,
            LongReplyCallback callback) {
        return new RpushOperationImpl(key, values, callback);
    }

    @Override
    public RpushxOperation rpushx(byte[] key, byte[] value,
            LongReplyCallback callback) {
        return new RpushxOperationImpl(key, value, callback);
    }

    @Override
    public SaddOperation sadd(byte[] key, byte[][] values,
            LongReplyCallback callback) {
        return new SaddOperationImpl(key, values, callback);
    }

    @Override
    public ScardOperation scard(byte[] key, LongReplyCallback callback) {
        return new ScardOperationImpl(key, callback);
    }

    @Override
    public SdiffOperation sdiff(byte[][] keys, BytesSetReplyCallback callback) {
        return new SdiffOperationImpl(keys, callback);
    }

    @Override
    public SdiffstoreOperation sdiffstore(byte[] destination, byte[][] keys,
            LongReplyCallback callback) {
        return new SdiffstoreOperationImpl(destination, keys, callback);
    }

    @Override
    public SinterOperation
            sinter(byte[][] keys, BytesSetReplyCallback callback) {
        return new SinterOperationImpl(keys, callback);
    }

    @Override
    public SinterstoreOperation sinterstore(byte[] destination, byte[][] keys,
            LongReplyCallback callback) {
        return new SinterstoreOperationImpl(destination, keys, callback);
    }

    @Override
    public SismembersOperation sismember(byte[] key, byte[] value,
            BooleanReplyCallback callback) {
        return new SismemberOperationImpl(key, value, callback);
    }

    @Override
    public SmoveOperation smove(byte[] source, byte[] destination,
            byte[] value, BooleanReplyCallback callback) {
        return new SmoveOperationImpl(source, destination, value, callback);
    }

    @Override
    public SmembersOperation
            smembers(byte[] key, BytesSetReplyCallback callback) {
        return new SmembersOperationImpl(key, callback);
    }

    @Override
    public SpopOperation spop(byte[] key, BytesReplyCallback callback) {
        return new SpopOperationImpl(key, callback);
    }

    @Override
    public SrandmemberOperation srandmember(byte[] key,
            BytesReplyCallback callback) {
        return new SrandmemberOperationImpl(key, callback);
    }

    @Override
    public SrandmemberCountOperation srandmember(byte[] key, long count,
            BytesSetReplyCallback callback) {
        return new SrandmemberCountOperationImpl(key, count, callback);
    }

    @Override
    public SremOperation srem(byte[] key, byte[][] values,
            LongReplyCallback callback) {
        return new SremOperationImpl(key, values, callback);
    }

    @Override
    public SunionOperation
            sunion(byte[][] keys, BytesSetReplyCallback callback) {
        return new SunionOperationImpl(keys, callback);
    }

    @Override
    public SunionstoreOperation sunionstore(byte[] destination, byte[][] keys,
            LongReplyCallback callback) {
        return new SunionstoreOperationImpl(destination, keys, callback);
    }

    @Override
    public ZaddOperation zadd(byte[] key, List<BytesSortedSetEntry> entries,
            LongReplyCallback callback) {
        return new ZaddOperationImpl(key, entries, callback);
    }

    @Override
    public ZcardOperation zcard(byte[] key, LongReplyCallback callback) {
        return new ZcardOperationImpl(key, callback);
    }

    @Override
    public ZcountOperation zcount(byte[] key, double min, double max,
            LongReplyCallback callback) {
        return new ZcountOperationImpl(key, min, max, callback);
    }

    @Override
    public ZincrbyOperation zincrby(byte[] key, double increment,
            byte[] member, DoubleReplyCallback callback) {
        return new ZincrbyOperationImpl(key, increment, member, callback);
    }

    @Override
    public ZinterstoreOperation
            zinterstore(byte[] destination, byte[][] keys, double[] weights,
                    Aggregation aggregate, LongReplyCallback callback) {
        return new ZinterstoreOperationImpl(destination, keys, weights,
                aggregate, callback);
    }

    @Override
    public ZrangeOperation zrange(byte[] key, long start, long stop,
            BytesSetReplyCallback callback) {
        return new ZrangeOperationImpl(key, start, stop, callback);
    }

    @Override
    public ZrangeWithScoresOperation zrangeWithScores(byte[] key, long start,
            long stop, BytesSetWithScoresReplyCallback callback) {
        return new ZrangeWithScoresOperationImpl(key, start, stop, callback);
    }

    @Override
    public ZrangebyscoreOperation zrangebyscores(byte[] key, IntervalValue min,
            IntervalValue max, Long limit, Long count,
            BytesSetReplyCallback callback) {
        return new ZrangebyscoreOperationImpl(key, min, max, limit, count,
                callback);
    }

    @Override
    public ZrangebyscoreWithScoresOperation zrangebyscoresWithScores(
            byte[] key, IntervalValue min, IntervalValue max, Long limit,
            Long count, BytesSetWithScoresReplyCallback callback) {
        return new ZrangebyscoreWithScoresOperationImpl(key, min, max, limit,
                count, callback);
    }

    @Override
    public ZrankOperation zrank(byte[] key, byte[] member,
            NullableLongReplyCallback callback) {
        return new ZrankOperationImpl(key, member, callback);
    }

    @Override
    public ZremOperation zrem(byte[] key, byte[][] members,
            LongReplyCallback callback) {
        return new ZremOperationImpl(key, members, callback);
    }

    @Override
    public ZremrangebyrankOperation zremrangebyrank(byte[] key, long start,
            long stop, LongReplyCallback callback) {
        return new ZremrangebyrankOperationImpl(key, start, stop, callback);
    }

    @Override
    public ZremrangebyscoreOperation zremrangebyscore(byte[] key, double min,
            double max, LongReplyCallback callback) {
        return new ZremrangebyscoreOperationImpl(key, min, max, callback);
    }

    @Override
    public ZrevrangeOperation zrevrange(byte[] key, long start, long stop,
            BytesSetReplyCallback callback) {
        return new ZrevrangeOperationImpl(key, start, stop, callback);
    }

    @Override
    public ZrevrangeWithScoresOperation zrevrangeWithScores(byte[] key,
            long start, long stop, BytesSetWithScoresReplyCallback callback) {
        return new ZrevrangeWithScoresOperationImpl(key, start, stop, callback);
    }

    @Override
    public ZrevrangebyscoreOperation zrevrangebyscores(byte[] key,
            IntervalValue min, IntervalValue max, Long limit, Long count,
            BytesSetReplyCallback callback) {
        return new ZrevrangebyscoreOperationImpl(key, min, max, limit, count,
                callback);
    }

    @Override
    public ZrevrangebyscoreWithScoresOperation zrevrangebyscoresWithScores(
            byte[] key, IntervalValue min, IntervalValue max, Long limit,
            Long count, BytesSetWithScoresReplyCallback callback) {
        return new ZrevrangebyscoreWithScoresOperationImpl(key, min, max,
                limit, count, callback);
    }

    @Override
    public ZrevrankOperation zrevrank(byte[] key, byte[] member,
            NullableLongReplyCallback callback) {
        return new ZrevrankOperationImpl(key, member, callback);
    }

    @Override
    public ZscoreOperation zscore(byte[] key, byte[] member,
            NullableDoubleReplyCallback callback) {
        return new ZscoreOperationImpl(key, member, callback);
    }

    @Override
    public ZunionstoreOperation
            zunionstore(byte[] destination, byte[][] keys, double[] weights,
                    Aggregation aggregate, LongReplyCallback callback) {
        return new ZunionstoreOperationImpl(destination, keys, weights,
                aggregate, callback);
    }

    @Override
    public PingOperation ping(StringReplyCallback callback) {
        return new PingOperationImpl(callback);
    }

    @Override
    public PipelineOperation pipeline() {
        return new PipelineOperationImpl();
    }

}
