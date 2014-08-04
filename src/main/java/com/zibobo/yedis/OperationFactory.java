package com.zibobo.yedis;

import java.util.List;

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

/**
 * Factory that builds operations for protocol handlers.
 */
public interface OperationFactory {

    /**
     * Create an APPEND operation.
     *
     * @param key
     *            the key
     * @param data
     *            the data to append
     * @param callback
     *            the append callback
     * @return the new {@link AppendOperation}
     */
    AppendOperation append(byte[] key, byte[] data,
            IntegerReplyCallback callback);

    /**
     * Create a BITCOUNT operation.
     *
     * @param key
     *            the key
     * @param start
     *            possibly null start
     * @param end
     *            possilby null end
     * @param callback
     *            the bitcount callback
     * @return the new {@link BitcountOperation}
     */
    BitcountOperation bitcount(byte[] key, Integer start, Integer end,
            LongReplyCallback callback);

    /**
     * Create a BITOP operation.
     *
     * @param op
     *            the bitop
     * @param dstKey
     *            the destination key
     * @param srcKeys
     *            the src keys
     * @param callback
     *            the bitop callback
     * @return the new {@link BitopOperation}
     */
    BitopOperation bitop(byte[] op, byte[] dstKey, byte[][] srcKeys,
            IntegerReplyCallback callback);

    /**
     * Create a DECR operation.
     *
     * @param key
     *            the key
     * @param callback
     *            the decr callback
     * @return the new {@link DecrOperation}
     */
    DecrOperation decr(byte[] key, LongReplyCallback callback);

    /**
     * Create a DECRBY operation.
     *
     * @param key
     *            the key
     * @param value
     *            the value to decr by
     * @param callback
     *            the decr callback
     * @return the new {@link DecrOperation}
     */
    DecrOperation decrBy(byte[] key, long value, LongReplyCallback callback);

    /**
     * Create a GET operation.
     *
     * @param key
     *            the key to get
     * @param callback
     *            the get callback
     * @return the new GetOperation
     */
    GetOperation get(byte[] key, BytesReplyCallback callback);

    /**
     * Create a GETBIT operation.
     *
     * @param key
     *            the key to get the bit from
     * @param offset
     *            the offset of the bit to get
     * @param callback
     *            the getbit callback
     * @return the new {@link GetbitOperation}
     */
    GetbitOperation getbit(byte[] key, long offset,
            BooleanReplyCallback callback);

    /**
     * Create a GETRANGE operation.
     *
     * @param key
     *            the key to get the range from
     * @param start
     *            the start offset
     * @param end
     *            the end offset
     * @param callback
     *            the getrange callback
     * @return the new {@link GetrangeOperation}
     */
    GetrangeOperation getrange(byte[] key, int start, int end,
            BytesReplyCallback callback);

    /**
     * Create a GETSET operation.
     *
     * @param key
     *            the key to getset
     * @param value
     *            the value to set
     * @param callback
     *            the getset callback
     * @return the new {@link GetsetOperation}
     */
    GetsetOperation
            getset(byte[] key, byte[] value, BytesReplyCallback callback);

    /**
     * Create a INCR operation.
     *
     * @param key
     *            the key
     * @param callback
     *            the incr callback
     * @return the new {@link IncrOperation}
     */
    IncrOperation incr(byte[] key, LongReplyCallback callback);

    /**
     * Create a INCRBY operation.
     *
     * @param key
     *            the key
     * @param value
     *            the value to incr by
     * @param callback
     *            the incr callback
     * @return the new {@link IncrOperation}
     */
    IncrOperation incrBy(byte[] key, long value, LongReplyCallback callback);

    /**
     * Create a INCRBYFLOAT operation.
     *
     * @param key
     *            the key
     * @param value
     *            the value to incr by
     * @param callback
     *            the incrbyfloat callback
     * @return the new {@link IncrByFloatOperation}
     */
    IncrByFloatOperation incrByFloat(byte[] key, double value,
            DoubleReplyCallback callback);

    /**
     * Create a MGET operation.
     *
     * @param keys
     *            the keys
     * @param callback
     *            the mget callback
     *
     * @return the new {@link MgetOperation}
     */
    MgetOperation mget(byte[][] keys, BytesListReplyCallback callback);

    /**
     * Creates a MSET operation
     *
     * @param keyValues
     *            the key value pairs
     * @param callback
     *            the mset callback
     * @return the new {@link MsetOperation}
     */
    MsetOperation mset(byte[][] keyValues, OperationCallback callback);

    /**
     * Creates a MSETNX operation
     *
     * @param keyValues
     *            the key value pairs
     * @param callback
     *            the msetnx callback
     * @return the new {@link MsetnxOperation}
     */
    MsetnxOperation msetnx(byte[][] keyValues, BooleanReplyCallback callback);

    /**
     * Creates a PSETEX operation
     *
     * @param key
     *            the key
     * @param value
     *            the value
     * @param expire
     *            the expiry in milliseconds
     * @param callback
     *            the psetex callback
     * @return the new {@link PsetexOperation}
     */
    PsetexOperation psetex(byte[] key, byte[] value, long expire,
            OperationCallback callback);

    /**
     * Creates a SET operation
     *
     * @param key
     *            the key
     * @param value
     *            the value
     * @param expirationType
     *            the type of expiration
     * @param expire
     *            the value of expiration
     * @param callback
     *            the set callback
     * @return the new {@link SetOperation}
     */
    SetOperation set(byte[] key, byte[] value, ExpirationType expirationType,
            long expire, BooleanReplyCallback callback);

    /**
     * Creates a SET operation
     *
     * @param key
     *            the key
     * @param value
     *            the value
     * @param expirationType
     *            the type of expiration
     * @param expire
     *            the value of expiration
     * @param callback
     *            the set callback
     * @return the new {@link SetOperation}
     */
    SetOperation set(byte[] key, byte[] value, Exclusiveness exclusiveness,
            ExpirationType expirationType, long expire,
            BooleanReplyCallback callback);

    /**
     * Create a SETBIT operation.
     *
     * @param key
     *            the key to set the bit in
     * @param offset
     *            the offset of the bit to set
     * @param callback
     *            the setbit callback
     * @return the new {@link SetbitOperation}
     */
    SetbitOperation setbit(byte[] key, long offset,
            BooleanReplyCallback callback);

    /**
     * Creates a SETEX operation
     *
     * @param key
     *            the key
     * @param value
     *            the value
     * @param expire
     *            the expiry in seconds
     * @param callback
     *            the setex callback
     * @return the new {@link SetexOperation}
     */
    SetexOperation setex(byte[] key, byte[] value, long expire,
            OperationCallback callback);

    /**
     * Creates a SETNX operation
     *
     * @param key
     *            the key
     * @param value
     *            the value
     * @param callback
     *            the setnx callback
     * @return the new {@link SetnxOperation}
     */
    SetnxOperation
            setnx(byte[] key, byte[] value, BooleanReplyCallback callback);

    /**
     * Create a SETRANGE operation.
     *
     * @param key
     *            the key
     * @param offset
     *            the offset
     * @param data
     *            the data to set
     * @param callback
     *            the setrange callback
     * @return the new {@link SetrangeOperation}
     */
    SetrangeOperation setrange(byte[] key, int offset, byte[] data,
            IntegerReplyCallback callback);

    /**
     * Create a STRLEN operation
     *
     * @param key
     *            the key
     * @param callback
     *            the strlen callback
     * @return the new {@link StrlenOperation}
     */
    StrlenOperation strlen(byte[] key, IntegerReplyCallback callback);

    /**
     * Create a DEL operation
     *
     * @param keys
     *            the keys to delete
     * @param callback
     *            the del callback
     * @return the new {@link DelOperation}
     */
    DelOperation del(byte[][] keys, IntegerReplyCallback callback);

    /**
     * Create a DUMP operation
     *
     * @param key
     *            the key to dump
     * @param callback
     *            the dump callback
     * @return the new {@link DumpOperation}
     */
    DumpOperation dump(byte[] key, BytesReplyCallback callback);

    /**
     * Create an EXISTS operation
     *
     * @param key
     *            the key to check
     * @param callback
     *            the exists callback
     * @return the new {@link ExistsOperation}
     */
    ExistsOperation exists(byte[] key, BooleanReplyCallback callback);

    /**
     * Create an EXPIRE operation
     *
     * @param key
     *            the key to set the expiration for
     * @param seconds
     *            seconds to expire in
     * @param callback
     *            the expire callback
     * @return the new {@link ExpireOperation}
     */
    ExpireOperation expire(byte[] key, int seconds,
            BooleanReplyCallback callback);

    /**
     * Create an EXPIREAT operation
     *
     * @param key
     *            the key to set the expiration for
     * @param timestamp
     *            unix timestamp in seconds to expire at
     * @param callback
     *            the expireat callback
     * @return the new {@link ExpireAtOperation}
     */
    ExpireAtOperation expireat(byte[] key, int timestamp,
            BooleanReplyCallback callback);

    /**
     * Create a KEYS operation
     *
     * @param pattern
     *            the pattern
     * @param callback
     *            the keys callback
     * @return the new {@link KeysOperation}
     */
    KeysOperation keys(byte[] pattern, BytesSetReplyCallback callback);

    /**
     * Create a MIGRATE operation
     *
     * @param host
     *            the host of the server to migrate to
     * @param port
     *            the port of the server to migrate to
     * @param key
     *            the key to migrate
     * @param db
     *            the number of the dest db
     * @param timeout
     *            timeout in seconds
     * @param copy
     *            if true then the the key will be left on the source server
     * @param replace
     *            if true then existing keys on the dest server will be
     *            overwritten
     * @param callback
     * @return the new {@link MigrateOperation}
     */
    MigrateOperation migrate(byte[] host, int port, byte[] key, int db,
            int timeout, boolean copy, boolean replace,
            OperationCallback callback);

    /**
     * Create a new MOVE operation
     *
     * @param key
     *            the key to move
     * @param db
     *            the database to move it to
     * @param callback
     *            the move callback
     * @return the new {@link MoveOperation}
     */
    MoveOperation move(byte[] key, int db, BooleanReplyCallback callback);

    /**
     * Create a new OBJECT operation that returns a string
     *
     * @param cmd
     *            the cmd to pass after OBJECT
     * @param key
     *            the key
     * @param callback
     *            the objectstring callback
     * @return the new {@link ObjectStringOperation}
     */
    ObjectStringOperation object(byte[] cmd, byte[] key,
            StringReplyCallback callback);

    /**
     * Create a new OBJECT operation that returns an integer
     *
     * @param cmd
     *            the cmd to pass after OBJECT
     * @param key
     *            the key
     * @param callback
     *            the object encoding callback
     * @return the new {@link ObjectStringOperation}
     */
    ObjectIntegerOperation object(byte[] cmd, byte[] key,
            IntegerReplyCallback callback);

    /**
     * Create a new PERSIST operation
     *
     * @param key
     *            the key to persist
     * @param callback
     *            the persist callback
     * @return the new {@link PersistOperation}
     */
    PersistOperation persist(byte[] key, BooleanReplyCallback callback);

    /**
     * Create a PEXPIRE operation
     *
     * @param key
     *            the key to set the expiration for
     * @param ttl
     *            milliseconds to expire in
     * @param callback
     *            the pexpire callback
     * @return the new {@link PexpireOperation}
     */
    PexpireOperation
            pexpire(byte[] key, long ttl, BooleanReplyCallback callback);

    /**
     * Create a PEXPIREAT operation
     *
     * @param key
     *            the key to set the expiration for
     * @param timestamp
     *            unix timestamp in milliseconds to expire at
     * @param callback
     *            the pexpireat callback
     * @return the new {@link PexpireAtOperation}
     */
    PexpireAtOperation pexpireat(byte[] key, long timestamp,
            BooleanReplyCallback callback);

    /**
     * Create a PTTL operation
     *
     * @param key
     *            the key
     * @param callback
     *            the pttl callback
     * @return the new {@link PttlOperation}
     */
    PttlOperation pttl(byte[] key, LongReplyCallback callback);

    /**
     * Create a RANDOMKEY operation
     *
     * @param callback
     *            the randomkey callback
     * @return the new {@link RandomkeyOperation}
     */
    RandomkeyOperation randomkey(StringReplyCallback callback);

    /**
     * Create a RENAME operation
     *
     * @param key
     *            the key
     * @param newKey
     *            the key to rename to
     * @param callback
     *            the rename callback
     * @return the new {@link RenameOperation}
     */
    RenameOperation rename(byte[] key, byte[] newKey,
            BooleanReplyCallback callback);

    /**
     * Create a RENAMENX operation
     *
     * @param key
     *            the key
     * @param newKey
     *            the key to rename to
     * @param callback
     *            the renamenx callback
     * @return the new {@link RenamenxOperation}
     */
    RenamenxOperation renamenx(byte[] key, byte[] newKey,
            BooleanReplyCallback callback);

    /**
     * Create a RESTORE operation
     *
     * @param key
     *            the key to restore to
     * @param ttl
     *            the ttl in milliseconds
     * @param data
     *            the data to restore
     * @param callback
     *            the restore callback
     * @return the new {@link RestoreOperation}
     */
    RestoreOperation restore(byte[] key, long ttl, byte[] data,
            BooleanReplyCallback callback);

    /**
     * Create a SORT operation
     *
     * @param key
     *            the key to sort
     * @param alpha
     *            if true then values are sorted in lexical order
     * @param options
     *            options for sorting
     * @param callback
     *            the sort callback
     * @return the new {@link SortOperation}
     */
    SortOperation sort(byte[] key, boolean alpha, SortOptions options,
            BytesListReplyCallback callback);

    /**
     * Create a SORT operation that STOREs it's results
     *
     * @param key
     *            the key to sort
     * @param store
     *            where to store the results
     * @param alpha
     *            if true then values are sorted in lexical order
     * @param options
     *            options for sorting
     * @param store
     *            if not null then the sort results will be stored in this key
     * @param callback
     *            the sort callback
     * @return the new {@link SortOperation}
     */
    SortOperation sort(byte[] key, byte[] store, boolean alpha,
            SortOptions options, LongReplyCallback callback);

    /**
     * Create a TTL operation
     *
     * @param key
     *            the key
     * @param callback
     *            the ttl callback
     * @return the new {@link TtlOperation}
     */
    TtlOperation ttl(byte[] key, IntegerReplyCallback callback);

    /**
     * Create a TYPE operation
     *
     * @param key
     *            the key
     * @param callback
     *            the type callback
     * @return the new {@link TypeOperation}
     */
    TypeOperation type(byte[] key, StringReplyCallback callback);

    /**
     * Create an HDEL operation
     *
     * @param key
     *            the hash key
     * @param fields
     *            the fields to delete
     * @param callback
     *            the hdel callback
     * @return the new {@link HdelOperation}
     */
    HdelOperation hdel(byte[] key, byte[][] fields,
            IntegerReplyCallback callback);

    /**
     * Create an HEXISTS operation
     *
     * @param key
     *            the hash key
     * @param field
     *            the field
     * @param callback
     *            the hexists callback
     * @return the new {@link HexistsOperation}
     */
    HexistsOperation hexists(byte[] key, byte[] field,
            BooleanReplyCallback callback);

    /**
     * Create an HGET operation
     *
     * @param key
     *            the hash key
     * @param field
     *            the field
     * @param callback
     *            the hget callback
     * @return the new {@link HgetOperation}
     */
    HgetOperation hget(byte[] key, byte[] field, BytesReplyCallback callback);

    /**
     * Create an HGETALL operation
     *
     * @param key
     *            the hash key
     * @param callback
     *            the hgetall callback
     * @return the new {@link HgetallOperation}
     */
    HgetallOperation hgetall(byte[] key, BytesListReplyCallback callback);

    /**
     * Create an HINCRBY operation.
     *
     * @param key
     *            the key
     * @param field
     *            the field
     * @param value
     *            the value to incr by
     * @param callback
     *            the hincrby callback
     * @return the new {@link HincrOperation}
     */
    HincrOperation hincrBy(byte[] key, byte[] field, long value,
            LongReplyCallback callback);

    /**
     * Create an HINCRBYFLOAT operation.
     *
     * @param key
     *            the key
     * @param field
     *            the field
     * @param value
     *            the value to incr by
     * @param callback
     *            the incrbyfloat callback
     * @return the new {@link IncrByFloatOperation}
     */
    HincrByFloatOperation hincrByFloat(byte[] key, byte[] field, double value,
            DoubleReplyCallback callback);

    /**
     * Create an HKEYS operation
     *
     * @param key
     *            the key
     * @param callback
     *            the hkeys callback
     * @return the new {@link HkeysOperation}
     */
    HkeysOperation hkeys(byte[] key, StringListReplyCallback callback);

    /**
     * Create an HLEN operation
     *
     * @param key
     *            the key
     * @param callback
     *            the hlen callback
     * @return the new {@link HlenOperation}
     */
    HlenOperation hlen(byte[] key, IntegerReplyCallback callback);

    /**
     * Create an HMGET operation
     *
     * @param key
     *            the key
     * @param fields
     *            the fields
     * @param callback
     *            the hmget callback
     * @return the new {@link HmgetOperation}
     */
    HmgetOperation hmget(byte[] key, byte[][] fields,
            BytesListReplyCallback callback);

    /**
     * Create an HMSET operation
     *
     * @param key
     *            the hash key
     * @param fieldValues
     *            the array of field and values
     * @param callback
     *            the hmset callback
     * @return the new {@link HmsetOperation}
     */
    HmsetOperation hmset(byte[] key, byte[][] fieldValues,
            OperationCallback callback);

    /**
     * Create an HSET operation
     *
     * @param key
     *            the hash key
     * @param field
     *            the field
     * @param value
     *            the value
     * @param callback
     *            the hset callback
     * @return the new {@link HsetOperation}
     */
    HsetOperation hset(byte[] key, byte[] field, byte[] value,
            BooleanReplyCallback callback);

    /**
     * Create an HSETNX operation
     *
     * @param key
     *            the hash key
     * @param field
     *            the field
     * @param value
     *            the value
     * @param callback
     *            the hsetnx callback
     * @return the new {@link HsetnxOperation}
     */
    HsetnxOperation hsetnx(byte[] key, byte[] field, byte[] value,
            BooleanReplyCallback callback);

    /**
     * Create an HVALS operation
     *
     * @param key
     *            the hash key
     * @param callback
     *            the hvals callback
     * @return the new {@link HvalsOperation}
     */
    HvalsOperation hvals(byte[] key, BytesListReplyCallback callback);

    /**
     * Create a BLPOP operation
     *
     * @param keys
     *            the keys to wait for
     * @param timeout
     *            the timeout in seconds
     * @param callback
     *            the blpop callback
     * @return the new {@link BlpopOperation}
     */
    BlpopOperation blpop(byte[][] keys, int timeout,
            BlockingPopReplyCallback callback);

    /**
     * Create a BRPOP operation
     *
     * @param keys
     *            the keys to wait for
     * @param timeout
     *            the timeout in seconds
     * @param callback
     *            the brpop callback
     * @return the new {@link BrpopOperation}
     */
    BrpopOperation brpop(byte[][] keys, int timeout,
            BlockingPopReplyCallback callback);

    /**
     * Create a BRPOPLPUSH operation
     *
     * @param source
     *            the source to pop from
     * @param destination
     *            the destination to push into
     * @param timeout
     *            the timeout in seconds
     * @param callback
     *            the brpoplpush callback
     * @return the new {@link BrpoplpushOperation}
     */
    BrpoplpushOperation brpoplpush(byte[] source, byte[] destination,
            int timeout, BytesReplyCallback callback);

    /**
     * Create a LINDEX operation
     *
     * @param key
     *            the key of the list
     * @param index
     *            the index
     * @param callback
     *            the lindex callback
     * @return the new {@link LindexOperation}
     */
    LindexOperation lindex(byte[] key, long index, BytesReplyCallback callback);

    /**
     * Create a LINSERT operation
     *
     * @param key
     *            the key of the list
     * @param position
     *            the position to insert (BEFORE or AFTER)
     * @param pivot
     *            the pivot point
     * @param value
     *            the value to insert
     * @param callback
     *            the linsert callback
     * @return the new {@link LinsertOperation}
     */
    LinsertOperation linsert(byte[] key, byte[] position, byte[] pivot,
            byte[] value, LongReplyCallback callback);

    /**
     * Create a LLEN operation
     *
     * @param key
     *            the key of the list
     * @param callback
     *            the llen callback
     * @return the new {@link LlenOperation}
     */
    LlenOperation llen(byte[] key, LongReplyCallback callback);

    /**
     * Create a LPOP operation
     *
     * @param key
     *            the key of the list
     * @param callback
     *            the lpop callback
     * @return the new {@link LpopOperation}
     */
    LpopOperation lpop(byte[] key, BytesReplyCallback callback);

    /**
     * Create a LPUSH operation
     *
     * @param key
     *            the key of the list
     * @param values
     *            the values to push
     * @param callback
     *            the lpush callback
     * @return the new {@link LpushOperation}
     */
    LpushOperation
            lpush(byte[] key, byte[][] values, LongReplyCallback callback);

    /**
     * Create a LPUSHX operation
     *
     * @param key
     *            the key of the list
     * @param value
     *            the value to push
     * @param callback
     *            the lpushx callback
     * @return the new {@link LpushxOperation}
     */
    LpushxOperation
            lpushx(byte[] key, byte[] value, LongReplyCallback callback);

    /**
     * Create a LRANGE operation
     *
     * @param key
     *            the key of the list
     * @param start
     *            the start index
     * @param stop
     *            the stop index
     * @param callback
     *            the lrange callback
     * @return the new {@link LrangeOperation}
     */
    LrangeOperation lrange(byte[] key, long start, long stop,
            BytesListReplyCallback callback);

    /**
     * Create a LREM operation
     *
     * @param key
     *            the key of the list
     * @param count
     *            the count to remove
     * @param value
     *            the value to remove
     * @param callback
     *            the lrem callback
     * @return the new {@link LremOperation}
     */
    LremOperation lrem(byte[] key, long count, byte[] value,
            LongReplyCallback callback);

    /**
     * Create a LSET operation
     *
     * @param key
     *            the key of the list
     * @param index
     *            the index to set
     * @param value
     *            the value to set
     * @param callback
     *            the lset callback
     * @return the new {@link LsetOperation}
     */
    LsetOperation lset(byte[] key, long index, byte[] value,
            OperationCallback callback);

    /**
     * Create a LTRIM operation
     *
     * @param key
     *            the key of the list
     * @param start
     *            the start index
     * @param stop
     *            the stop index
     * @param callback
     *            the ltrim callback
     * @return the new {@link LtrimOperation}
     */
    LtrimOperation ltrim(byte[] key, long start, long stop,
            OperationCallback callback);

    /**
     * Create a RPOP operation
     *
     * @param key
     *            the key of the list
     * @param callback
     *            the rpop callback
     * @return the new {@link RpopOperation}
     */
    RpopOperation rpop(byte[] key, BytesReplyCallback callback);

    /**
     * Create a RPOPLPUSH operation
     *
     * @param source
     *            the source to pop from
     * @param destination
     *            the destination to push into
     * @param callback
     *            the rpoplpush callback
     * @return the new {@link RpoplpushOperation}
     */
    RpoplpushOperation rpoplpush(byte[] source, byte[] destination,
            BytesReplyCallback callback);

    /**
     * Create a RPUSH operation
     *
     * @param key
     *            the key of the list
     * @param values
     *            the values to push
     * @param callback
     *            the rpush callback
     * @return the new {@link RpushOperation}
     */
    RpushOperation
            rpush(byte[] key, byte[][] values, LongReplyCallback callback);

    /**
     * Create a RPUSHX operation
     *
     * @param key
     *            the key of the list
     * @param value
     *            the value to push
     * @param callback
     *            the rpushx callback
     * @return the new {@link RpushxOperation}
     */
    RpushxOperation
            rpushx(byte[] key, byte[] value, LongReplyCallback callback);

    /**
     * Create a SADD operation
     *
     * @param key
     *            the key of the set
     * @param values
     *            the values to add
     * @param callback
     *            the sadd callback
     * @return the new {@link SaddOperation}
     */
    SaddOperation sadd(byte[] key, byte[][] values, LongReplyCallback callback);

    /**
     * Create a SCARD operation
     *
     * @param key
     *            the key of the set
     * @param callback
     *            the scard callback
     * @return the new {@link ScardOperation}
     */
    ScardOperation scard(byte[] key, LongReplyCallback callback);

    /**
     * Create a SDIFF operation
     *
     * @param keys
     *            the keys to diff
     * @param callback
     *            the sdiff callback
     * @return the new {@link SdiffOperation}
     */
    SdiffOperation sdiff(byte[][] keys, BytesSetReplyCallback callback);

    /**
     * Create a SDIFFSTORE operation
     *
     * @param destination
     *            the destination key
     * @param keys
     *            the keys to diff
     * @param callback
     *            the sdiffstore callback
     * @return the new {@link SdiffstoreOperation}
     */
    SdiffstoreOperation sdiffstore(byte[] destination, byte[][] keys,
            LongReplyCallback callback);

    /**
     * Create a SINTER operation
     *
     * @param keys
     *            the keys to intersect
     * @param callback
     *            the sinter callback
     * @return the new {@link SinterOperation}
     */
    SinterOperation sinter(byte[][] keys, BytesSetReplyCallback callback);

    /**
     * Create a SINTERSTORE operation
     *
     * @param destination
     *            the destination key
     * @param keys
     *            the set keys to intersect
     * @param callback
     *            the sinterstore callback
     * @return the new {@link SinterstoreOperation}
     */
    SinterstoreOperation sinterstore(byte[] destination, byte[][] keys,
            LongReplyCallback callback);

    /**
     * Create a SISMEMBER operation
     *
     * @param key
     *            the key of the set
     * @param value
     *            the to check
     * @param callback
     *            the sismember callback
     * @return the new {@link SismembersOperation}
     */
    SismembersOperation sismember(byte[] key, byte[] value,
            BooleanReplyCallback callback);

    /**
     * Creates a SMEMBERS operation.
     *
     * @param key
     *            the set key
     * @param callback
     *            the smembers callback
     * @return the new {@link SmembersOperation}
     */
    SmembersOperation smembers(byte[] key, BytesSetReplyCallback callback);

    /**
     * Creates a SMOVE operation
     *
     * @param source
     *            the source key
     * @param destination
     *            the destination key
     * @param value
     *            the value to move
     * @param callback
     *            the smove callback
     * @return the new {@link SmoveOperation}
     */
    SmoveOperation smove(byte[] source, byte[] destination, byte[] value,
            BooleanReplyCallback callback);

    /**
     * Creates a SPOP operation
     *
     * @param key
     *            the set key
     * @param callback
     *            the spop callback
     * @return the new {@link SpopOperation}
     */
    SpopOperation spop(byte[] key, BytesReplyCallback callback);

    /**
     * Creates a SRANDMEMBER operation
     *
     * @param key
     *            the set key
     * @param callback
     *            the srandmember callback
     * @return the new {@link SrandmemberOperation}
     */
    SrandmemberOperation srandmember(byte[] key, BytesReplyCallback callback);

    /**
     * Creates a SRANDMEMBER with count operation
     *
     * @param key
     *            the set key
     * @param count
     *            the count to return
     * @param callback
     *            the srandmember callback
     * @return the new {@link SrandmemberCountOperation}
     */
    SrandmemberCountOperation srandmember(byte[] key, long count,
            BytesSetReplyCallback callback);

    /**
     * Creates a SREM operation
     *
     * @param key
     *            the set key
     * @param values
     *            the
     * @param callback
     * @return the new {@link SremOperation}
     */
    SremOperation srem(byte[] key, byte[][] values, LongReplyCallback callback);

    /**
     * Create a SUNION operation
     *
     * @param keys
     *            the keys to union
     * @param callback
     *            the sunion callback
     * @return the new {@link SunionOperation}
     */
    SunionOperation sunion(byte[][] keys, BytesSetReplyCallback callback);

    /**
     * Create a SUNIONSTORE operation
     *
     * @param destination
     *            the destination key
     * @param keys
     *            the set keys to union
     * @param callback
     *            the sunionstore callback
     * @return the new {@link SunionstoreOperation}
     */
    SunionstoreOperation sunionstore(byte[] destination, byte[][] keys,
            LongReplyCallback callback);

    /**
     * Create a ZADD operation
     *
     * @param key
     *            the sorted set key
     * @param entries
     *            the entries to add
     * @param callback
     *            the zadd callback
     * @return the new {@link ZaddOperation}
     */
    ZaddOperation zadd(byte[] key, List<BytesSortedSetEntry> entries,
            LongReplyCallback callback);

    /**
     * Create a ZCARD operation
     *
     * @param key
     *            the key
     * @param callback
     *            the zcard callback
     * @return the new {@link ZcardOperation}
     */
    ZcardOperation zcard(byte[] key, LongReplyCallback callback);

    /**
     * Create a ZCOUNT operation
     *
     * @param key
     *            the key
     * @param min
     *            the min value
     * @param max
     *            the max value
     * @param callback
     *            the zcount operation
     * @return the new {@link ZcountOperation}
     */
    ZcountOperation zcount(byte[] key, double min, double max,
            LongReplyCallback callback);

    /**
     * Create a ZINCRBY operation
     *
     * @param key
     *            the sorted set key
     * @param increment
     *            the value to increment the score by
     * @param member
     *            the value who's score we want to increment
     * @param callback
     *            the zincrby callback
     * @return the new {@link ZincrbyOperation}
     */
    ZincrbyOperation zincrby(byte[] key, double increment, byte[] member,
            DoubleReplyCallback callback);

    /**
     * Create a ZINTERSTORE operation
     *
     * @param destination
     *            the destination key
     * @param keys
     *            keys to intersect
     * @param weights
     *            optional weights for each key
     * @param aggregate
     *            how to aggregate the intersected keys
     * @param callback
     *            the zinterstore callback
     * @return the new {@link ZinterstoreOperation}
     */
    ZinterstoreOperation
            zinterstore(byte[] destination, byte[][] keys, double[] weights,
                    Aggregation aggregate, LongReplyCallback callback);

    /**
     * Create a ZRANGE operation
     *
     * @param key
     *            the key
     * @param start
     *            the start index
     * @param stop
     *            the stop index
     * @param callback
     *            the zrange callback
     * @return the new {@link ZrangeOperation}
     */
    ZrangeOperation zrange(byte[] key, long start, long stop,
            BytesSetReplyCallback callback);

    /**
     * Create a ZRANGE ... WITHSCORES operation
     *
     * @param key
     *            the key
     * @param start
     *            the start index
     * @param stop
     *            the stop index
     * @param callback
     *            the zrangewithscores callback
     * @return the new {@link ZrangeWithScoresOperation}
     */
    ZrangeWithScoresOperation zrangeWithScores(byte[] key, long start,
            long stop, BytesSetWithScoresReplyCallback callback);

    /**
     * Create a ZRANGEBYSCORE operation
     *
     * @param key
     *            the key
     * @param min
     *            the min score
     * @param max
     *            the max score
     * @param limit
     *            optional limit
     * @param count
     *            optional count
     * @param callback
     *            the zrangebyscore callback
     * @return the new {@link ZrangebyscoreOperation}
     */
    ZrangebyscoreOperation zrangebyscores(byte[] key, IntervalValue min,
            IntervalValue max, Long limit, Long count,
            BytesSetReplyCallback callback);

    /**
     * Create a ZRANGEBYSCORE .. WITHSCORES operation
     *
     * @param key
     *            the key
     * @param min
     *            the min score
     * @param max
     *            the max score
     * @param limit
     *            optional limit
     * @param count
     *            optional count
     * @param callback
     *            the zrangebyscore callback
     * @return the new {@link ZrangebyscoreOperation}
     */
    ZrangebyscoreWithScoresOperation zrangebyscoresWithScores(byte[] key,
            IntervalValue min, IntervalValue max, Long limit, Long count,
            BytesSetWithScoresReplyCallback callback);

    /**
     * Create a ZRANK operation
     *
     * @param key
     *            the key
     * @param member
     *            the member to get the rank of
     * @param callback
     *            the zrank callback
     * @return the new {@link ZrankOperation}
     */
    ZrankOperation zrank(byte[] key, byte[] member,
            NullableLongReplyCallback callback);

    /**
     * Create a ZREM operation
     *
     * @param key
     *            the key
     * @param members
     *            the members to remove
     * @param callback
     *            the zrem callback
     * @return the new {@link ZremOperation}
     */
    ZremOperation
            zrem(byte[] key, byte[][] members, LongReplyCallback callback);

    /**
     * Create a ZREMRANGEBYRANK operation
     *
     * @param key
     *            the key
     * @param start
     *            the start index
     * @param stop
     *            the stop index
     * @param callback
     *            the zremrangebyrank callback
     * @return the new {@link ZremrangebyrankOperation}
     */
    ZremrangebyrankOperation zremrangebyrank(byte[] key, long start, long stop,
            LongReplyCallback callback);

    /**
     * Create a ZREMRANGEBYSCORE operation
     *
     * @param key
     *            the key
     * @param min
     *            the min score
     * @param max
     *            the max score
     * @param callback
     *            the zremrangebyscore callback
     * @return the new {@link ZremrangebyscoreOperation}
     */
    ZremrangebyscoreOperation zremrangebyscore(byte[] key, double min,
            double max, LongReplyCallback callback);

    /**
     * Create a ZREVRANGE operation
     *
     * @param key
     *            the key
     * @param start
     *            the start index
     * @param stop
     *            the stop index
     * @param callback
     *            the zrevrange callback
     * @return the new {@link ZrevrangeOperation}
     */
    ZrevrangeOperation zrevrange(byte[] key, long start, long stop,
            BytesSetReplyCallback callback);

    /**
     * Create a ZREVRANGE ... WITHSCORES operation
     *
     * @param key
     *            the key
     * @param start
     *            the start index
     * @param stop
     *            the stop index
     * @param callback
     *            the zrevrangewithscores callback
     * @return the new {@link ZrevrangeWithScoresOperation}
     */
    ZrevrangeWithScoresOperation zrevrangeWithScores(byte[] key, long start,
            long stop, BytesSetWithScoresReplyCallback callback);

    /**
     * Create a ZREVRANGEBYSCORE operation
     *
     * @param key
     *            the key
     * @param min
     *            the min score
     * @param max
     *            the max score
     * @param limit
     *            optional limit
     * @param count
     *            optional count
     * @param callback
     *            the zrevrangebyscore callback
     * @return the new {@link ZrevrangebyscoreOperation}
     */
    ZrevrangebyscoreOperation zrevrangebyscores(byte[] key, IntervalValue min,
            IntervalValue max, Long limit, Long count,
            BytesSetReplyCallback callback);

    /**
     * Create a ZREVRANGEBYSCORE .. WITHSCORES operation
     *
     * @param key
     *            the key
     * @param min
     *            the min score
     * @param max
     *            the max score
     * @param limit
     *            optional limit
     * @param count
     *            optional count
     * @param callback
     *            the zrevrangebyscore callback
     * @return the new {@link ZrevrangebyscoreOperation}
     */
    ZrevrangebyscoreWithScoresOperation zrevrangebyscoresWithScores(byte[] key,
            IntervalValue min, IntervalValue max, Long limit, Long count,
            BytesSetWithScoresReplyCallback callback);

    /**
     * Create a ZREVRANK operation
     *
     * @param key
     *            the key
     * @param member
     *            the member to get the rank of
     * @param callback
     *            the zrevrank callback
     * @return the new {@link ZrevrankOperation}
     */
    ZrevrankOperation zrevrank(byte[] key, byte[] member,
            NullableLongReplyCallback callback);

    /**
     * Create a ZSCORE operation
     *
     * @param key
     *            the key
     * @param member
     *            the member
     * @param callback
     *            the zscore callback
     * @return the new {@link ZscoreOperation}
     */
    ZscoreOperation zscore(byte[] key, byte[] member,
            NullableDoubleReplyCallback callback);

    /**
     * Create a ZUNIONSTORE operation
     *
     * @param destination
     *            the destination key
     * @param keys
     *            keys to intersect
     * @param weights
     *            optional weights for each key
     * @param aggregate
     *            how to aggregate the intersected keys
     * @param callback
     *            the zunionstore callback
     * @return the new {@link ZunionstoreOperation}
     */
    ZunionstoreOperation
            zunionstore(byte[] destination, byte[][] keys, double[] weights,
                    Aggregation aggregate, LongReplyCallback callback);

    /**
     * Create a Ping Operation.
     *
     * @param cb
     *            the ping callback
     * @return the new {@link PingOperation}
     */
    PingOperation ping(StringReplyCallback callback);

    /**
     * Create a pipeline operation
     *
     * @return the new {@link PipelineOperation}
     */
    PipelineOperation pipeline();
}
