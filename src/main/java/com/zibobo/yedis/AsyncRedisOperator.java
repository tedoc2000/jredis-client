package com.zibobo.yedis;

import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.compat.SpyObject;

import com.zibobo.yedis.internal.BlockingPopFuture;
import com.zibobo.yedis.internal.GetFuture;
import com.zibobo.yedis.internal.ListFuture;
import com.zibobo.yedis.internal.MapFuture;
import com.zibobo.yedis.internal.OpFuture;
import com.zibobo.yedis.internal.OperationFuture;
import com.zibobo.yedis.internal.SetFuture;
import com.zibobo.yedis.internal.SetWithScoresFuture;
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
import com.zibobo.yedis.ops.Operation;
import com.zibobo.yedis.ops.OperationCallback;
import com.zibobo.yedis.ops.OperationStatus;
import com.zibobo.yedis.ops.StringListReplyCallback;
import com.zibobo.yedis.ops.StringReplyCallback;
import com.zibobo.yedis.protocol.unified.UnifiedOperationFactory;
import com.zibobo.yedis.transcoder.BytesTranscoder;
import com.zibobo.yedis.transcoder.DoubleTranscoder;
import com.zibobo.yedis.transcoder.LongTranscoder;
import com.zibobo.yedis.transcoder.StringTranscoder;
import com.zibobo.yedis.transcoder.TranscodeService;
import com.zibobo.yedis.transcoder.Transcoder;

public abstract class AsyncRedisOperator extends SpyObject implements
        AsyncStringOperations, AsyncKeyOperations, AsyncHashesOperations,
        AsyncListsOperations, AsyncSetsOperations, AsyncSortedSetsOperations {

    protected static Charset UTF_8 = Charset.forName("UTF-8");
    protected static final byte[] OR_OP = toBytes("OR");
    protected static final byte[] AND_OP = toBytes("AND");
    protected static final byte[] XOR_OP = toBytes("XOR");
    protected static final byte[] NOT_OP = toBytes("NOT");
    protected static final byte[] ENCODING = toBytes("ENCODING");
    protected static final byte[] IDLETIME = toBytes("IDLETIME");
    protected static final byte[] REFCOUNT = toBytes("REFCOUNT");
    protected static final byte[] BEFORE = toBytes("BEFORE");
    protected static final byte[] AFTER = toBytes("AFTER");

    protected static final StringTranscoder stringTranscoder = StringTranscoder
            .getInstance();
    protected static final BytesTranscoder bytesTranscoder = BytesTranscoder
            .getInstance();
    protected static final LongTranscoder longTranscoder = LongTranscoder
            .getInstance();
    protected static final DoubleTranscoder doubleTranscoder = DoubleTranscoder
            .getInstance();

    protected static final Random rand = new Random();

    protected final long operationTimeout;
    protected final RedisConnection rconn;
    protected final OperationFactory opFact;
    protected final TranscodeService tcService;
    protected final ExecutorService executorService;

    protected AsyncRedisOperator(ConnectionFactory cf, RedisConnection rconn) {
        operationTimeout = cf.getOperationTimeout();
        this.rconn = rconn;
        executorService = cf.getListenerExecutorService();
        tcService = new TranscodeService(cf.isDaemon());
        opFact = new UnifiedOperationFactory();
    }

    abstract protected <T> Future<T> addOperation(String key,
            OpFuture<T> opFuture);

    abstract protected <T> Future<T> addWriteOperation(OpFuture<T> opFuture);

    @Override
    public Future<Integer> asyncAppend(String key, String data) {
        return asyncAppend(key, data, stringTranscoder);
    }

    @Override
    public Future<Integer> asyncAppend(String key, byte[] data) {
        return asyncAppend(key, data, bytesTranscoder);
    }

    @Override
    public <T> Future<Integer> asyncAppend(String key, T data,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeAppendOperation(key, data, transcoder));
    }

    protected <T> OpFuture<Integer> makeAppendOperation(String key, T data,
            Transcoder<T> transcoder) {
        IntegerCallback cb =
                new IntegerCallback(operationTimeout, executorService);
        Operation op = opFact.append(toBytes(key), transcoder.encode(data), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Integer>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncBitcount(String key) {
        return addOperation(key, makeBitcountOperation(key));
    }

    protected OpFuture<Long> makeBitcountOperation(String key) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);

        Operation op = opFact.bitcount(toBytes(key), null, null, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncBitcount(String key, int start, int end) {
        return addOperation(key, makeBitcountOperation(key, start, end));
    }

    protected OpFuture<Long> makeBitcountOperation(String key, int start,
            int end) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.bitcount(toBytes(key), start, end, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Integer> asyncBitOpAnd(String dstKey, String... srcKeys) {
        return bitop(AND_OP, dstKey, srcKeys);
    }

    @Override
    public Future<Integer>
            asyncBitOpAnd(String dstKey, Iterator<String> srcKeys) {
        return bitop(AND_OP, dstKey, srcKeys);
    }

    @Override
    public Future<Integer>
            asyncBitOpAnd(String dstKey, Iterable<String> srcKeys) {
        return bitop(AND_OP, dstKey, srcKeys);
    }

    @Override
    public Future<Integer> asyncBitOpOr(String dstKey, String... srcKeys) {
        return bitop(OR_OP, dstKey, srcKeys);
    }

    @Override
    public Future<Integer>
            asyncBitOpOr(String dstKey, Iterator<String> srcKeys) {
        return bitop(OR_OP, dstKey, srcKeys);
    }

    @Override
    public Future<Integer>
            asyncBitOpOr(String dstKey, Iterable<String> srcKeys) {
        return bitop(OR_OP, dstKey, srcKeys);
    }

    @Override
    public Future<Integer> asyncBitOpXOr(String dstKey, String... srcKeys) {
        return bitop(XOR_OP, dstKey, srcKeys);
    }

    @Override
    public Future<Integer>
            asyncBitOpXOr(String dstKey, Iterator<String> srcKeys) {
        return bitop(XOR_OP, dstKey, srcKeys);
    }

    @Override
    public Future<Integer>
            asyncBitOpXOr(String dstKey, Iterable<String> srcKeys) {
        return bitop(XOR_OP, dstKey, srcKeys);
    }

    @Override
    public Future<Integer> asyncBitOpNot(String dstKey, String srcKey) {
        return bitop(NOT_OP, dstKey, srcKey);
    }

    protected Future<Integer> bitop(byte[] bitop, String dstKey,
            String... srcKeys) {
        return addWriteOperation(makeBitopOperation(bitop, dstKey,
                toBytes(srcKeys)));
    }

    protected Future<Integer> bitop(byte[] bitop, String dstKey,
            Iterable<String> srcKeys) {
        return bitop(bitop, dstKey, srcKeys.iterator());
    }

    protected Future<Integer> bitop(byte[] bitop, String dstKey,
            Iterator<String> srcKeys) {
        return addWriteOperation(makeBitopOperation(bitop, dstKey,
                toBytes(srcKeys)));
    }

    protected OpFuture<Integer> makeBitopOperation(byte[] bitop, String dstKey,
            byte[][] srcKeys) {
        IntegerCallback cb =
                new IntegerCallback(operationTimeout, executorService);
        Operation op = opFact.bitop(bitop, toBytes(dstKey), srcKeys, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Integer>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncDecr(String key) {
        return addWriteOperation(makeDecrOperation(key));
    }

    protected OpFuture<Long> makeDecrOperation(String key) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.decr(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncDecrBy(String key, long by) {
        return addWriteOperation(makeDecrByOperation(key, by));
    }

    protected OpFuture<Long> makeDecrByOperation(String key, long by) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.decrBy(toBytes(key), by, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<String> asyncGet(String key) {
        return asyncGet(key, stringTranscoder);
    }

    @Override
    public Future<byte[]> asyncGetAsBytes(String key) {
        return asyncGet(key, bytesTranscoder);
    }

    @Override
    public <T> Future<T> asyncGet(String key, final Transcoder<T> transcoder) {
        return addOperation(key, makeGetOperation(key, transcoder));
    }

    protected <T> OpFuture<T> makeGetOperation(String key,
            final Transcoder<T> transcoder) {
        TranscodedBytesCallback<T> cb =
                new TranscodedBytesCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);

        Operation op = opFact.get(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<T>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncGetbit(String key, long offset) {
        return addOperation(key, makeGetbitOperation(key, offset));
    }

    protected OpFuture<Boolean> makeGetbitOperation(String key, long offset) {
        BooleanCallback cb = new BooleanCallback(offset, executorService);

        Operation op = opFact.getbit(toBytes(key), offset, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<String> asyncGetrange(String key, int start, int end) {
        return asyncGetrange(key, start, end, stringTranscoder);
    }

    @Override
    public Future<byte[]> asyncGetrangeAsBytes(String key, int start, int end) {
        return asyncGetrange(key, start, end, bytesTranscoder);
    }

    @Override
    public <T> Future<T> asyncGetrange(String key, int start, int end,
            Transcoder<T> transcoder) {
        return addOperation(key,
                makeGetrangeOperation(key, start, end, transcoder));
    }

    protected <T> OpFuture<T> makeGetrangeOperation(String key, int start,
            int end, final Transcoder<T> transcoder) {
        TranscodedBytesCallback<T> cb =
                new TranscodedBytesCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.getrange(toBytes(key), start, end, cb);
        cb.rv.setOperation(op);
        return new OpFuture<T>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<String> asyncGetset(String key, String newValue) {
        return asyncGetset(key, newValue, stringTranscoder);
    }

    @Override
    public Future<byte[]> asyncGetset(String key, byte[] newValue) {
        return asyncGetset(key, newValue, bytesTranscoder);
    }

    @Override
    public <T> Future<T> asyncGetset(String key, T data,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeGetsetOperation(key, data, transcoder));
    }

    protected <T> OpFuture<T> makeGetsetOperation(String key, T data,
            final Transcoder<T> transcoder) {
        TranscodedBytesCallback<T> cb =
                new TranscodedBytesCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.getset(toBytes(key), transcoder.encode(data), cb);

        cb.rv.setOperation(op);
        return new OpFuture<T>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncIncr(String key) {
        return addWriteOperation(makeIncrOperation(key));
    }

    protected OpFuture<Long> makeIncrOperation(String key) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.incr(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncIncrBy(String key, long by) {
        return addWriteOperation(makeIncrByOperation(key, by));
    }

    protected OpFuture<Long> makeIncrByOperation(String key, long by) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.incrBy(toBytes(key), by, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Double> asyncIncrByFloat(String key, double by) {
        return addWriteOperation(makeIncrByFloatOperation(key, by));
    }

    protected OpFuture<Double> makeIncrByFloatOperation(String key, double by) {
        DoubleCallback cb =
                new DoubleCallback(operationTimeout, executorService);
        Operation op = opFact.incrByFloat(toBytes(key), by, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Double>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<List<String>> asyncMget(String... keys) {
        return asyncMget(stringTranscoder, keys);
    }

    @Override
    public Future<List<String>> asyncMget(Iterator<String> keys) {
        return asyncMget(stringTranscoder, keys);
    }

    @Override
    public Future<List<String>> asyncMget(Iterable<String> keys) {
        return asyncMget(stringTranscoder, keys);
    }

    @Override
    public Future<List<byte[]>> asyncMgetAsBytes(String... keys) {
        return asyncMget(bytesTranscoder, keys);
    }

    @Override
    public Future<List<byte[]>> asyncMgetAsBytes(Iterator<String> keys) {
        return asyncMget(bytesTranscoder, keys);
    }

    @Override
    public Future<List<byte[]>> asyncMgetAsBytes(Iterable<String> keys) {
        return asyncMget(bytesTranscoder, keys);
    }

    @Override
    public <T> Future<List<T>> asyncMget(Transcoder<T> transcoder,
            String... keys) {
        FirstKeyBytes fkb = toFirstKeyBytes(keys);

        return addOperation(fkb.firstKey, makeMgetOperation(fkb, transcoder));
    }

    @Override
    public <T> Future<List<T>> asyncMget(Transcoder<T> transcoder,
            Iterator<String> keys) {
        FirstKeyBytes fkb = toFirstKeyBytes(keys);

        return addOperation(fkb.firstKey, makeMgetOperation(fkb, transcoder));
    }

    @Override
    public <T> Future<List<T>> asyncMget(Transcoder<T> transcoder,
            Iterable<String> keys) {
        return asyncMget(transcoder, keys.iterator());
    }

    protected <T> OpFuture<List<T>> makeMgetOperation(FirstKeyBytes fkb,
            Transcoder<T> transcoder) {
        TranscodedListCallback<T> cb =
                new TranscodedListCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);

        Operation op = opFact.mget(fkb.bytes, cb);
        cb.rv.setOperation(op);
        return new OpFuture<List<T>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<List<Object>> asyncMget(Map<String, Transcoder<?>> keys) {
        FirstKeyBytes fkb = toFirstKeyBytes(keys.keySet().iterator());

        return addOperation(fkb.firstKey, makeMgetOperation(fkb, keys));
    }

    protected OpFuture<List<Object>> makeMgetOperation(FirstKeyBytes fkb,
            final Map<String, Transcoder<?>> keyTranscoders) {
        TranscodedListCallback<Object> cb =
                new TranscodedListCallback<Object>(
                        new ListValueTranscoder<Object>() {
                            private final Iterator<Transcoder<?>> i =
                                    keyTranscoders.values().iterator();

                            @SuppressWarnings("unchecked")
                            @Override
                            public Future<Object> getFuture(
                                    TranscodeService tcService, byte[] object) {
                                return (Future<Object>) tcService.decode(
                                        i.next(), object);
                            }

                        }, tcService, operationTimeout, executorService);

        Operation op = opFact.mget(fkb.bytes, cb);
        cb.rv.setOperation(op);
        return new OpFuture<List<Object>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Void> asyncMset(String... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "Argument must be an even number of strings.");
        }

        return addWriteOperation(makeMsetOperation(toBytes(keyValues)));
    }

    @Override
    public Future<Void> asyncMset(Map<String, String> keyValues) {
        return asyncMset(keyValues, stringTranscoder);
    }

    @Override
    public Future<Void> asyncMsetBytes(Map<String, byte[]> keyValues) {
        return asyncMset(keyValues, bytesTranscoder);
    }

    @Override
    public <T> Future<Void> asyncMset(Map<String, T> keyValues,
            Transcoder<T> transcoder) {
        byte[][] bytes = new byte[keyValues.size() * 2][];
        int index = 0;
        for (Map.Entry<String, T> entry : keyValues.entrySet()) {
            bytes[index++] = toBytes(entry.getKey());
            bytes[index++] = transcoder.encode(entry.getValue());
        }
        return addWriteOperation(makeMsetOperation(bytes));
    }

    protected OpFuture<Void> makeMsetOperation(byte[][] keyvalues) {

        VoidCallback cb = new VoidCallback(operationTimeout, executorService);
        Operation op = opFact.mset(keyvalues, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Void>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncMsetnx(String... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "Argument must be an even number of strings.");
        }

        return addWriteOperation(makeMsetnxOperation(toBytes(keyValues)));
    }

    @Override
    public Future<Boolean> asyncMsetnx(Map<String, String> keyValues) {
        return asyncMsetnx(keyValues, stringTranscoder);
    }

    @Override
    public Future<Boolean> asyncMsetnxBytes(Map<String, byte[]> keyValues) {
        return asyncMsetnx(keyValues, bytesTranscoder);
    }

    @Override
    public <T> Future<Boolean> asyncMsetnx(Map<String, T> keyValues,
            Transcoder<T> transcoder) {
        byte[][] bytes = new byte[keyValues.size() * 2][];
        int index = 0;
        for (Map.Entry<String, T> entry : keyValues.entrySet()) {
            bytes[index++] = toBytes(entry.getKey());
            bytes[index++] = transcoder.encode(entry.getValue());
        }
        return addWriteOperation(makeMsetnxOperation(bytes));
    }

    protected OpFuture<Boolean> makeMsetnxOperation(byte[][] keyvalues) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op = opFact.msetnx(keyvalues, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Void> asyncPsetex(String key, String value, long expire) {
        return asyncPsetex(key, value, expire, stringTranscoder);
    }

    @Override
    public Future<Void> asyncPsetex(String key, byte[] value, long expire) {
        return asyncPsetex(key, value, expire, bytesTranscoder);
    }

    @Override
    public <T> Future<Void> asyncPsetex(String key, T value, long expire,
            Transcoder<T> transcoder) {
        return addWriteOperation(makePsetexOperation(key,
                transcoder.encode(value), expire));
    }

    protected OpFuture<Void> makePsetexOperation(String key, byte[] value,
            long expire) {
        VoidCallback cb = new VoidCallback(expire, executorService);
        Operation op = opFact.psetex(toBytes(key), value, expire, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Void>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Void> asyncSet(String key, String value) {
        return asyncSet(key, value, stringTranscoder);
    }

    @Override
    public Future<Void> asyncSet(String key, byte[] value) {
        return asyncSet(key, value, bytesTranscoder);
    }

    @Override
    public <T> Future<Void> asyncSet(String key, T value,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeSetOperation(key,
                transcoder.encode(value), null, -1L));
    }

    @Override
    public Future<Boolean> asyncSet(String key, String value,
            Exclusiveness exclusive) {
        return asyncSet(key, value, exclusive, stringTranscoder);
    }

    @Override
    public Future<Boolean> asyncSet(String key, byte[] value,
            Exclusiveness exclusive) {
        return asyncSet(key, value, exclusive, bytesTranscoder);
    }

    @Override
    public <T> Future<Boolean> asyncSet(String key, T value,
            Exclusiveness exclusive, Transcoder<T> transcoder) {
        return addWriteOperation(makeSetOperation(key,
                transcoder.encode(value), exclusive));
    }

    @Override
    public Future<Void> asyncSet(String key, String value,
            ExpirationType expType, long expiration) {
        return asyncSet(key, value, expType, expiration, stringTranscoder);
    }

    @Override
    public Future<Void> asyncSet(String key, byte[] value,
            ExpirationType expType, long expiration) {
        return asyncSet(key, value, expType, expiration, bytesTranscoder);
    }

    @Override
    public <T> Future<Void> asyncSet(String key, T value,
            ExpirationType expType, long expiration, Transcoder<T> transcoder) {
        return addWriteOperation(makeSetOperation(key,
                transcoder.encode(value), expType, expiration));
    }

    @Override
    public Future<Boolean> asyncSet(String key, String value,
            Exclusiveness exclusive, ExpirationType expType, long expiration) {
        return asyncSet(key, value, exclusive, expType, expiration,
                stringTranscoder);
    }

    @Override
    public Future<Boolean> asyncSet(String key, byte[] value,
            Exclusiveness exclusive, ExpirationType expType, long expiration) {
        return asyncSet(key, value, exclusive, expType, expiration,
                bytesTranscoder);
    }

    @Override
    public <T> Future<Boolean> asyncSet(String key, T value,
            Exclusiveness exclusive, ExpirationType expType, long expiration,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeSetOperation(key,
                transcoder.encode(value), exclusive, expType, expiration));
    }

    protected OpFuture<Boolean> makeSetOperation(String key, byte[] value,
            Exclusiveness exclusiveness) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op =
                opFact.set(toBytes(key), value, exclusiveness, null, -1, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    protected OpFuture<Void> makeSetOperation(String key, byte[] value,
            ExpirationType expirationType, long expire) {
        VoidBooleanCallback cb =
                new VoidBooleanCallback(expire, executorService);
        Operation op =
                opFact.set(toBytes(key), value, expirationType, expire, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Void>(op, cb.rv, operationTimeout);
    }

    protected OpFuture<Boolean> makeSetOperation(String key, byte[] value,
            Exclusiveness exclusiveness, ExpirationType expirationType,
            long expire) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op =
                opFact.set(toBytes(key), value, exclusiveness, expirationType,
                        expire, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncSetbit(String key, long offset) {
        return addWriteOperation(makeSetbitOperation(key, offset));
    }

    protected OpFuture<Boolean> makeSetbitOperation(String key, long offset) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op = opFact.setbit(toBytes(key), offset, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Void> asyncSetex(String key, String value, long expire) {
        return asyncSetex(key, value, expire, stringTranscoder);
    }

    @Override
    public Future<Void> asyncSetex(String key, byte[] value, long expire) {
        return asyncSetex(key, value, expire, bytesTranscoder);
    }

    @Override
    public <T> Future<Void> asyncSetex(String key, T value, long expire,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeSetexOperation(key,
                transcoder.encode(value), expire));
    }

    protected OpFuture<Void> makeSetexOperation(String key, byte[] value,
            long expire) {
        VoidCallback cb = new VoidCallback(expire, executorService);
        Operation op = opFact.setex(toBytes(key), value, expire, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Void>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncSetnx(String key, String value) {
        return asyncSetnx(key, value, stringTranscoder);
    }

    @Override
    public Future<Boolean> asyncSetnx(String key, byte[] value) {
        return asyncSetnx(key, value, bytesTranscoder);
    }

    @Override
    public <T> Future<Boolean> asyncSetnx(String key, T value,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeSetnxOperation(key,
                transcoder.encode(value)));
    }

    protected OpFuture<Boolean> makeSetnxOperation(String key, byte[] value) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op = opFact.setnx(toBytes(key), value, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Integer> asyncSetrange(String key, int offset, String data) {
        return asyncSetrange(key, offset, data, stringTranscoder);
    }

    @Override
    public Future<Integer> asyncSetrange(String key, int offset, byte[] data) {
        return asyncSetrange(key, offset, data, bytesTranscoder);
    }

    @Override
    public <T> Future<Integer> asyncSetrange(String key, int offset, T data,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeSetrangeOperation(key, offset, data,
                transcoder));
    }

    protected <T> OpFuture<Integer> makeSetrangeOperation(String key,
            int offset, T data, Transcoder<T> transcoder) {
        IntegerCallback cb = new IntegerCallback(offset, executorService);
        Operation op =
                opFact.setrange(toBytes(key), offset, transcoder.encode(data),
                        cb);
        cb.rv.setOperation(op);
        return new OpFuture<Integer>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Integer> asyncStrlen(String key) {
        return addOperation(key, makeStrlenOperation(key));
    }

    protected <T> OpFuture<Integer> makeStrlenOperation(String key) {
        IntegerCallback cb =
                new IntegerCallback(operationTimeout, executorService);
        Operation op = opFact.strlen(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Integer>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Integer> asyncDel(String... keys) {
        return addWriteOperation(makeDelOperation(toBytes(keys)));
    }

    @Override
    public Future<Integer> asyncDel(Iterator<String> keys) {

        return addWriteOperation(makeDelOperation(toBytes(keys)));
    }

    @Override
    public Future<Integer> asyncDel(Iterable<String> keys) {
        return asyncDel(keys.iterator());
    }

    protected OpFuture<Integer> makeDelOperation(byte[][] keys) {
        IntegerCallback cb =
                new IntegerCallback(operationTimeout, executorService);
        Operation op = opFact.del(keys, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Integer>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<byte[]> asyncDump(String key) {
        return addOperation(key, makeDumpOperation(key));
    }

    protected OpFuture<byte[]> makeDumpOperation(String key) {
        BytesCallback cb = new BytesCallback(operationTimeout, executorService);
        Operation op = opFact.dump(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<byte[]>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncExists(String key) {
        return addOperation(key, makeExistsOperation(key));
    }

    protected OpFuture<Boolean> makeExistsOperation(String key) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op = opFact.exists(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncExpire(String key, int ttl) {
        return addWriteOperation(makeExpireOperation(key, ttl));
    }

    protected OpFuture<Boolean> makeExpireOperation(String key, int seconds) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op = opFact.expire(toBytes(key), seconds, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncExpireAt(String key, int timestamp) {
        return addWriteOperation(makeExpireAtOperation(key, timestamp));
    }

    protected OpFuture<Boolean>
            makeExpireAtOperation(String key, int timestamp) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op = opFact.expireat(toBytes(key), timestamp, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Set<String>> asyncKeys(String pattern) {
        return addOperation(pattern, makeKeysOperation(pattern));
    }

    protected OpFuture<Set<String>> makeKeysOperation(String pattern) {
        TranscodedSetCallback<String> cb =
                new TranscodedSetCallback<String>(stringTranscoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.keys(toBytes(pattern), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Set<String>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Void> asyncMigrate(String host, int port, String key, int db,
            int timeout) {
        return asyncMigrate(host, port, key, db, timeout, false, false);
    }

    @Override
    public Future<Void> asyncMigrateCopy(String host, int port, String key,
            int db, int timeout) {
        return asyncMigrate(host, port, key, db, timeout, true, false);
    }

    @Override
    public Future<Void> asyncMigrateReplace(String host, int port, String key,
            int db, int timeout) {
        return asyncMigrate(host, port, key, db, timeout, false, true);
    }

    @Override
    public Future<Void> asyncMigrate(String host, int port, String key, int db,
            int timeout, boolean copy, boolean replace) {
        return addWriteOperation(makeMigrateOperation(host, port, key, db,
                timeout, copy, replace));
    }

    protected OpFuture<Void> makeMigrateOperation(String host, int port,
            String key, int db, int timeout, boolean copy, boolean replace) {
        VoidCallback cb = new VoidCallback(timeout, executorService);
        Operation op =
                opFact.migrate(toBytes(host), port, toBytes(key), db, timeout,
                        copy, replace, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Void>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncMove(String key, int db) {
        return addWriteOperation(makeMoveOperation(key, db));
    }

    protected OpFuture<Boolean> makeMoveOperation(String key, int db) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op = opFact.move(toBytes(key), db, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<String> asyncObjectEncoding(String key) {
        return addOperation(key, makeObjectStringOperation(ENCODING, key));
    }

    @Override
    public Future<Integer> asyncObjectIdletime(String key) {
        return addOperation(key, makeObjectIntegerOperation(IDLETIME, key));
    }

    @Override
    public Future<Integer> asyncObjectRefcount(String key) {
        return addOperation(key, makeObjectIntegerOperation(REFCOUNT, key));
    }

    protected OpFuture<Integer> makeObjectIntegerOperation(byte[] cmd,
            String key) {
        IntegerCallback cb =
                new IntegerCallback(operationTimeout, executorService);
        Operation op = opFact.object(cmd, toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Integer>(op, cb.rv, operationTimeout);
    }

    protected OpFuture<String>
            makeObjectStringOperation(byte[] cmd, String key) {
        StringCallback cb =
                new StringCallback(operationTimeout, executorService);
        Operation op = opFact.object(cmd, toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<String>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncPersist(String key) {
        return addWriteOperation(makePeristOperation(key));
    }

    protected OpFuture<Boolean> makePeristOperation(String key) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op = opFact.persist(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncPexpire(String key, long ttl) {
        return addWriteOperation(makePexpireOperation(key, ttl));
    }

    protected OpFuture<Boolean> makePexpireOperation(String key, long ttl) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op = opFact.pexpire(toBytes(key), ttl, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncPexpireAt(String key, long timestamp) {
        return addWriteOperation(makePexpireAtOperation(key, timestamp));
    }

    protected OpFuture<Boolean> makePexpireAtOperation(String key,
            long timestamp) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op = opFact.pexpireat(toBytes(key), timestamp, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncPttl(String key) {
        return addWriteOperation(makePttlOperation(key));
    }

    protected OpFuture<Long> makePttlOperation(String key) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.pttl(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Void> asyncRename(String key, String newKey) {
        return addWriteOperation(makeRenameOperation(key, newKey));
    }

    protected OpFuture<Void> makeRenameOperation(String key, String newKey) {
        VoidBooleanCallback cb =
                new VoidBooleanCallback(operationTimeout, executorService);
        Operation op = opFact.rename(toBytes(key), toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Void>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncRenamenx(String key, String newKey) {
        return addWriteOperation(makeRenamenxOperation(key, newKey));
    }

    protected OpFuture<Boolean>
            makeRenamenxOperation(String key, String newKey) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op = opFact.renamenx(toBytes(key), toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Void> asyncRestore(String key, long ttl, byte[] data) {
        return addWriteOperation(makeRestoreOperation(key, ttl, data));
    }

    protected OpFuture<Void> makeRestoreOperation(String key, long ttl,
            byte[] data) {
        VoidBooleanCallback cb =
                new VoidBooleanCallback(operationTimeout, executorService);
        Operation op = opFact.restore(toBytes(key), ttl, data, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Void>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<String> asyncRandomkey() {
        return addOperation("", makeRandomkeyOperation());
    }

    protected OpFuture<String> makeRandomkeyOperation() {
        StringCallback cb =
                new StringCallback(operationTimeout, executorService);
        Operation op = opFact.randomkey(cb);
        cb.rv.setOperation(op);
        return new OpFuture<String>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<List<String>> asyncSort(String key) {
        return asyncSort(key, stringTranscoder, true);
    }

    @Override
    public Future<List<String>> asyncSortDesc(String key) {
        return asyncSortDesc(key, stringTranscoder, true);
    }

    @Override
    public Future<List<String>>
            asyncSortLimit(String key, int offset, int count) {
        return asyncSortLimit(key, offset, count, stringTranscoder, true);
    }

    @Override
    public Future<List<String>> asyncSortBy(String key, String by) {
        return asyncSortBy(key, by, stringTranscoder, true);
    }

    @Override
    public Future<List<String>> asyncSortGet(String key, String get) {
        return asyncSortGet(key, get, stringTranscoder, true);
    }

    @Override
    public Future<List<String>> asyncSortMultiget(String key, String... get) {
        return asyncSortMultiget(key, stringTranscoder, true, get);
    }

    @Override
    public Future<List<String>> asyncSortMultiget(String key,
            Iterable<String> get) {
        return asyncSortMultiget(key, get, stringTranscoder, true);
    }

    @Override
    public Future<List<String>> asyncSortMultiget(String key,
            Iterator<String> get) {
        return asyncSortMultiget(key, get, stringTranscoder, true);
    }

    @Override
    public Future<List<String>> asyncSort(String key, SortOptions options) {
        return asyncSort(key, options, stringTranscoder, true);
    }

    @Override
    public Future<List<byte[]>> asyncSortBytes(String key) {
        return asyncSort(key, bytesTranscoder, true);
    }

    @Override
    public Future<List<byte[]>> asyncSortDescBytes(String key) {
        return asyncSortDesc(key, bytesTranscoder, true);
    }

    @Override
    public Future<List<byte[]>> asyncSortLimitBytes(String key, int offset,
            int count) {
        return asyncSortLimit(key, offset, count, bytesTranscoder, true);
    }

    @Override
    public Future<List<byte[]>> asyncSortByBytes(String key, String by) {
        return asyncSortBy(key, by, bytesTranscoder, true);
    }

    @Override
    public Future<List<byte[]>> asyncSortGetBytes(String key, String get) {
        return asyncSortGet(key, get, bytesTranscoder, true);
    }

    @Override
    public Future<List<byte[]>> asyncSortMultigetBytes(String key,
            String... get) {
        return asyncSortMultiget(key, bytesTranscoder, true, get);
    }

    @Override
    public Future<List<byte[]>> asyncSortMultigetBytes(String key,
            Iterable<String> get) {
        return asyncSortMultiget(key, get, bytesTranscoder, true);
    }

    @Override
    public Future<List<byte[]>> asyncSortMultigetBytes(String key,
            Iterator<String> get) {
        return asyncSortMultiget(key, get, bytesTranscoder, true);
    }

    @Override
    public Future<List<byte[]>> asyncSortBytes(String key, SortOptions options) {
        return asyncSort(key, options, bytesTranscoder, true);
    }

    @Override
    public <T> Future<List<T>> asyncSort(String key, Transcoder<T> transcoder,
            boolean alpha) {
        SortOptions options = new SortOptions();
        return asyncSort(key, options, transcoder, alpha);
    }

    @Override
    public <T> Future<List<T>> asyncSortDesc(String key,
            Transcoder<T> transcoder, boolean alpha) {
        SortOptions options = new SortOptions();
        options.desc = true;
        return asyncSort(key, options, transcoder, alpha);
    }

    @Override
    public <T> Future<List<T>> asyncSortLimit(String key, int offset,
            int count, Transcoder<T> transcoder, boolean alpha) {
        SortOptions options = new SortOptions();
        options.offset = offset;
        options.count = count;
        return asyncSort(key, options, transcoder, alpha);
    }

    @Override
    public <T> Future<List<T>> asyncSortBy(String key, String by,
            Transcoder<T> transcoder, boolean alpha) {
        SortOptions options = new SortOptions();
        options.by = by;
        return asyncSort(key, options, transcoder, alpha);
    }

    @Override
    public <T> Future<List<T>> asyncSortGet(String key, String get,
            Transcoder<T> transcoder, boolean alpha) {
        SortOptions options = new SortOptions();
        options.get = get;
        return asyncSort(key, options, transcoder, alpha);
    }

    @Override
    public <T> Future<List<T>> asyncSortMultiget(String key,
            Transcoder<T> transcoder, boolean alpha, String... get) {
        SortOptions options = new SortOptions();
        options.multiGet = Arrays.asList(get);
        return asyncSort(key, options, transcoder, alpha);
    }

    @Override
    public <T> Future<List<T>> asyncSortMultiget(String key,
            Iterable<String> get, Transcoder<T> transcoder, boolean alpha) {
        return asyncSortMultiget(key, get.iterator(), transcoder, alpha);
    }

    @Override
    public <T> Future<List<T>> asyncSortMultiget(String key,
            Iterator<String> get, Transcoder<T> transcoder, boolean alpha) {
        SortOptions options = new SortOptions();
        options.multiGet = new ArrayList<String>();
        while (get.hasNext()) {
            options.multiGet.add(get.next());
        }
        return asyncSort(key, options, transcoder, alpha);
    }

    @Override
    public <T> Future<List<T>> asyncSort(String key, SortOptions options,
            Transcoder<T> transcoder, boolean alpha) {
        return addOperation(key,
                makeSortOperation(key, alpha, options, transcoder));
    }

    @Override
    public Future<List<Long>> asyncSortLong(String key) {
        return asyncSort(key, longTranscoder, false);
    }

    @Override
    public Future<List<Long>> asyncSortDescLong(String key) {
        return asyncSortDesc(key, longTranscoder, false);
    }

    @Override
    public Future<List<Long>> asyncSortLimitLong(String key, int offset,
            int count) {
        return asyncSortLimit(key, offset, count, longTranscoder, false);
    }

    @Override
    public Future<List<Long>> asyncSortByLong(String key, String by) {
        return asyncSortBy(key, by, longTranscoder, false);
    }

    @Override
    public Future<List<Long>> asyncSortGetLong(String key, String get) {
        return asyncSortGet(key, get, longTranscoder, false);
    }

    @Override
    public Future<List<Long>> asyncSortMultigetLong(String key, String... get) {
        return asyncSortMultiget(key, longTranscoder, false, get);
    }

    @Override
    public Future<List<Long>> asyncSortMultigetLong(String key,
            Iterable<String> get) {
        return asyncSortMultiget(key, get, longTranscoder, false);
    }

    @Override
    public Future<List<Long>> asyncSortMultigetLong(String key,
            Iterator<String> get) {
        return asyncSortMultiget(key, get, longTranscoder, false);
    }

    @Override
    public Future<List<Long>> asyncSortLong(String key, SortOptions options) {
        return asyncSort(key, options, longTranscoder, false);
    }

    @Override
    public Future<List<Double>> asyncSortDouble(String key) {
        return asyncSort(key, doubleTranscoder, false);
    }

    @Override
    public Future<List<Double>> asyncSortDescDouble(String key) {
        return asyncSortDesc(key, doubleTranscoder, false);
    }

    @Override
    public Future<List<Double>> asyncSortLimitDouble(String key, int offset,
            int count) {
        return asyncSortLimit(key, offset, count, doubleTranscoder, false);
    }

    @Override
    public Future<List<Double>> asyncSortByDouble(String key, String by) {
        return asyncSortBy(key, by, doubleTranscoder, false);
    }

    @Override
    public Future<List<Double>> asyncSortGetDouble(String key, String get) {
        return asyncSortGet(key, get, doubleTranscoder, false);
    }

    @Override
    public Future<List<Double>> asyncSortMultigetDouble(String key,
            String... get) {
        return asyncSortMultiget(key, doubleTranscoder, false, get);
    }

    @Override
    public Future<List<Double>> asyncSortMultigetDouble(String key,
            Iterable<String> get) {
        return asyncSortMultiget(key, get, doubleTranscoder, false);
    }

    @Override
    public Future<List<Double>> asyncSortMultigetDouble(String key,
            Iterator<String> get) {
        return asyncSortMultiget(key, get, doubleTranscoder, false);
    }

    @Override
    public Future<List<Double>>
            asyncSortDouble(String key, SortOptions options) {
        return asyncSort(key, options, doubleTranscoder, false);
    }

    protected <T> OpFuture<List<T>> makeSortOperation(String key,
            boolean alpha, SortOptions options, final Transcoder<T> transcoder) {
        TranscodedListCallback<T> cb =
                new TranscodedListCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.sort(toBytes(key), alpha, options, cb);
        cb.rv.setOperation(op);
        return new OpFuture<List<T>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncSortStore(String key, boolean alpha,
            String storeKey) {
        SortOptions options = new SortOptions();
        return asyncSortStore(key, options, alpha, storeKey);
    }

    @Override
    public Future<Long> asyncSortDescStore(String key, boolean alpha,
            String storeKey) {
        SortOptions options = new SortOptions();
        options.desc = true;
        return asyncSortStore(key, options, alpha, storeKey);
    }

    @Override
    public Future<Long> asyncSortLimitStore(String key, int offset, int count,
            boolean alpha, String storeKey) {
        SortOptions options = new SortOptions();
        options.offset = offset;
        options.count = count;
        return asyncSortStore(key, options, alpha, storeKey);
    }

    @Override
    public Future<Long> asyncSortByStore(String key, String by, boolean alpha,
            String storeKey) {
        SortOptions options = new SortOptions();
        options.by = by;
        return asyncSortStore(key, options, alpha, storeKey);
    }

    @Override
    public Future<Long> asyncSortGetStore(String key, String get,
            boolean alpha, String storeKey) {
        SortOptions options = new SortOptions();
        options.get = get;
        return asyncSortStore(key, options, alpha, storeKey);
    }

    @Override
    public Future<Long> asyncSortMultigetStore(String key, boolean alpha,
            String storeKey, String... get) {
        return asyncSortMultigetStore(key, Arrays.asList(get), alpha, storeKey);
    }

    @Override
    public Future<Long> asyncSortMultigetStore(String key,
            Iterable<String> get, boolean alpha, String storeKey) {
        return asyncSortMultigetStore(key, get.iterator(), alpha, storeKey);
    }

    @Override
    public Future<Long> asyncSortMultigetStore(String key,
            Iterator<String> get, boolean alpha, String storeKey) {
        SortOptions options = new SortOptions();
        options.multiGet = new ArrayList<String>();
        while (get.hasNext()) {
            options.multiGet.add(get.next());
        }
        return asyncSortStore(key, options, alpha, storeKey);
    }

    @Override
    public Future<Long> asyncSortStore(String key, SortOptions options,
            boolean alpha, String storeKey) {
        return addWriteOperation(makeSortOperation(key, storeKey, alpha,
                options));
    }

    protected OpFuture<Long> makeSortOperation(String key, String store,
            boolean alpha, SortOptions options) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op =
                opFact.sort(toBytes(key), toBytes(store), alpha, options, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Integer> asyncTtl(String key) {
        return addOperation(key, makeTTLOperation(key));
    }

    protected OpFuture<Integer> makeTTLOperation(String key) {
        IntegerCallback cb =
                new IntegerCallback(operationTimeout, executorService);
        Operation op = opFact.ttl(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Integer>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<String> asyncType(String key) {
        return addOperation(key, makeTypeOperation(key));
    }

    public OpFuture<String> makeTypeOperation(String key) {
        StringCallback cb =
                new StringCallback(operationTimeout, executorService);
        Operation op = opFact.type(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<String>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Integer> asyncHdel(String key, String... fields) {
        return addWriteOperation(makeHdelOperation(key, toBytes(fields)));
    }

    @Override
    public Future<Integer> asyncHdel(String key, Iterable<String> fields) {
        return asyncHdel(key, fields.iterator());
    }

    @Override
    public Future<Integer> asyncHdel(String key, Iterator<String> fields) {
        return addWriteOperation(makeHdelOperation(key, toBytes(fields)));
    }

    protected OpFuture<Integer> makeHdelOperation(String key, byte[][] fields) {
        IntegerCallback cb =
                new IntegerCallback(operationTimeout, executorService);
        Operation op = opFact.hdel(toBytes(key), fields, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Integer>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncHexists(String key, String field) {
        return addOperation(key, makeHexistsOperation(key, field));
    }

    protected OpFuture<Boolean> makeHexistsOperation(String key, String field) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op = opFact.hexists(toBytes(key), toBytes(field), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<String> asyncHget(String key, String field) {
        return asyncHget(key, field, stringTranscoder);
    }

    @Override
    public Future<byte[]> asyncHgetAsBytes(String key, String field) {
        return asyncHget(key, field, bytesTranscoder);
    }

    @Override
    public <T> Future<T> asyncHget(String key, String field,
            Transcoder<T> transcoder) {
        return addOperation(key, makeHgetOperation(key, field, transcoder));
    }

    protected <T> OpFuture<T> makeHgetOperation(String key, String field,
            Transcoder<T> transcoder) {
        TranscodedBytesCallback<T> cb =
                new TranscodedBytesCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.hget(toBytes(key), toBytes(field), cb);
        cb.rv.setOperation(op);
        return new OpFuture<T>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Map<String, String>> asyncHgetall(String key) {
        return asyncHgetall(key, stringTranscoder);
    }

    @Override
    public Future<Map<String, byte[]>> asyncHgetallAsBytes(String key) {
        return asyncHgetall(key, bytesTranscoder);
    }

    @Override
    public <T> Future<Map<String, T>> asyncHgetall(String key,
            Transcoder<T> transcoder) {
        return addOperation(key, makeHgetallOperation(key, transcoder));
    }

    @Override
    public <T> Future<Map<String, Object>> asyncHgetall(String key,
            Map<String, Transcoder<?>> transcoders) {
        return addOperation(key, makeHgetallOperation(key, transcoders));
    }

    protected <T> OpFuture<Map<String, T>> makeHgetallOperation(String key,
            final Transcoder<T> transcoder) {
        final CountDownLatch l = new CountDownLatch(1);
        final MapFuture<T> rv =
                new MapFuture<T>(l, operationTimeout, executorService);
        Operation op =
                opFact.hgetall(toBytes(key), new BytesListReplyCallback() {
                    private String key = null;
                    private Map<String, Future<T>> map = null;

                    @Override
                    public void receivedStatus(OperationStatus status) {
                        rv.set(map, status);
                    }

                    @Override
                    public void complete() {
                        l.countDown();
                    }

                    @Override
                    public void onSize(int size) {
                        map = new HashMap<String, Future<T>>(size / 2);
                    }

                    @Override
                    public void onEmptyList() {
                        map = Collections.emptyMap();
                    }

                    @Override
                    public void onData(byte[] data) {
                        if (key == null) {
                            key = new String(data, UTF_8);
                        } else {
                            map.put(key, tcService.decode(transcoder, data));
                            key = null;
                        }
                    }
                });
        rv.setOperation(op);
        return new OpFuture<Map<String, T>>(op, rv, operationTimeout);
    }

    protected OpFuture<Map<String, Object>> makeHgetallOperation(String key,
            final Map<String, Transcoder<?>> transcoders) {
        final CountDownLatch l = new CountDownLatch(1);
        final MapFuture<Object> rv =
                new MapFuture<Object>(l, operationTimeout, executorService);
        Operation op =
                opFact.hgetall(toBytes(key), new BytesListReplyCallback() {
                    private String key = null;
                    private Map<String, Future<Object>> map = null;

                    @Override
                    public void receivedStatus(OperationStatus status) {
                        rv.set(map, status);
                    }

                    @Override
                    public void complete() {
                        l.countDown();
                    }

                    @Override
                    public void onSize(int size) {
                        map = new HashMap<String, Future<Object>>(size / 2);
                    }

                    @Override
                    public void onEmptyList() {
                        map = Collections.emptyMap();
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onData(byte[] data) {
                        if (key == null) {
                            key = new String(data, UTF_8);
                        } else {
                            Transcoder<?> transcoder = transcoders.get(key);
                            if (transcoder != null) {
                                map.put(key, (Future<Object>) tcService.decode(
                                        transcoder, data));
                                key = null;
                            }
                        }
                    }
                });
        rv.setOperation(op);
        return new OpFuture<Map<String, Object>>(op, rv, operationTimeout);
    }

    @Override
    public Future<Set<String>> asyncHkeys(String key) {
        return addOperation(key, makeHkeysOperation(key));
    }

    protected OpFuture<Set<String>> makeHkeysOperation(String key) {
        StringSetCallback cb =
                new StringSetCallback(operationTimeout, executorService);
        Operation op = opFact.hkeys(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Set<String>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncHincrBy(String key, String field, long by) {
        return addWriteOperation(makeHincrByOperation(key, field, by));
    }

    protected OpFuture<Long> makeHincrByOperation(String key, String field,
            long by) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.hincrBy(toBytes(key), toBytes(field), by, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Double>
            asyncHincrByFloat(String key, String field, double by) {
        return addWriteOperation(makeHincrByFloatOperation(key, field, by));
    }

    protected OpFuture<Double> makeHincrByFloatOperation(String key,
            String field, double by) {
        DoubleCallback cb =
                new DoubleCallback(operationTimeout, executorService);
        Operation op =
                opFact.hincrByFloat(toBytes(key), toBytes(field), by, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Double>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Integer> asyncHlen(String key) {
        return addOperation(key, makeHlenOperation(key));
    }

    protected <T> OpFuture<Integer> makeHlenOperation(String key) {
        IntegerCallback cb =
                new IntegerCallback(operationTimeout, executorService);
        Operation op = opFact.hlen(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Integer>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Map<String, String>> asyncHmget(String key, String... fields) {
        return asyncHmget(key, stringTranscoder, fields);
    }

    @Override
    public Future<Map<String, String>> asyncHmget(String key,
            Iterator<String> fields) {
        return asyncHmget(key, fields, stringTranscoder);
    }

    @Override
    public Future<Map<String, String>> asyncHmget(String key,
            Iterable<String> fields) {
        return asyncHmget(key, fields, stringTranscoder);
    }

    @Override
    public Future<Map<String, byte[]>> asyncHmgetAsBytes(String key,
            String... fields) {
        return asyncHmget(key, bytesTranscoder, fields);

    }

    @Override
    public Future<Map<String, byte[]>> asyncHmgetAsBytes(String key,
            Iterator<String> fields) {
        return asyncHmget(key, fields, bytesTranscoder);
    }

    @Override
    public Future<Map<String, byte[]>> asyncHmgetAsBytes(String key,
            Iterable<String> fields) {
        return asyncHmget(key, fields, bytesTranscoder);
    }

    @Override
    public <T> Future<Map<String, T>> asyncHmget(String key,
            Transcoder<T> transcoder, String... fields) {
        return addOperation(key, makeHmgetOperation(key, fields, transcoder));
    }

    @Override
    public <T> Future<Map<String, T>> asyncHmget(String key,
            Iterator<String> fields, Transcoder<T> transcoder) {
        List<String> fieldsList = new ArrayList<String>();
        while (fields.hasNext()) {
            fieldsList.add(fields.next());
        }
        return addOperation(
                key,
                makeHmgetOperation(key,
                        fieldsList.toArray(new String[fieldsList.size()]),
                        transcoder));
    }

    @Override
    public <T> Future<Map<String, T>> asyncHmget(String key,
            Iterable<String> fields, Transcoder<T> transcoder) {
        return asyncHmget(key, fields.iterator(), transcoder);
    }

    protected <T> OpFuture<Map<String, T>> makeHmgetOperation(String key,
            final String[] fields, final Transcoder<T> transcoder) {
        final CountDownLatch l = new CountDownLatch(1);
        final MapFuture<T> rv =
                new MapFuture<T>(l, operationTimeout, executorService);
        Operation op =
                opFact.hmget(toBytes(key), toBytes(fields),
                        new BytesListReplyCallback() {
                            private int index = 0;
                            private Map<String, Future<T>> map = null;

                            @Override
                            public void receivedStatus(OperationStatus status) {
                                rv.set(map, status);
                            }

                            @Override
                            public void complete() {
                                l.countDown();
                            }

                            @Override
                            public void onSize(int size) {
                                map = new HashMap<String, Future<T>>(size / 2);
                            }

                            @Override
                            public void onEmptyList() {
                                map = Collections.emptyMap();
                            }

                            @Override
                            public void onData(byte[] data) {
                                map.put(fields[index++],
                                        tcService.decode(transcoder, data));
                            }
                        });
        rv.setOperation(op);
        return new OpFuture<Map<String, T>>(op, rv, operationTimeout);
    }

    @Override
    public Future<Map<String, Object>> asyncHmget(String key,
            Map<String, Transcoder<?>> transcoders) {
        return addOperation(key, makeHmgetOperation(key, transcoders));
    }

    protected OpFuture<Map<String, Object>> makeHmgetOperation(String key,
            final Map<String, Transcoder<?>> transcoders) {
        final CountDownLatch l = new CountDownLatch(1);
        final MapFuture<Object> rv =
                new MapFuture<Object>(l, operationTimeout, executorService);

        Operation op =
                opFact.hmget(toBytes(key), toBytes(transcoders.keySet()
                        .iterator()), new BytesListReplyCallback() {
                    private Map<String, Future<Object>> map = null;
                    private final Iterator<Map.Entry<String, Transcoder<?>>> fields =
                            transcoders.entrySet().iterator();

                    @Override
                    public void receivedStatus(OperationStatus status) {
                        rv.set(map, status);
                    }

                    @Override
                    public void complete() {
                        l.countDown();
                    }

                    @Override
                    public void onSize(int size) {
                        map = new HashMap<String, Future<Object>>(size / 2);
                    }

                    @Override
                    public void onEmptyList() {
                        map = Collections.emptyMap();
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onData(byte[] data) {
                        Map.Entry<String, Transcoder<?>> entry = fields.next();
                        map.put(entry.getKey(),
                                (Future<Object>) tcService.decode(
                                        entry.getValue(), data));
                    }
                });
        rv.setOperation(op);
        return new OpFuture<Map<String, Object>>(op, rv, operationTimeout);
    }

    @Override
    public Future<Void> asyncHmset(String key, String... fieldValues) {
        return addOperation(key, makeHmsetOperation(key, toBytes(fieldValues)));
    }

    @Override
    public Future<Void> asyncHmset(String key, Map<String, String> fieldValues) {
        return asyncHmset(key, fieldValues, stringTranscoder);
    }

    @Override
    public Future<Void> asyncHmsetBytes(String key,
            Map<String, byte[]> fieldValues) {
        return asyncHmset(key, fieldValues, bytesTranscoder);
    }

    @Override
    public <T> Future<Void> asyncHmset(String key,
            Map<String, T> fieldValuesMap, Transcoder<T> transcoder) {
        byte[][] fieldValues = new byte[fieldValuesMap.size() * 2][];
        int index = 0;
        for (Map.Entry<String, T> entry : fieldValuesMap.entrySet()) {
            fieldValues[index++] = toBytes(entry.getKey());
            fieldValues[index++] = transcoder.encode(entry.getValue());
        }
        return addOperation(key, makeHmsetOperation(key, fieldValues));
    }

    protected OpFuture<Void>
            makeHmsetOperation(String key, byte[][] fieldValues) {
        VoidCallback cb = new VoidCallback(operationTimeout, executorService);
        Operation op = opFact.hmset(toBytes(key), fieldValues, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Void>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncHset(String key, String field, String value) {
        return asyncHset(key, field, value, stringTranscoder);
    }

    @Override
    public Future<Boolean> asyncHset(String key, String field, byte[] value) {
        return asyncHset(key, field, value, bytesTranscoder);
    }

    @Override
    public <T> Future<Boolean> asyncHset(String key, String field, T value,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeHsetOperation(key, field,
                transcoder.encode(value)));
    }

    protected OpFuture<Boolean> makeHsetOperation(String key, String field,
            byte[] value) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op = opFact.hset(toBytes(key), toBytes(field), value, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncHsetnx(String key, String field, String value) {
        return asyncHsetnx(key, field, value, stringTranscoder);
    }

    @Override
    public Future<Boolean> asyncHsetnx(String key, String field, byte[] value) {
        return asyncHsetnx(key, field, value, bytesTranscoder);
    }

    @Override
    public <T> Future<Boolean> asyncHsetnx(String key, String field, T value,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeHsetnxOperation(key, field,
                transcoder.encode(value)));
    }

    protected OpFuture<Boolean> makeHsetnxOperation(String key, String field,
            byte[] value) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op = opFact.hsetnx(toBytes(key), toBytes(field), value, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<List<String>> asyncHvals(String key) {
        return asyncHvals(key, stringTranscoder);
    }

    @Override
    public Future<List<byte[]>> asyncHvalsAsBytes(String key) {
        return asyncHvals(key, bytesTranscoder);
    }

    @Override
    public <T> Future<List<T>> asyncHvals(String key, Transcoder<T> transcoder) {
        return addOperation(key, makeHvalsOperation(key, transcoder));
    }

    protected <T> OpFuture<List<T>> makeHvalsOperation(String key,
            final Transcoder<T> transcoder) {
        TranscodedListCallback<T> cb =
                new TranscodedListCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.hvals(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<List<T>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<BlockingPopResult<String>> asyncBlpop(int timeout,
            String... keys) {
        return asyncBlpop(timeout, stringTranscoder, keys);
    }

    @Override
    public Future<BlockingPopResult<String>> asyncBlpop(Iterator<String> keys,
            int timeout) {
        return asyncBlpop(keys, timeout, stringTranscoder);
    }

    @Override
    public Future<BlockingPopResult<String>> asyncBlpop(Iterable<String> keys,
            int timeout) {
        return asyncBlpop(keys, timeout, stringTranscoder);
    }

    @Override
    public Future<BlockingPopResult<byte[]>> asyncBlpopAsBytes(int timeout,
            String... keys) {
        return asyncBlpop(timeout, bytesTranscoder, keys);
    }

    @Override
    public Future<BlockingPopResult<byte[]>> asyncBlpopAsBytes(
            Iterator<String> keys, int timeout) {
        return asyncBlpop(keys, timeout, bytesTranscoder);
    }

    @Override
    public Future<BlockingPopResult<byte[]>> asyncBlpopAsBytes(
            Iterable<String> keys, int timeout) {
        return asyncBlpop(keys, timeout, bytesTranscoder);
    }

    @Override
    public <T> Future<BlockingPopResult<T>> asyncBlpop(int timeout,
            Transcoder<T> transcoder, String... keys) {
        return addWriteOperation(makeBlpopOperation(toBytes(keys), timeout,
                transcoder));
    }

    @Override
    public <T> Future<BlockingPopResult<T>> asyncBlpop(Iterator<String> keys,
            int timeout, Transcoder<T> transcoder) {
        return addWriteOperation(makeBlpopOperation(toBytes(keys), timeout,
                transcoder));
    }

    @Override
    public <T> Future<BlockingPopResult<T>> asyncBlpop(Iterable<String> keys,
            int timeout, Transcoder<T> transcoder) {
        return asyncBlpop(keys.iterator(), timeout, transcoder);
    }

    protected <T> OpFuture<BlockingPopResult<T>> makeBlpopOperation(
            byte[][] keys, int timeout, final Transcoder<T> transcoder) {
        int opTimeout = 1000 * timeout + 20;
        BlockingPopCallback<T> cb =
                new BlockingPopCallback<T>(opTimeout, transcoder, tcService,
                        executorService);

        Operation op = opFact.blpop(keys, timeout, cb);
        cb.rv.setOperation(op);
        return new OpFuture<BlockingPopResult<T>>(op, cb.rv, opTimeout);
    }

    @Override
    public Future<BlockingPopResult<String>> asyncBrpop(int timeout,
            String... keys) {
        return asyncBrpop(timeout, stringTranscoder, keys);
    }

    @Override
    public Future<BlockingPopResult<String>> asyncBrpop(Iterator<String> keys,
            int timeout) {
        return asyncBrpop(keys, timeout, stringTranscoder);
    }

    @Override
    public Future<BlockingPopResult<String>> asyncBrpop(Iterable<String> keys,
            int timeout) {
        return asyncBrpop(keys, timeout, stringTranscoder);
    }

    @Override
    public Future<BlockingPopResult<byte[]>> asyncBrpopAsBytes(int timeout,
            String... keys) {
        return asyncBrpop(timeout, bytesTranscoder, keys);
    }

    @Override
    public Future<BlockingPopResult<byte[]>> asyncBrpopAsBytes(
            Iterator<String> keys, int timeout) {
        return asyncBrpop(keys, timeout, bytesTranscoder);
    }

    @Override
    public Future<BlockingPopResult<byte[]>> asyncBrpopAsBytes(
            Iterable<String> keys, int timeout) {
        return asyncBrpop(keys, timeout, bytesTranscoder);
    }

    @Override
    public <T> Future<BlockingPopResult<T>> asyncBrpop(int timeout,
            Transcoder<T> transcoder, String... keys) {
        return addWriteOperation(makeBrpopOperation(toBytes(keys), timeout,
                transcoder));
    }

    @Override
    public <T> Future<BlockingPopResult<T>> asyncBrpop(Iterator<String> keys,
            int timeout, Transcoder<T> transcoder) {

        return addWriteOperation(makeBrpopOperation(toBytes(keys), timeout,
                transcoder));
    }

    @Override
    public <T> Future<BlockingPopResult<T>> asyncBrpop(Iterable<String> keys,
            int timeout, Transcoder<T> transcoder) {
        return asyncBrpop(keys.iterator(), timeout, transcoder);
    }

    protected <T> OpFuture<BlockingPopResult<T>> makeBrpopOperation(
            byte[][] keys, int timeout, final Transcoder<T> transcoder) {
        int opTimeout = 1000 * timeout + 20;
        BlockingPopCallback<T> cb =
                new BlockingPopCallback<T>(opTimeout, transcoder, tcService,
                        executorService);

        Operation op = opFact.brpop(keys, timeout, cb);
        cb.rv.setOperation(op);
        return new OpFuture<BlockingPopResult<T>>(op, cb.rv, opTimeout);
    }

    @Override
    public Future<String> asyncBrpoplpush(String source, String destination,
            int timeout) {
        return asyncBrpoplpush(source, destination, timeout, stringTranscoder);
    }

    @Override
    public Future<byte[]> asyncBrpoplpushAsBytes(String source,
            String destination, int timeout) {
        return asyncBrpoplpush(source, destination, timeout, bytesTranscoder);
    }

    @Override
    public <T> Future<T> asyncBrpoplpush(String source, String destination,
            int timeout, Transcoder<T> transcoder) {
        return addWriteOperation(makeBrpoplpushOperation(source, destination,
                timeout, transcoder));
    }

    protected <T> OpFuture<T> makeBrpoplpushOperation(String source,
            String destination, int timeout, final Transcoder<T> transcoder) {
        int opTimeout = 1000 * timeout + 20;
        TranscodedBytesCallback<T> cb =
                new TranscodedBytesCallback<T>(transcoder, tcService,
                        opTimeout, executorService);

        Operation op =
                opFact.brpoplpush(toBytes(source), toBytes(destination),
                        timeout, cb);
        cb.rv.setOperation(op);
        return new OpFuture<T>(op, cb.rv, opTimeout);
    }

    @Override
    public Future<String> asyncLindex(String key, long index) {
        return asyncLindex(key, index, stringTranscoder);
    }

    @Override
    public Future<byte[]> asyncLindexAsBytes(String key, long index) {
        return asyncLindex(key, index, bytesTranscoder);
    }

    @Override
    public <T> Future<T> asyncLindex(String key, long index,
            Transcoder<T> transcoder) {
        return addOperation(key, makeLindexOperation(key, index, transcoder));
    }

    protected <T> OpFuture<T> makeLindexOperation(String key, long index,
            Transcoder<T> transcoder) {
        TranscodedBytesCallback<T> cb =
                new TranscodedBytesCallback<T>(transcoder, tcService, index,
                        executorService);
        Operation op = opFact.lindex(toBytes(key), index, cb);
        cb.rv.setOperation(op);
        return new OpFuture<T>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long>
            asyncLinsertAfter(String key, String pivot, String value) {
        return asyncLinsertAfter(key, pivot, value, stringTranscoder);
    }

    @Override
    public Future<Long>
            asyncLinsertAfter(String key, byte[] pivot, byte[] value) {
        return asyncLinsertAfter(key, pivot, value, bytesTranscoder);
    }

    @Override
    public <T> Future<Long> asyncLinsertAfter(String key, T pivot, T value,
            Transcoder<T> transcoder) {
        return asyncLinsertAfter(key, pivot, transcoder, value, transcoder);
    }

    @Override
    public <P, V> Future<Long> asyncLinsertAfter(String key, P pivot,
            Transcoder<P> pivotTranscoder, V value,
            Transcoder<V> valueTranscoder) {
        return addWriteOperation(makeLinsertAfterOperation(key, pivot,
                pivotTranscoder, value, valueTranscoder));
    }

    @Override
    public Future<Long> asyncLinsertBefore(String key, String pivot,
            String value) {
        return asyncLinsertBefore(key, pivot, value, stringTranscoder);
    }

    @Override
    public Future<Long> asyncLinsertBefore(String key, byte[] pivot,
            byte[] value) {
        return asyncLinsertBefore(key, pivot, value, bytesTranscoder);
    }

    @Override
    public <T> Future<Long> asyncLinsertBefore(String key, T pivot, T value,
            Transcoder<T> transcoder) {
        return asyncLinsertBefore(key, pivot, transcoder, value, transcoder);
    }

    @Override
    public <P, V> Future<Long> asyncLinsertBefore(String key, P pivot,
            Transcoder<P> pivotTranscoder, V value,
            Transcoder<V> valueTranscoder) {
        return addWriteOperation(makeLinsertBeforeOperation(key, pivot,
                pivotTranscoder, value, valueTranscoder));
    }

    protected <P, V> OpFuture<Long> makeLinsertAfterOperation(String key,
            P pivot, Transcoder<P> pivotTranscoder, V value,
            Transcoder<V> valueTranscoder) {
        return makeLinsertOperation(key, AFTER, pivot, pivotTranscoder, value,
                valueTranscoder);
    }

    protected <P, V> OpFuture<Long> makeLinsertBeforeOperation(String key,
            P pivot, Transcoder<P> pivotTranscoder, V value,
            Transcoder<V> valueTranscoder) {
        return makeLinsertOperation(key, BEFORE, pivot, pivotTranscoder, value,
                valueTranscoder);
    }

    protected <P, V> OpFuture<Long> makeLinsertOperation(String key,
            byte[] position, P pivot, Transcoder<P> pivotTranscoder, V value,
            Transcoder<V> valueTranscoder) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op =
                opFact.linsert(toBytes(key), position,
                        pivotTranscoder.encode(pivot),
                        valueTranscoder.encode(value), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncLlen(String key) {
        return addOperation(key, makeLlenOperation(key));
    }

    protected OpFuture<Long> makeLlenOperation(String key) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.llen(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<String> asyncLpop(String key) {
        return asyncLpop(key, stringTranscoder);
    }

    @Override
    public Future<byte[]> asyncLpopAsBytes(String key) {
        return asyncLpop(key, bytesTranscoder);
    }

    @Override
    public <T> Future<T> asyncLpop(String key, Transcoder<T> transcoder) {
        return addWriteOperation(makeLpopOperation(key, transcoder));
    }

    protected <T> OpFuture<T> makeLpopOperation(String key,
            Transcoder<T> transcoder) {
        TranscodedBytesCallback<T> cb =
                new TranscodedBytesCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.lpop(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<T>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncLpush(String key, String... values) {
        return asyncLpush(key, stringTranscoder, values);
    }

    @Override
    public Future<Long> asyncLpush(String key, Iterator<String> values) {
        return asyncLpush(key, values, stringTranscoder);
    }

    @Override
    public Future<Long> asyncLpush(String key, Iterable<String> values) {
        return asyncLpush(key, values, stringTranscoder);
    }

    @Override
    public Future<Long> asyncLpushAsBytes(String key, byte[]... values) {
        return asyncLpush(key, bytesTranscoder, values);
    }

    @Override
    public Future<Long> asyncLpushAsBytes(String key, Iterator<byte[]> values) {
        return asyncLpush(key, values, bytesTranscoder);
    }

    @Override
    public Future<Long> asyncLpushAsBytes(String key, Iterable<byte[]> values) {
        return asyncLpush(key, values, bytesTranscoder);
    }

    @Override
    public <T> Future<Long> asyncLpush(String key, Transcoder<T> transcoder,
            T... values) {
        return addWriteOperation(makeLpushOperation(key,
                toBytes(values, transcoder)));
    }

    @Override
    public <T> Future<Long> asyncLpush(String key, Iterator<T> values,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeLpushOperation(key,
                toBytes(values, transcoder)));
    }

    @Override
    public <T> Future<Long> asyncLpush(String key, Iterable<T> values,
            Transcoder<T> transcoder) {
        return asyncLpush(key, values.iterator(), transcoder);
    }

    protected OpFuture<Long> makeLpushOperation(String key, byte[][] values) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.lpush(toBytes(key), values, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncLpushx(String key, String value) {
        return asyncLpushx(key, value, stringTranscoder);
    }

    @Override
    public Future<Long> asyncLpushx(String key, byte[] value) {
        return asyncLpushx(key, value, bytesTranscoder);
    }

    @Override
    public <T> Future<Long> asyncLpushx(String key, T value,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeLpushxOperation(key,
                transcoder.encode(value)));
    }

    protected OpFuture<Long> makeLpushxOperation(String key, byte[] value) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.lpushx(toBytes(key), value, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<List<String>> asyncLrange(String key, long start, long stop) {
        return asyncLrange(key, start, stop, stringTranscoder);
    }

    @Override
    public Future<List<byte[]>> asyncLrangeAsBytes(String key, long start,
            long stop) {
        return asyncLrange(key, start, stop, bytesTranscoder);
    }

    @Override
    public <T> Future<List<T>> asyncLrange(String key, long start, long stop,
            Transcoder<T> transcoder) {
        return addOperation(key,
                makeLrangeOperation(key, start, stop, transcoder));
    }

    protected <T> OpFuture<List<T>> makeLrangeOperation(String key, long start,
            long stop, Transcoder<T> transcoder) {
        TranscodedListCallback<T> cb =
                new TranscodedListCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.lrange(toBytes(key), start, stop, cb);
        cb.rv.setOperation(op);
        return new OpFuture<List<T>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncLrem(String key, long count, String value) {
        return asyncLrem(key, count, value, stringTranscoder);
    }

    @Override
    public Future<Long> asyncLrem(String key, long count, byte[] value) {
        return asyncLrem(key, count, value, bytesTranscoder);
    }

    @Override
    public <T> Future<Long> asyncLrem(String key, long count, T value,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeLremOperation(key, count,
                transcoder.encode(value)));
    }

    protected OpFuture<Long> makeLremOperation(String key, long count,
            byte[] value) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.lrem(toBytes(key), count, value, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Void> asyncLset(String key, long index, String value) {
        return asyncLset(key, index, value, stringTranscoder);
    }

    @Override
    public Future<Void> asyncLset(String key, long index, byte[] value) {
        return asyncLset(key, index, value, bytesTranscoder);
    }

    @Override
    public <T> Future<Void> asyncLset(String key, long index, T value,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeLsetOperation(key, index,
                transcoder.encode(value)));
    }

    protected OpFuture<Void> makeLsetOperation(String key, long index,
            byte[] value) {
        VoidCallback cb = new VoidCallback(operationTimeout, executorService);
        Operation op = opFact.lset(toBytes(key), index, value, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Void>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Void> asyncLtrim(String key, long start, long stop) {
        return addWriteOperation(makeLtrimOperation(key, start, stop));
    }

    protected OpFuture<Void> makeLtrimOperation(String key, long start,
            long stop) {
        VoidCallback cb = new VoidCallback(operationTimeout, executorService);
        Operation op = opFact.ltrim(toBytes(key), start, stop, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Void>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<String> asyncRpop(String key) {
        return asyncRpop(key, stringTranscoder);
    }

    @Override
    public Future<byte[]> asyncRpopAsBytes(String key) {
        return asyncRpop(key, bytesTranscoder);
    }

    @Override
    public <T> Future<T> asyncRpop(String key, Transcoder<T> transcoder) {
        return addWriteOperation(makeRpopOperation(key, transcoder));
    }

    protected <T> OpFuture<T> makeRpopOperation(String key,
            Transcoder<T> transcoder) {
        TranscodedBytesCallback<T> cb =
                new TranscodedBytesCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.rpop(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<T>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<String> asyncRpoplpush(String source, String destination) {
        return asyncRpoplpush(source, destination, stringTranscoder);
    }

    @Override
    public Future<byte[]> asyncRpoplpushAsBytes(String source,
            String destination) {
        return asyncRpoplpush(source, destination, bytesTranscoder);
    }

    @Override
    public <T> Future<T> asyncRpoplpush(String source, String destination,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeRpoplpushOperation(source, destination,
                transcoder));
    }

    protected <T> OpFuture<T> makeRpoplpushOperation(String source,
            String destination, Transcoder<T> transcoder) {
        TranscodedBytesCallback<T> cb =
                new TranscodedBytesCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op =
                opFact.rpoplpush(toBytes(source), toBytes(destination), cb);
        cb.rv.setOperation(op);
        return new OpFuture<T>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncRpush(String key, String... values) {
        return asyncRpush(key, stringTranscoder, values);
    }

    @Override
    public Future<Long> asyncRpush(String key, Iterator<String> values) {
        return asyncRpush(key, values, stringTranscoder);
    }

    @Override
    public Future<Long> asyncRpush(String key, Iterable<String> values) {
        return asyncRpush(key, values, stringTranscoder);
    }

    @Override
    public Future<Long> asyncRpushAsBytes(String key, byte[]... values) {
        return asyncRpush(key, bytesTranscoder, values);
    }

    @Override
    public Future<Long> asyncRpushAsBytes(String key, Iterator<byte[]> values) {
        return asyncRpush(key, values, bytesTranscoder);
    }

    @Override
    public Future<Long> asyncRpushAsBytes(String key, Iterable<byte[]> values) {
        return asyncRpush(key, values, bytesTranscoder);
    }

    @Override
    public <T> Future<Long> asyncRpush(String key, Transcoder<T> transcoder,
            T... values) {
        return addWriteOperation(makeRpushOperation(key,
                toBytes(values, transcoder)));
    }

    @Override
    public <T> Future<Long> asyncRpush(String key, Iterator<T> values,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeRpushOperation(key,
                toBytes(values, transcoder)));
    }

    @Override
    public <T> Future<Long> asyncRpush(String key, Iterable<T> values,
            Transcoder<T> transcoder) {
        return asyncRpush(key, values.iterator(), transcoder);
    }

    protected OpFuture<Long> makeRpushOperation(String key, byte[][] values) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.rpush(toBytes(key), values, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncRpushx(String key, String value) {
        return asyncRpushx(key, value, stringTranscoder);
    }

    @Override
    public Future<Long> asyncRpushx(String key, byte[] value) {
        return asyncRpushx(key, value, bytesTranscoder);
    }

    @Override
    public <T> Future<Long> asyncRpushx(String key, T value,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeRpushxOperation(key,
                transcoder.encode(value)));
    }

    protected OpFuture<Long> makeRpushxOperation(String key, byte[] value) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.rpushx(toBytes(key), value, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncSadd(String key, String... values) {
        return asyncSadd(key, stringTranscoder, values);
    }

    @Override
    public Future<Long> asyncSadd(String key, Iterator<String> values) {
        return asyncSadd(key, values, stringTranscoder);
    }

    @Override
    public Future<Long> asyncSadd(String key, Iterable<String> values) {
        return asyncSadd(key, values, stringTranscoder);
    }

    @Override
    public Future<Long> asyncSaddAsBytes(String key, byte[]... values) {
        return asyncSadd(key, bytesTranscoder, values);
    }

    @Override
    public Future<Long> asyncSaddAsBytes(String key, Iterator<byte[]> values) {
        return asyncSadd(key, values, bytesTranscoder);
    }

    @Override
    public Future<Long> asyncSaddAsBytes(String key, Iterable<byte[]> values) {
        return asyncSadd(key, values, bytesTranscoder);
    }

    @Override
    public <T> Future<Long> asyncSadd(String key, Transcoder<T> transcoder,
            T... values) {
        return addWriteOperation(makeSaddOperation(key,
                toBytes(values, transcoder)));
    }

    @Override
    public <T> Future<Long> asyncSadd(String key, Iterator<T> values,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeSaddOperation(key,
                toBytes(values, transcoder)));
    }

    @Override
    public <T> Future<Long> asyncSadd(String key, Iterable<T> values,
            Transcoder<T> transcoder) {
        return asyncSadd(key, values.iterator(), transcoder);
    }

    protected OpFuture<Long> makeSaddOperation(String key, byte[][] values) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.sadd(toBytes(key), values, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncScard(String key) {
        return addOperation(key, makeScardOperation(key));
    }

    protected OpFuture<Long> makeScardOperation(String key) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.scard(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Set<String>> asyncSdiff(String... keys) {
        return asyncSdiff(stringTranscoder, keys);
    }

    @Override
    public Future<Set<String>> asyncSdiff(Iterator<String> keys) {
        return asyncSdiff(keys, stringTranscoder);
    }

    @Override
    public Future<Set<String>> asyncSdiff(Iterable<String> keys) {
        return asyncSdiff(keys, stringTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncSdiffAsBytes(String... keys) {
        return asyncSdiff(bytesTranscoder, keys);
    }

    @Override
    public Future<Set<byte[]>> asyncSdiffAsBytes(Iterator<String> keys) {
        return asyncSdiff(keys, bytesTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncSdiffAsBytes(Iterable<String> keys) {
        return asyncSdiff(keys, bytesTranscoder);
    }

    @Override
    public <T> Future<Set<T>> asyncSdiff(Transcoder<T> transcoder,
            String... keys) {
        FirstKeyBytes fkb = toFirstKeyBytes(keys);

        return addOperation(fkb.firstKey, makeSdiffOperation(fkb, transcoder));
    }

    @Override
    public <T> Future<Set<T>> asyncSdiff(Iterator<String> keys,
            Transcoder<T> transcoder) {
        FirstKeyBytes fkb = toFirstKeyBytes(keys);

        return addOperation(fkb.firstKey, makeSdiffOperation(fkb, transcoder));
    }

    @Override
    public <T> Future<Set<T>> asyncSdiff(Iterable<String> keys,
            Transcoder<T> transcoder) {
        return asyncSdiff(keys.iterator(), transcoder);
    }

    protected <T> OpFuture<Set<T>> makeSdiffOperation(FirstKeyBytes fkb,
            Transcoder<T> transcoder) {
        TranscodedSetCallback<T> cb =
                new TranscodedSetCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.sdiff(fkb.bytes, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Set<T>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncSdiffstore(String destination, String... keys) {
        return addWriteOperation(makeSdiffstoreOperation(destination,
                toBytes(keys)));
    }

    @Override
    public Future<Long> asyncSdiffstore(String destination,
            Iterator<String> keys) {
        return addWriteOperation(makeSdiffstoreOperation(destination,
                toBytes(keys)));
    }

    @Override
    public Future<Long> asyncSdiffstore(String destination,
            Iterable<String> keys) {
        return asyncSdiffstore(destination, keys.iterator());
    }

    protected OpFuture<Long> makeSdiffstoreOperation(String destination,
            byte[][] keys) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.sdiffstore(toBytes(destination), keys, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Set<String>> asyncSinter(String... keys) {
        return asyncSinter(stringTranscoder, keys);
    }

    @Override
    public Future<Set<String>> asyncSinter(Iterator<String> keys) {
        return asyncSinter(keys, stringTranscoder);
    }

    @Override
    public Future<Set<String>> asyncSinter(Iterable<String> keys) {
        return asyncSinter(keys, stringTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncSinterAsBytes(String... keys) {
        return asyncSinter(bytesTranscoder, keys);
    }

    @Override
    public Future<Set<byte[]>> asyncSinterAsBytes(Iterator<String> keys) {
        return asyncSinter(keys, bytesTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncSinterAsBytes(Iterable<String> keys) {
        return asyncSinter(keys, bytesTranscoder);
    }

    @Override
    public <T> Future<Set<T>> asyncSinter(Transcoder<T> transcoder,
            String... keys) {
        FirstKeyBytes fkb = toFirstKeyBytes(keys);

        return addOperation(fkb.firstKey, makeSinterOperation(fkb, transcoder));
    }

    @Override
    public <T> Future<Set<T>> asyncSinter(Iterator<String> keys,
            Transcoder<T> transcoder) {
        FirstKeyBytes fkb = toFirstKeyBytes(keys);

        return addOperation(fkb.firstKey, makeSinterOperation(fkb, transcoder));
    }

    @Override
    public <T> Future<Set<T>> asyncSinter(Iterable<String> keys,
            Transcoder<T> transcoder) {
        return asyncSinter(keys.iterator(), transcoder);
    }

    protected <T> OpFuture<Set<T>> makeSinterOperation(FirstKeyBytes fkb,
            Transcoder<T> transcoder) {
        TranscodedSetCallback<T> cb =
                new TranscodedSetCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.sinter(fkb.bytes, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Set<T>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncSinterstore(String destination, String... keys) {
        return addWriteOperation(makeSinterstoreOperation(destination,
                toBytes(keys)));
    }

    @Override
    public Future<Long> asyncSinterstore(String destination,
            Iterator<String> keys) {
        return addWriteOperation(makeSinterstoreOperation(destination,
                toBytes(keys)));
    }

    @Override
    public Future<Long> asyncSinterstore(String destination,
            Iterable<String> keys) {
        return asyncSinterstore(destination, keys.iterator());
    }

    protected OpFuture<Long> makeSinterstoreOperation(String destination,
            byte[][] keys) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.sinterstore(toBytes(destination), keys, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncSismember(String key, String member) {
        return asyncSismember(key, member, stringTranscoder);
    }

    @Override
    public Future<Boolean> asyncSismember(String key, byte[] member) {
        return asyncSismember(key, member, bytesTranscoder);
    }

    @Override
    public <T> Future<Boolean> asyncSismember(String key, T member,
            Transcoder<T> transcoder) {
        return addOperation(key, makeSismember(key, transcoder.encode(member)));
    }

    protected OpFuture<Boolean> makeSismember(String key, byte[] member) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op = opFact.sismember(toBytes(key), member, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Set<String>> asyncSmembers(String key) {
        return asyncSmembers(key, stringTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncSmembersAsBytes(String key) {
        return asyncSmembers(key, bytesTranscoder);
    }

    @Override
    public <T> Future<Set<T>> asyncSmembers(String key,
            final Transcoder<T> transcoder) {
        return addOperation(key, makeSmembersOperation(key, transcoder));

    }

    protected <T> OpFuture<Set<T>> makeSmembersOperation(String key,
            Transcoder<T> transcoder) {
        TranscodedSetCallback<T> cb =
                new TranscodedSetCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.smembers(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Set<T>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Boolean> asyncSmove(String source, String destination,
            String value) {
        return asyncSmove(source, destination, value, stringTranscoder);
    }

    @Override
    public Future<Boolean> asyncSmove(String source, String destination,
            byte[] value) {
        return asyncSmove(source, destination, value, bytesTranscoder);
    }

    @Override
    public <T> Future<Boolean> asyncSmove(String source, String destination,
            T value, Transcoder<T> transcoder) {
        return addWriteOperation(makeSmoveOperation(source, destination,
                transcoder.encode(value)));
    }

    protected OpFuture<Boolean> makeSmoveOperation(String source,
            String destination, byte[] value) {
        BooleanCallback cb =
                new BooleanCallback(operationTimeout, executorService);
        Operation op =
                opFact.smove(toBytes(source), toBytes(destination), value, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Boolean>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<String> asyncSpop(String key) {
        return asyncSpop(key, stringTranscoder);
    }

    @Override
    public Future<byte[]> asyncSpopAsBytes(String key) {
        return asyncSpop(key, bytesTranscoder);
    }

    @Override
    public <T> Future<T> asyncSpop(String key, Transcoder<T> transcoder) {
        return addWriteOperation(makeSpopOperation(key, transcoder));
    }

    protected <T> OpFuture<T> makeSpopOperation(String key,
            Transcoder<T> transcoder) {
        TranscodedBytesCallback<T> cb =
                new TranscodedBytesCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.spop(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<T>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<String> asyncSrandmember(String key) {
        return asyncSrandmember(key, stringTranscoder);
    }

    @Override
    public Future<byte[]> asyncSrandmemberAsBytes(String key) {
        return asyncSrandmember(key, bytesTranscoder);
    }

    @Override
    public <T> Future<T> asyncSrandmember(String key, Transcoder<T> transcoder) {
        return addOperation(key, makeSrandmemberOperation(key, transcoder));
    }

    protected <T> OpFuture<T> makeSrandmemberOperation(String key,
            Transcoder<T> transcoder) {
        TranscodedBytesCallback<T> cb =
                new TranscodedBytesCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.srandmember(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<T>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Set<String>> asyncSrandmember(String key, long count) {
        return asyncSrandmember(key, count, stringTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncSrandmemberAsBytes(String key, long count) {
        return asyncSrandmember(key, count, bytesTranscoder);
    }

    @Override
    public <T> Future<Set<T>> asyncSrandmember(String key, long count,
            Transcoder<T> transcoder) {
        return addOperation(key,
                makeSrandmemberOperation(key, count, transcoder));
    }

    protected <T> OpFuture<Set<T>> makeSrandmemberOperation(String key,
            long count, Transcoder<T> transcoder) {
        TranscodedSetCallback<T> cb =
                new TranscodedSetCallback<T>(transcoder, tcService, count,
                        executorService);
        Operation op = opFact.srandmember(toBytes(key), count, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Set<T>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncSrem(String key, String... values) {
        return asyncSrem(key, stringTranscoder, values);
    }

    @Override
    public Future<Long> asyncSrem(String key, Iterator<String> values) {
        return asyncSrem(key, values, stringTranscoder);
    }

    @Override
    public Future<Long> asyncSrem(String key, Iterable<String> values) {
        return asyncSrem(key, values, stringTranscoder);
    }

    @Override
    public Future<Long> asyncSremAsBytes(String key, byte[]... values) {
        return asyncSrem(key, bytesTranscoder, values);
    }

    @Override
    public Future<Long> asyncSremAsBytes(String key, Iterator<byte[]> values) {
        return asyncSrem(key, values, bytesTranscoder);
    }

    @Override
    public Future<Long> asyncSremAsBytes(String key, Iterable<byte[]> values) {
        return asyncSrem(key, values, bytesTranscoder);
    }

    @Override
    public <T> Future<Long> asyncSrem(String key, Transcoder<T> transcoder,
            T... values) {
        return addWriteOperation(makeSremOperation(key,
                toBytes(values, transcoder)));
    }

    @Override
    public <T> Future<Long> asyncSrem(String key, Iterator<T> values,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeSremOperation(key,
                toBytes(values, transcoder)));
    }

    @Override
    public <T> Future<Long> asyncSrem(String key, Iterable<T> values,
            Transcoder<T> transcoder) {
        return asyncSrem(key, values.iterator(), transcoder);
    }

    protected OpFuture<Long> makeSremOperation(String key, byte[][] values) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.srem(toBytes(key), values, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Set<String>> asyncSunion(String... keys) {
        return asyncSunion(stringTranscoder, keys);
    }

    @Override
    public Future<Set<String>> asyncSunion(Iterator<String> keys) {
        return asyncSunion(keys, stringTranscoder);
    }

    @Override
    public Future<Set<String>> asyncSunion(Iterable<String> keys) {
        return asyncSunion(keys, stringTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncSunionAsBytes(String... keys) {
        return asyncSunion(bytesTranscoder, keys);
    }

    @Override
    public Future<Set<byte[]>> asyncSunionAsBytes(Iterator<String> keys) {
        return asyncSunion(keys, bytesTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncSunionAsBytes(Iterable<String> keys) {
        return asyncSunion(keys, bytesTranscoder);
    }

    @Override
    public <T> Future<Set<T>> asyncSunion(Transcoder<T> transcoder,
            String... keys) {
        FirstKeyBytes fkb = toFirstKeyBytes(keys);

        return addOperation(fkb.firstKey, makeSunionOperation(fkb, transcoder));
    }

    @Override
    public <T> Future<Set<T>> asyncSunion(Iterator<String> keys,
            Transcoder<T> transcoder) {
        FirstKeyBytes fkb = toFirstKeyBytes(keys);

        return addOperation(fkb.firstKey, makeSunionOperation(fkb, transcoder));
    }

    @Override
    public <T> Future<Set<T>> asyncSunion(Iterable<String> keys,
            Transcoder<T> transcoder) {
        return asyncSunion(keys.iterator(), transcoder);
    }

    protected <T> OpFuture<Set<T>> makeSunionOperation(FirstKeyBytes fkb,
            Transcoder<T> transcoder) {
        TranscodedSetCallback<T> cb =
                new TranscodedSetCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.sunion(fkb.bytes, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Set<T>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncSunionstore(String destination, String... keys) {
        return addWriteOperation(makeSunionstoreOperation(destination,
                toBytes(keys)));
    }

    @Override
    public Future<Long> asyncSunionstore(String destination,
            Iterator<String> keys) {
        return addWriteOperation(makeSunionstoreOperation(destination,
                toBytes(keys)));
    }

    @Override
    public Future<Long> asyncSunionstore(String destination,
            Iterable<String> keys) {
        return asyncSunionstore(destination, keys.iterator());
    }

    protected OpFuture<Long> makeSunionstoreOperation(String destination,
            byte[][] keys) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.sunionstore(toBytes(destination), keys, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncZadd(String key, StringSortedSetEntry... entries) {
        return asyncZadd(key, stringTranscoder, entries);
    }

    @Override
    public Future<Long> asyncZadd(String key,
            Iterator<StringSortedSetEntry> entries) {
        return asyncZadd(key, entries, stringTranscoder);
    }

    @Override
    public Future<Long> asyncZadd(String key,
            Iterable<StringSortedSetEntry> entries) {
        return asyncZadd(key, entries, stringTranscoder);
    }

    @Override
    public Future<Long> asyncZaddAsBytes(String key,
            BytesSortedSetEntry... entries) {
        return asyncZadd(key, bytesTranscoder, entries);
    }

    @Override
    public Future<Long> asyncZaddAsBytes(String key,
            Iterator<BytesSortedSetEntry> entries) {
        return asyncZadd(key, entries, bytesTranscoder);
    }

    @Override
    public Future<Long> asyncZaddAsBytes(String key,
            Iterable<BytesSortedSetEntry> entries) {
        return asyncZadd(key, entries, bytesTranscoder);
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Future<Long> asyncZadd(String key,
            Transcoder<T> transcoder, U... entries) {
        return addWriteOperation(makeZaddOperation(key, transcoder, entries));
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Future<Long> asyncZadd(String key,
            Iterator<U> entries, Transcoder<T> transcoder) {
        return addWriteOperation(makeZaddOperation(key, transcoder, entries));
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Future<Long> asyncZadd(String key,
            Iterable<U> entries, Transcoder<T> transcoder) {
        return asyncZadd(key, entries.iterator(), transcoder);
    }

    protected <T> OpFuture<Long> makeZaddOperation(String key,
            Transcoder<T> transcoder, SortedSetEntry<T>... entries) {
        List<BytesSortedSetEntry> bytesEntries =
                new ArrayList<BytesSortedSetEntry>(entries.length);
        for (SortedSetEntry<T> entry : entries) {
            bytesEntries.add(new BytesSortedSetEntry(transcoder
                    .encode(entry.value), entry.score));
        }
        return makeZaddOperation(key, bytesEntries);
    }

    protected <T, U extends SortedSetEntry<T>> OpFuture<Long>
            makeZaddOperation(String key, Transcoder<T> transcoder,
                    Iterator<U> entries) {
        List<BytesSortedSetEntry> bytesEntries =
                new ArrayList<BytesSortedSetEntry>();
        while (entries.hasNext()) {
            SortedSetEntry<T> entry = entries.next();
            bytesEntries.add(new BytesSortedSetEntry(transcoder
                    .encode(entry.value), entry.score));
        }
        return makeZaddOperation(key, bytesEntries);
    }

    protected OpFuture<Long> makeZaddOperation(String key,
            List<BytesSortedSetEntry> entries) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.zadd(toBytes(key), entries, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncZcard(String key) {
        return addOperation(key, makeZcardOperation(key));
    }

    protected OpFuture<Long> makeZcardOperation(String key) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.zcard(toBytes(key), cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncZcount(String key, double min, double max) {
        return addOperation(key, makeZcountOperation(key, min, max));
    }

    protected OpFuture<Long> makeZcountOperation(String key, double min,
            double max) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.zcount(toBytes(key), min, max, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Double> asyncZincrby(String key, double increment,
            String member) {
        return asyncZincrby(key, increment, member, stringTranscoder);
    }

    @Override
    public Future<Double> asyncZincrby(String key, double increment,
            byte[] member) {
        return asyncZincrby(key, increment, member, bytesTranscoder);
    }

    @Override
    public <T> Future<Double> asyncZincrby(String key, double increment,
            T member, Transcoder<T> transcoder) {
        return addWriteOperation(makeZincrbyOperation(key, increment,
                transcoder.encode(member)));
    }

    protected OpFuture<Double> makeZincrbyOperation(String key,
            double increment, byte[] member) {
        DoubleCallback cb =
                new DoubleCallback(operationTimeout, executorService);
        Operation op = opFact.zincrby(toBytes(key), increment, member, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Double>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncZinterstore(String destination, String... keys) {
        return asyncZinterstore(destination, null, keys);
    }

    @Override
    public Future<Long> asyncZinterstore(String destination,
            Iterator<String> keys) {
        return asyncZinterstore(destination, keys, null);
    }

    @Override
    public Future<Long> asyncZinterstore(String destination,
            Iterable<String> keys) {
        return asyncZinterstore(destination, keys, null);
    }

    @Override
    public Future<Long> asyncZinterstore(String destination,
            Aggregation aggregate, String... keys) {
        return addWriteOperation(makeZinterstoreOperation(destination,
                toBytes(keys), null, aggregate));
    }

    @Override
    public Future<Long> asyncZinterstore(String destination,
            Iterator<String> keys, Aggregation aggregate) {
        return addWriteOperation(makeZinterstoreOperation(destination,
                toBytes(keys), null, aggregate));
    }

    @Override
    public Future<Long> asyncZinterstore(String destination,
            Iterable<String> keys, Aggregation aggregate) {
        return asyncZinterstore(destination, keys.iterator(), aggregate);
    }

    @Override
    public Future<Long> asyncZinterstoreWeights(String destination,
            KeyWeight... keyweights) {
        return asyncZinterstoreWeights(destination, null, keyweights);
    }

    @Override
    public Future<Long> asyncZinterstoreWeights(String destination,
            Iterator<KeyWeight> keyweights) {
        return asyncZinterstoreWeights(destination, keyweights, null);
    }

    @Override
    public Future<Long> asyncZinterstoreWeights(String destination,
            Iterable<KeyWeight> keyweights) {
        return asyncZinterstoreWeights(destination, keyweights, null);
    }

    @Override
    public Future<Long> asyncZinterstoreWeights(String destination,
            Aggregation aggregate, KeyWeight... keyweights) {
        return addWriteOperation(makeZinterstoreOperation(destination,
                keyweights, aggregate));
    }

    @Override
    public Future<Long> asyncZinterstoreWeights(String destination,
            Iterator<KeyWeight> keyweights, Aggregation aggregate) {
        return addWriteOperation(makeZinterstoreOperation(destination,
                keyweights, aggregate));
    }

    @Override
    public Future<Long> asyncZinterstoreWeights(String destination,
            Iterable<KeyWeight> keyweights, Aggregation aggregate) {
        return asyncZinterstoreWeights(destination, keyweights.iterator(),
                aggregate);
    }

    protected OpFuture<Long> makeZinterstoreOperation(String destination,
            KeyWeight[] keyWeights, Aggregation aggregate) {
        byte[][] keys = new byte[keyWeights.length][];
        double[] weights = new double[keyWeights.length];
        int index = 0;
        for (KeyWeight keyWeight : keyWeights) {
            keys[index] = toBytes(keyWeight.key);
            weights[index] = keyWeight.weight;
            index++;
        }
        return makeZinterstoreOperation(destination, keys, weights, aggregate);
    }

    protected OpFuture<Long> makeZinterstoreOperation(String destination,
            Iterator<KeyWeight> keyWeights, Aggregation aggregate) {
        List<byte[]> keys = new ArrayList<byte[]>();
        List<Double> weights = new ArrayList<Double>();

        while (keyWeights.hasNext()) {
            KeyWeight keyWeight = keyWeights.next();
            keys.add(toBytes(keyWeight.key));
            weights.add(keyWeight.weight);
        }
        double[] weightsArr = new double[weights.size()];
        int index = 0;
        for (Double weight : weights) {
            weightsArr[index++] = weight;
        }
        return makeZinterstoreOperation(destination,
                keys.toArray(new byte[keys.size()][]), weightsArr, aggregate);
    }

    protected OpFuture<Long> makeZinterstoreOperation(String destination,
            byte[][] keys, double[] weights, Aggregation aggregate) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op =
                opFact.zinterstore(toBytes(destination), keys, weights,
                        aggregate, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);

    }

    @Override
    public Future<Set<String>> asyncZrange(String key, long start, long stop) {
        return asyncZrange(key, start, stop, stringTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncZrangeAsBytes(String key, long start,
            long stop) {
        return asyncZrange(key, start, stop, bytesTranscoder);
    }

    @Override
    public <T> Future<Set<T>> asyncZrange(String key, long start, long stop,
            Transcoder<T> transcoder) {
        return addOperation(key,
                makeZrangeOperation(key, start, stop, transcoder));
    }

    protected <T> OpFuture<Set<T>> makeZrangeOperation(String key, long start,
            long stop, Transcoder<T> transcoder) {
        TranscodedSetCallback<T> cb =
                new TranscodedSetCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.zrange(toBytes(key), start, stop, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Set<T>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Set<StringSortedSetEntry>> asyncZrangeWithScores(String key,
            long start, long stop) {
        return addOperation(
                key,
                makeZrangeWithScoresOperation(key, start, stop,
                        stringTranscoder, new StringSortedSetCreator()));
    }

    @Override
    public Future<Set<BytesSortedSetEntry>> asyncZrangeWithScoresAsBytes(
            String key, long start, long stop) {
        return addOperation(
                key,
                makeZrangeWithScoresOperation(key, start, stop,
                        bytesTranscoder, new BytesSortedSetCreator()));
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrangeWithScores(String key, long start, long stop,
                    Transcoder<T> transcoder) {
        return addOperation(
                key,
                makeZrangeWithScoresOperation(key, start, stop, transcoder,
                        new SortedSetEntrySetCreator<T, U>()));
    }

    protected <T, U extends SortedSetEntry<T>> OpFuture<Set<U>>
            makeZrangeWithScoresOperation(String key, long start, long stop,
                    Transcoder<T> transcoder, SortedSetCreator<T, U> creator) {
        TranscodedSetWithScoresCallback<T, U> cb =
                new TranscodedSetWithScoresCallback<T, U>(creator, transcoder,
                        tcService, operationTimeout, executorService);
        Operation op = opFact.zrangeWithScores(toBytes(key), start, stop, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Set<U>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Set<String>> asyncZrangebyscore(String key, double min,
            double max) {
        return asyncZrangebyscore(key, min, max, stringTranscoder);
    }

    @Override
    public Future<Set<String>> asyncZrangebyscore(String key,
            IntervalValue min, IntervalValue end) {
        return asyncZrangebyscore(key, min, end, stringTranscoder);
    }

    @Override
    public Future<Set<String>> asyncZrangebyscore(String key, double min,
            double end, long limit, long count) {
        return asyncZrangebyscore(key, min, end, limit, count, stringTranscoder);
    }

    @Override
    public Future<Set<String>> asyncZrangebyscore(String key,
            IntervalValue min, IntervalValue end, long limit, long count) {
        return asyncZrangebyscore(key, min, end, limit, count, stringTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncZrangebyscoreAsBytes(String key,
            double min, double end) {
        return asyncZrangebyscore(key, min, end, bytesTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncZrangebyscoreAsBytes(String key,
            IntervalValue min, IntervalValue end) {
        return asyncZrangebyscore(key, min, end, bytesTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncZrangebyscoreAsBytes(String key,
            double min, double end, long limit, long count) {
        return asyncZrangebyscore(key, min, end, limit, count, bytesTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncZrangebyscoreAsBytes(String key,
            IntervalValue min, IntervalValue end, long limit, long count) {
        return asyncZrangebyscore(key, min, end, limit, count, bytesTranscoder);
    }

    @Override
    public <T> Future<Set<T>> asyncZrangebyscore(String key, double min,
            double end, Transcoder<T> transcoder) {
        return asyncZrangebyscore(key, new InclusiveValue(min),
                new InclusiveValue(end), transcoder);
    }

    @Override
    public <T> Future<Set<T>> asyncZrangebyscore(String key, IntervalValue min,
            IntervalValue end, Transcoder<T> transcoder) {
        return addOperation(
                key,
                makeZrangebyscoreOperation(key, min, end, null, null,
                        transcoder));
    }

    @Override
    public <T> Future<Set<T>> asyncZrangebyscore(String key, double min,
            double end, long limit, long count, Transcoder<T> transcoder) {
        return asyncZrangebyscore(key, new InclusiveValue(min),
                new InclusiveValue(end), limit, count, transcoder);
    }

    @Override
    public <T> Future<Set<T>>
            asyncZrangebyscore(String key, IntervalValue min,
                    IntervalValue end, long limit, long count,
                    Transcoder<T> transcoder) {
        return addOperation(
                key,
                makeZrangebyscoreOperation(key, min, end, limit, count,
                        transcoder));
    }

    protected <T> OpFuture<Set<T>> makeZrangebyscoreOperation(String key,
            IntervalValue min, IntervalValue end, Long limit, Long count,
            Transcoder<T> transcoder) {
        TranscodedSetCallback<T> cb =
                new TranscodedSetCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op =
                opFact.zrangebyscores(toBytes(key), min, end, limit, count, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Set<T>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Set<StringSortedSetEntry>> asyncZrangebyscoreWithScores(
            String key, double min, double end) {
        return asyncZrangebyscoreWithScores(key, new InclusiveValue(min),
                new InclusiveValue(end));
    }

    @Override
    public Future<Set<StringSortedSetEntry>> asyncZrangebyscoreWithScores(
            String key, IntervalValue min, IntervalValue end) {
        return addOperation(
                key,
                makeZrangebyscoreWithScoresOperation(key, min, end, null, null,
                        stringTranscoder, new StringSortedSetCreator()));
    }

    @Override
    public Future<Set<StringSortedSetEntry>> asyncZrangebyscoreWithScores(
            String key, double min, double end, long limit, long count) {
        return asyncZrangebyscoreWithScores(key, new InclusiveValue(min),
                new InclusiveValue(end), limit, count);
    }

    @Override
    public Future<Set<StringSortedSetEntry>> asyncZrangebyscoreWithScores(
            String key, IntervalValue min, IntervalValue end, long limit,
            long count) {
        return addOperation(
                key,
                makeZrangebyscoreWithScoresOperation(key, min, end, limit,
                        count, stringTranscoder, new StringSortedSetCreator()));
    }

    @Override
    public Future<Set<BytesSortedSetEntry>>
            asyncZrangebyscoreWithScoresAsBytes(String key, double min,
                    double end) {
        return asyncZrangebyscoreWithScoresAsBytes(key,
                new InclusiveValue(min), new InclusiveValue(end));
    }

    @Override
    public Future<Set<BytesSortedSetEntry>>
            asyncZrangebyscoreWithScoresAsBytes(String key, IntervalValue min,
                    IntervalValue end) {
        return addOperation(
                key,
                makeZrangebyscoreWithScoresOperation(key, min, end, null, null,
                        bytesTranscoder, new BytesSortedSetCreator()));
    }

    @Override
    public Future<Set<BytesSortedSetEntry>>
            asyncZrangebyscoreWithScoresAsBytes(String key, double min,
                    double end, long limit, long count) {
        return asyncZrangebyscoreWithScoresAsBytes(key,
                new InclusiveValue(min), new InclusiveValue(end), limit, count);
    }

    @Override
    public Future<Set<BytesSortedSetEntry>>
            asyncZrangebyscoreWithScoresAsBytes(String key, IntervalValue min,
                    IntervalValue end, long limit, long count) {
        return addOperation(
                key,
                makeZrangebyscoreWithScoresOperation(key, min, end, limit,
                        count, bytesTranscoder, new BytesSortedSetCreator()));
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrangebyscoreWithScores(String key, double min, double end,
                    Transcoder<T> transcoder) {
        return asyncZrangebyscoreWithScores(key, new InclusiveValue(min),
                new InclusiveValue(end), transcoder);
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrangebyscoreWithScores(String key, IntervalValue min,
                    IntervalValue end, Transcoder<T> transcoder) {
        return addOperation(
                key,
                makeZrangebyscoreWithScoresOperation(key, min, end, null, null,
                        transcoder, new SortedSetEntrySetCreator<T, U>()));
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrangebyscoreWithScores(String key, double min, double end,
                    long limit, long count, Transcoder<T> transcoder) {
        return asyncZrangebyscoreWithScores(key, new InclusiveValue(min),
                new InclusiveValue(end), limit, count, transcoder);
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrangebyscoreWithScores(String key, IntervalValue min,
                    IntervalValue end, long limit, long count,
                    Transcoder<T> transcoder) {
        return addOperation(
                key,
                makeZrangebyscoreWithScoresOperation(key, min, end, limit,
                        count, transcoder, new SortedSetEntrySetCreator<T, U>()));
    }

    protected <T, U extends SortedSetEntry<T>> OpFuture<Set<U>>
            makeZrangebyscoreWithScoresOperation(String key, IntervalValue min,
                    IntervalValue end, Long limit, Long count,
                    Transcoder<T> transcoder, SortedSetCreator<T, U> creator) {
        TranscodedSetWithScoresCallback<T, U> cb =
                new TranscodedSetWithScoresCallback<T, U>(creator, transcoder,
                        tcService, operationTimeout, executorService);
        Operation op =
                opFact.zrangebyscoresWithScores(toBytes(key), min, end, limit,
                        count, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Set<U>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncZrank(String key, String member) {
        return asyncZrank(key, member, stringTranscoder);
    }

    @Override
    public Future<Long> asyncZrank(String key, byte[] member) {
        return asyncZrank(key, member, bytesTranscoder);
    }

    @Override
    public <T> Future<Long> asyncZrank(String key, T member,
            Transcoder<T> transcoder) {
        return addOperation(key,
                makeZrankOperation(key, transcoder.encode(member)));
    }

    protected OpFuture<Long> makeZrankOperation(String key, byte[] member) {
        NullableLongCallback cb =
                new NullableLongCallback(operationTimeout, executorService);
        Operation op = opFact.zrank(toBytes(key), member, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncZrem(String key, String... members) {
        return asyncZrem(key, stringTranscoder, members);
    }

    @Override
    public Future<Long> asyncZrem(String key, Iterator<String> members) {
        return asyncZrem(key, members, stringTranscoder);
    }

    @Override
    public Future<Long> asyncZrem(String key, Iterable<String> members) {
        return asyncZrem(key, members, stringTranscoder);
    }

    @Override
    public Future<Long> asyncZremAsBytes(String key, byte[]... members) {
        return asyncZrem(key, bytesTranscoder, members);
    }

    @Override
    public Future<Long> asyncZremAsBytes(String key, Iterator<byte[]> members) {
        return asyncZrem(key, members, bytesTranscoder);
    }

    @Override
    public Future<Long> asyncZremAsBytes(String key, Iterable<byte[]> members) {
        return asyncZrem(key, members, bytesTranscoder);
    }

    @Override
    public <T> Future<Long> asyncZrem(String key, Transcoder<T> transcoder,
            T... members) {
        return addWriteOperation(makeZremOperation(key,
                toBytes(members, transcoder)));
    }

    @Override
    public <T> Future<Long> asyncZrem(String key, Iterator<T> members,
            Transcoder<T> transcoder) {
        return addWriteOperation(makeZremOperation(key,
                toBytes(members, transcoder)));
    }

    @Override
    public <T> Future<Long> asyncZrem(String key, Iterable<T> members,
            Transcoder<T> transcoder) {
        return asyncZrem(key, members.iterator(), transcoder);
    }

    protected OpFuture<Long> makeZremOperation(String key, byte[][] members) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.zrem(toBytes(key), members, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncZremrangebyrank(String key, long start, long stop) {
        return addWriteOperation(makeZremrangebyrankOperation(key, start, stop));
    }

    protected OpFuture<Long> makeZremrangebyrankOperation(String key,
            long start, long stop) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.zremrangebyrank(toBytes(key), start, stop, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long>
            asyncZremrangebyscore(String key, double min, double max) {
        return addWriteOperation(makeZremrangebyscoreOperation(key, min, max));
    }

    protected OpFuture<Long> makeZremrangebyscoreOperation(String key,
            double min, double max) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op = opFact.zremrangebyscore(toBytes(key), min, max, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Set<String>>
            asyncZrevrange(String key, long start, long stop) {
        return asyncZrevrange(key, start, stop, stringTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncZrevrangeAsBytes(String key, long start,
            long stop) {
        return asyncZrevrange(key, start, stop, bytesTranscoder);
    }

    @Override
    public <T> Future<Set<T>> asyncZrevrange(String key, long start, long stop,
            Transcoder<T> transcoder) {
        return addOperation(key,
                makeZrevrangeOperation(key, start, stop, transcoder));
    }

    protected <T> OpFuture<Set<T>> makeZrevrangeOperation(String key,
            long start, long stop, Transcoder<T> transcoder) {
        TranscodedSetCallback<T> cb =
                new TranscodedSetCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op = opFact.zrevrange(toBytes(key), start, stop, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Set<T>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Set<StringSortedSetEntry>> asyncZrevrangeWithScores(
            String key, long start, long stop) {
        return addOperation(
                key,
                makeZrevrangeWithScoresOperation(key, start, stop,
                        stringTranscoder, new StringSortedSetCreator()));
    }

    @Override
    public Future<Set<BytesSortedSetEntry>> asyncZrevrangeWithScoresAsBytes(
            String key, long start, long stop) {
        return addOperation(
                key,
                makeZrevrangeWithScoresOperation(key, start, stop,
                        bytesTranscoder, new BytesSortedSetCreator()));
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrevrangeWithScores(String key, long start, long stop,
                    Transcoder<T> transcoder) {
        return addOperation(
                key,
                makeZrevrangeWithScoresOperation(key, start, stop, transcoder,
                        new SortedSetEntrySetCreator<T, U>()));
    }

    protected <T, U extends SortedSetEntry<T>> OpFuture<Set<U>>
            makeZrevrangeWithScoresOperation(String key, long start, long stop,
                    Transcoder<T> transcoder, SortedSetCreator<T, U> creator) {
        TranscodedSetWithScoresCallback<T, U> cb =
                new TranscodedSetWithScoresCallback<T, U>(creator, transcoder,
                        tcService, operationTimeout, executorService);
        Operation op =
                opFact.zrevrangeWithScores(toBytes(key), start, stop, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Set<U>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Set<String>> asyncZrevrangebyscore(String key, double min,
            double max) {
        return asyncZrevrangebyscore(key, min, max, stringTranscoder);
    }

    @Override
    public Future<Set<String>> asyncZrevrangebyscore(String key,
            IntervalValue min, IntervalValue end) {
        return asyncZrevrangebyscore(key, min, end, stringTranscoder);
    }

    @Override
    public Future<Set<String>> asyncZrevrangebyscore(String key, double min,
            double end, long limit, long count) {
        return asyncZrevrangebyscore(key, min, end, limit, count,
                stringTranscoder);
    }

    @Override
    public Future<Set<String>> asyncZrevrangebyscore(String key,
            IntervalValue min, IntervalValue end, long limit, long count) {
        return asyncZrevrangebyscore(key, min, end, limit, count,
                stringTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncZrevrangebyscoreAsBytes(String key,
            double min, double end) {
        return asyncZrevrangebyscore(key, min, end, bytesTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncZrevrangebyscoreAsBytes(String key,
            IntervalValue min, IntervalValue end) {
        return asyncZrevrangebyscore(key, min, end, bytesTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncZrevrangebyscoreAsBytes(String key,
            double min, double end, long limit, long count) {
        return asyncZrevrangebyscore(key, min, end, limit, count,
                bytesTranscoder);
    }

    @Override
    public Future<Set<byte[]>> asyncZrevrangebyscoreAsBytes(String key,
            IntervalValue min, IntervalValue end, long limit, long count) {
        return asyncZrevrangebyscore(key, min, end, limit, count,
                bytesTranscoder);
    }

    @Override
    public <T> Future<Set<T>> asyncZrevrangebyscore(String key, double min,
            double end, Transcoder<T> transcoder) {
        return asyncZrevrangebyscore(key, new InclusiveValue(min),
                new InclusiveValue(end), transcoder);
    }

    @Override
    public <T> Future<Set<T>> asyncZrevrangebyscore(String key,
            IntervalValue min, IntervalValue end, Transcoder<T> transcoder) {
        return addOperation(
                key,
                makeZrevrangebyscoreOperation(key, min, end, null, null,
                        transcoder));
    }

    @Override
    public <T> Future<Set<T>> asyncZrevrangebyscore(String key, double min,
            double end, long limit, long count, Transcoder<T> transcoder) {
        return asyncZrevrangebyscore(key, new InclusiveValue(min),
                new InclusiveValue(end), limit, count, transcoder);
    }

    @Override
    public <T> Future<Set<T>> asyncZrevrangebyscore(String key,
            IntervalValue min, IntervalValue end, long limit, long count,
            Transcoder<T> transcoder) {
        return addOperation(
                key,
                makeZrevrangebyscoreOperation(key, min, end, limit, count,
                        transcoder));
    }

    protected <T> OpFuture<Set<T>> makeZrevrangebyscoreOperation(String key,
            IntervalValue min, IntervalValue end, Long limit, Long count,
            Transcoder<T> transcoder) {
        TranscodedSetCallback<T> cb =
                new TranscodedSetCallback<T>(transcoder, tcService,
                        operationTimeout, executorService);
        Operation op =
                opFact.zrevrangebyscores(toBytes(key), min, end, limit, count,
                        cb);
        cb.rv.setOperation(op);
        return new OpFuture<Set<T>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Set<StringSortedSetEntry>> asyncZrevrangebyscoreWithScores(
            String key, double min, double end) {
        return asyncZrevrangebyscoreWithScores(key, new InclusiveValue(min),
                new InclusiveValue(end));
    }

    @Override
    public Future<Set<StringSortedSetEntry>> asyncZrevrangebyscoreWithScores(
            String key, IntervalValue min, IntervalValue end) {
        return addOperation(
                key,
                makeZrevrangebyscoreWithScoresOperation(key, min, end, null,
                        null, stringTranscoder, new StringSortedSetCreator()));
    }

    @Override
    public Future<Set<StringSortedSetEntry>> asyncZrevrangebyscoreWithScores(
            String key, double min, double end, long limit, long count) {
        return asyncZrevrangebyscoreWithScores(key, new InclusiveValue(min),
                new InclusiveValue(end), limit, count);
    }

    @Override
    public Future<Set<StringSortedSetEntry>> asyncZrevrangebyscoreWithScores(
            String key, IntervalValue min, IntervalValue end, long limit,
            long count) {
        return addOperation(
                key,
                makeZrevrangebyscoreWithScoresOperation(key, min, end, limit,
                        count, stringTranscoder, new StringSortedSetCreator()));
    }

    @Override
    public Future<Set<BytesSortedSetEntry>>
            asyncZrevrangebyscoreWithScoresAsBytes(String key, double min,
                    double end) {
        return asyncZrevrangebyscoreWithScoresAsBytes(key, new InclusiveValue(
                min), new InclusiveValue(end));
    }

    @Override
    public Future<Set<BytesSortedSetEntry>>
            asyncZrevrangebyscoreWithScoresAsBytes(String key,
                    IntervalValue min, IntervalValue end) {
        return addOperation(
                key,
                makeZrevrangebyscoreWithScoresOperation(key, min, end, null,
                        null, bytesTranscoder, new BytesSortedSetCreator()));
    }

    @Override
    public Future<Set<BytesSortedSetEntry>>
            asyncZrevrangebyscoreWithScoresAsBytes(String key, double min,
                    double end, long limit, long count) {
        return asyncZrevrangebyscoreWithScoresAsBytes(key, new InclusiveValue(
                min), new InclusiveValue(end), limit, count);
    }

    @Override
    public
            Future<Set<BytesSortedSetEntry>>
            asyncZrevrangebyscoreWithScoresAsBytes(String key,
                    IntervalValue min, IntervalValue end, long limit, long count) {
        return addOperation(
                key,
                makeZrevrangebyscoreWithScoresOperation(key, min, end, limit,
                        count, bytesTranscoder, new BytesSortedSetCreator()));
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrevrangebyscoreWithScores(String key, double min, double end,
                    Transcoder<T> transcoder) {
        return asyncZrevrangebyscoreWithScores(key, new InclusiveValue(min),
                new InclusiveValue(end), transcoder);
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrevrangebyscoreWithScores(String key, IntervalValue min,
                    IntervalValue end, Transcoder<T> transcoder) {
        return addOperation(
                key,
                makeZrevrangebyscoreWithScoresOperation(key, min, end, null,
                        null, transcoder, new SortedSetEntrySetCreator<T, U>()));
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrevrangebyscoreWithScores(String key, double min, double end,
                    long limit, long count, Transcoder<T> transcoder) {
        return asyncZrevrangebyscoreWithScores(key, new InclusiveValue(min),
                new InclusiveValue(end), limit, count, transcoder);
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrevrangebyscoreWithScores(String key, IntervalValue min,
                    IntervalValue end, long limit, long count,
                    Transcoder<T> transcoder) {
        return addOperation(
                key,
                makeZrevrangebyscoreWithScoresOperation(key, min, end, limit,
                        count, transcoder, new SortedSetEntrySetCreator<T, U>()));
    }

    protected <T, U extends SortedSetEntry<T>> OpFuture<Set<U>>
            makeZrevrangebyscoreWithScoresOperation(String key,
                    IntervalValue min, IntervalValue end, Long limit,
                    Long count, Transcoder<T> transcoder,
                    SortedSetCreator<T, U> creator) {
        TranscodedSetWithScoresCallback<T, U> cb =
                new TranscodedSetWithScoresCallback<T, U>(creator, transcoder,
                        tcService, operationTimeout, executorService);
        Operation op =
                opFact.zrevrangebyscoresWithScores(toBytes(key), min, end,
                        limit, count, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Set<U>>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncZrevrank(String key, String member) {
        return asyncZrevrank(key, member, stringTranscoder);
    }

    @Override
    public Future<Long> asyncZrevrank(String key, byte[] member) {
        return asyncZrevrank(key, member, bytesTranscoder);
    }

    @Override
    public <T> Future<Long> asyncZrevrank(String key, T member,
            Transcoder<T> transcoder) {
        return addOperation(key,
                makeZrevrankOperation(key, transcoder.encode(member)));
    }

    protected OpFuture<Long> makeZrevrankOperation(String key, byte[] member) {
        NullableLongCallback cb =
                new NullableLongCallback(operationTimeout, executorService);
        Operation op = opFact.zrevrank(toBytes(key), member, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Double> asyncZscore(String key, String member) {
        return asyncZscore(key, member, stringTranscoder);
    }

    @Override
    public Future<Double> asyncZscore(String key, byte[] member) {
        return asyncZscore(key, member, bytesTranscoder);
    }

    @Override
    public <T> Future<Double> asyncZscore(String key, T member,
            Transcoder<T> transcoder) {
        return addOperation(key,
                makeZscoreOperation(key, transcoder.encode(member)));
    }

    protected OpFuture<Double> makeZscoreOperation(String key, byte[] member) {
        NullableDoubleCallback cb =
                new NullableDoubleCallback(operationTimeout, executorService);
        Operation op = opFact.zscore(toBytes(key), member, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Double>(op, cb.rv, operationTimeout);
    }

    @Override
    public Future<Long> asyncZunionstore(String destination, String... keys) {
        return asyncZunionstore(destination, null, keys);
    }

    @Override
    public Future<Long> asyncZunionstore(String destination,
            Iterator<String> keys) {
        return asyncZunionstore(destination, keys, null);
    }

    @Override
    public Future<Long> asyncZunionstore(String destination,
            Iterable<String> keys) {
        return asyncZunionstore(destination, keys, null);
    }

    @Override
    public Future<Long> asyncZunionstore(String destination,
            Aggregation aggregate, String... keys) {
        return addWriteOperation(makeZunionstoreOperation(destination,
                toBytes(keys), null, aggregate));
    }

    @Override
    public Future<Long> asyncZunionstore(String destination,
            Iterator<String> keys, Aggregation aggregate) {
        return addWriteOperation(makeZunionstoreOperation(destination,
                toBytes(keys), null, aggregate));
    }

    @Override
    public Future<Long> asyncZunionstore(String destination,
            Iterable<String> keys, Aggregation aggregate) {
        return asyncZunionstore(destination, keys.iterator(), aggregate);
    }

    @Override
    public Future<Long> asyncZunionstoreWeights(String destination,
            KeyWeight... keyweights) {
        return asyncZunionstoreWeights(destination, null, keyweights);
    }

    @Override
    public Future<Long> asyncZunionstoreWeights(String destination,
            Iterator<KeyWeight> keyweights) {
        return asyncZunionstoreWeights(destination, keyweights, null);
    }

    @Override
    public Future<Long> asyncZunionstoreWeights(String destination,
            Iterable<KeyWeight> keyweights) {
        return asyncZunionstoreWeights(destination, keyweights, null);
    }

    @Override
    public Future<Long> asyncZunionstoreWeights(String destination,
            Aggregation aggregate, KeyWeight... keyweights) {
        return addWriteOperation(makeZunionstoreOperation(destination,
                keyweights, aggregate));
    }

    @Override
    public Future<Long> asyncZunionstoreWeights(String destination,
            Iterator<KeyWeight> keyweights, Aggregation aggregate) {
        return addWriteOperation(makeZunionstoreOperation(destination,
                keyweights, aggregate));
    }

    @Override
    public Future<Long> asyncZunionstoreWeights(String destination,
            Iterable<KeyWeight> keyweights, Aggregation aggregate) {
        return asyncZunionstoreWeights(destination, keyweights.iterator(),
                aggregate);
    }

    protected OpFuture<Long> makeZunionstoreOperation(String destination,
            KeyWeight[] keyWeights, Aggregation aggregate) {
        byte[][] keys = new byte[keyWeights.length][];
        double[] weights = new double[keyWeights.length];
        int index = 0;
        for (KeyWeight keyWeight : keyWeights) {
            keys[index] = toBytes(keyWeight.key);
            weights[index] = keyWeight.weight;
            index++;
        }
        return makeZunionstoreOperation(destination, keys, weights, aggregate);
    }

    protected OpFuture<Long> makeZunionstoreOperation(String destination,
            Iterator<KeyWeight> keyWeights, Aggregation aggregate) {
        List<byte[]> keys = new ArrayList<byte[]>();
        List<Double> weights = new ArrayList<Double>();

        while (keyWeights.hasNext()) {
            KeyWeight keyWeight = keyWeights.next();
            keys.add(toBytes(keyWeight.key));
            weights.add(keyWeight.weight);
        }
        double[] weightsArr = new double[weights.size()];
        int index = 0;
        for (Double weight : weights) {
            weightsArr[index++] = weight;
        }
        return makeZunionstoreOperation(destination,
                keys.toArray(new byte[keys.size()][]), weightsArr, aggregate);
    }

    protected OpFuture<Long> makeZunionstoreOperation(String destination,
            byte[][] keys, double[] weights, Aggregation aggregate) {
        LongCallback cb = new LongCallback(operationTimeout, executorService);
        Operation op =
                opFact.zunionstore(toBytes(destination), keys, weights,
                        aggregate, cb);
        cb.rv.setOperation(op);
        return new OpFuture<Long>(op, cb.rv, operationTimeout);

    }

    protected static byte[] toBytes(String str) {
        return str.getBytes(UTF_8);
    }

    protected static byte[][] toBytes(String[] strings) {
        return toBytes(strings, stringTranscoder);
    }

    protected static <T> byte[][]
            toBytes(T[] strings, Transcoder<T> transcoder) {
        byte[][] bytes = new byte[strings.length][];
        int index = 0;
        for (T key : strings) {
            bytes[index++] = transcoder.encode(key);
        }
        return bytes;
    }

    protected static byte[][] toBytes(Iterator<String> keysIter) {
        return toBytes(keysIter, stringTranscoder);
    }

    protected static <T> byte[][] toBytes(Iterator<T> keysIter,
            Transcoder<T> transcoder) {
        List<byte[]> bytes = new ArrayList<byte[]>();
        while (keysIter.hasNext()) {
            bytes.add(transcoder.encode(keysIter.next()));
        }
        return bytes.toArray(new byte[bytes.size()][]);
    }

    private static class FirstKeyBytes {
        public String firstKey;
        public byte[][] bytes;
    }

    protected static FirstKeyBytes toFirstKeyBytes(String[] strings) {
        FirstKeyBytes fkb = new FirstKeyBytes();
        fkb.bytes = new byte[strings.length][];
        int index = 0;
        for (String key : strings) {
            if (index == 0) {
                fkb.firstKey = key;
            }
            fkb.bytes[index++] = toBytes(key);
        }
        return fkb;
    }

    protected static FirstKeyBytes toFirstKeyBytes(Iterator<String> keysIter) {
        List<byte[]> bytes = new ArrayList<byte[]>();
        boolean first = true;
        String firstKey = null;
        while (keysIter.hasNext()) {
            String key = keysIter.next();
            if (first) {
                firstKey = key;
            }
            bytes.add(toBytes(key));
        }
        FirstKeyBytes fkb = new FirstKeyBytes();
        fkb.firstKey = firstKey;
        return fkb;
    }

    protected String buildTimeoutMessage(long timeWaited, TimeUnit unit) {
        StringBuilder message = new StringBuilder();

        message.append(MessageFormat.format("waited {0} ms.",
                unit.convert(timeWaited, TimeUnit.MILLISECONDS)));
        message.append(" Node status: ").append(rconn.connectionsStatus());
        return message.toString();
    }

    protected static class IntegerCallback implements IntegerReplyCallback {
        protected final OperationFuture<Integer> rv;
        private final CountDownLatch latch;
        private int results;

        public IntegerCallback(long operationTimeout,
                ExecutorService executorService) {
            latch = new CountDownLatch(1);
            rv =
                    new OperationFuture<Integer>(latch, operationTimeout,
                            executorService);
        }

        @Override
        public void receivedStatus(OperationStatus status) {
            rv.set(results, status);
        }

        @Override
        public void onReply(int reply) {
            this.results = reply;
        }

        @Override
        public void complete() {
            latch.countDown();
        }
    }

    protected static class LongCallback implements LongReplyCallback {
        protected final OperationFuture<Long> rv;
        private final CountDownLatch latch;
        private long results;

        public LongCallback(long operationTimeout,
                ExecutorService executorService) {
            latch = new CountDownLatch(1);
            rv =
                    new OperationFuture<Long>(latch, operationTimeout,
                            executorService);
        }

        @Override
        public void receivedStatus(OperationStatus status) {
            rv.set(results, status);
        }

        @Override
        public void onReply(long reply) {
            this.results = reply;
        }

        @Override
        public void complete() {
            latch.countDown();
        }
    }

    protected static class NullableLongCallback implements
            NullableLongReplyCallback {
        protected final OperationFuture<Long> rv;
        private final CountDownLatch latch;
        private Long results;

        public NullableLongCallback(long operationTimeout,
                ExecutorService executorService) {
            latch = new CountDownLatch(1);
            rv =
                    new OperationFuture<Long>(latch, operationTimeout,
                            executorService);
        }

        @Override
        public void receivedStatus(OperationStatus status) {
            rv.set(results, status);
        }

        @Override
        public void onReply(long reply) {
            this.results = reply;
        }

        @Override
        public void onNull() {
            results = null;
        }

        @Override
        public void complete() {
            latch.countDown();
        }
    }

    protected static class BytesCallback implements BytesReplyCallback {
        protected final OperationFuture<byte[]> rv;
        private final CountDownLatch latch;
        private byte[] results;

        public BytesCallback(long operationTimeout,
                ExecutorService executorService) {
            latch = new CountDownLatch(1);
            rv =
                    new OperationFuture<byte[]>(latch, operationTimeout,
                            executorService);
        }

        @Override
        public void receivedStatus(OperationStatus status) {
            rv.set(results, status);
        }

        @Override
        public void onReply(byte[] reply) {
            this.results = reply;
        }

        @Override
        public void complete() {
            latch.countDown();
        }
    }

    protected static class StringCallback implements StringReplyCallback {
        protected final OperationFuture<String> rv;
        private final CountDownLatch latch;
        private String results;

        public StringCallback(long operationTimeout,
                ExecutorService executorService) {
            latch = new CountDownLatch(1);
            rv =
                    new OperationFuture<String>(latch, operationTimeout,
                            executorService);
        }

        @Override
        public void receivedStatus(OperationStatus status) {
            rv.set(results, status);
        }

        @Override
        public void onReply(String reply) {
            this.results = reply;
        }

        @Override
        public void complete() {
            latch.countDown();
        }
    }

    protected static class BooleanCallback implements BooleanReplyCallback {
        protected final OperationFuture<Boolean> rv;
        private final CountDownLatch latch;
        private boolean results;

        public BooleanCallback(long operationTimeout,
                ExecutorService executorService) {
            latch = new CountDownLatch(1);
            rv =
                    new OperationFuture<Boolean>(latch, operationTimeout,
                            executorService);
        }

        @Override
        public void receivedStatus(OperationStatus status) {
            rv.set(results, status);
        }

        @Override
        public void onReply(boolean reply) {
            this.results = reply;
        }

        @Override
        public void complete() {
            latch.countDown();
        }
    }

    protected static class VoidBooleanCallback implements BooleanReplyCallback {
        protected final OperationFuture<Void> rv;
        private final CountDownLatch latch;

        public VoidBooleanCallback(long operationTimeout,
                ExecutorService executorService) {
            latch = new CountDownLatch(1);
            rv =
                    new OperationFuture<Void>(latch, operationTimeout,
                            executorService);
        }

        @Override
        public void receivedStatus(OperationStatus status) {
            rv.set(null, status);
        }

        @Override
        public void onReply(boolean reply) {

        }

        @Override
        public void complete() {
            latch.countDown();
        }
    }

    protected static class VoidCallback implements OperationCallback {
        protected final OperationFuture<Void> rv;
        private final CountDownLatch latch;

        public VoidCallback(long operationTimeout,
                ExecutorService executorService) {
            latch = new CountDownLatch(1);
            rv =
                    new OperationFuture<Void>(latch, operationTimeout,
                            executorService);
        }

        @Override
        public void receivedStatus(OperationStatus status) {
            rv.set(null, status);
        }

        @Override
        public void complete() {
            latch.countDown();
        }
    }

    protected static class DoubleCallback implements DoubleReplyCallback {
        protected final OperationFuture<Double> rv;
        private final CountDownLatch latch;
        private double results;

        public DoubleCallback(long operationTimeout,
                ExecutorService executorService) {
            latch = new CountDownLatch(1);
            rv =
                    new OperationFuture<Double>(latch, operationTimeout,
                            executorService);
        }

        @Override
        public void receivedStatus(OperationStatus status) {
            rv.set(results, status);
        }

        @Override
        public void onReply(double reply) {
            this.results = reply;
        }

        @Override
        public void complete() {
            latch.countDown();
        }
    }

    protected static class NullableDoubleCallback implements
            NullableDoubleReplyCallback {
        protected final OperationFuture<Double> rv;
        private final CountDownLatch latch;
        private Double results;

        public NullableDoubleCallback(long operationTimeout,
                ExecutorService executorService) {
            latch = new CountDownLatch(1);
            rv =
                    new OperationFuture<Double>(latch, operationTimeout,
                            executorService);
        }

        @Override
        public void receivedStatus(OperationStatus status) {
            rv.set(results, status);
        }

        @Override
        public void onReply(double reply) {
            this.results = reply;
        }

        @Override
        public void onNull() {
            results = null;
        }

        @Override
        public void complete() {
            latch.countDown();
        }
    }

    protected static class StringSetCallback implements StringListReplyCallback {
        protected final OperationFuture<Set<String>> rv;
        private final CountDownLatch latch;
        private Set<String> val = null;

        public StringSetCallback(long operationTimeout,
                ExecutorService executorService) {
            latch = new CountDownLatch(1);
            rv =
                    new OperationFuture<Set<String>>(latch, operationTimeout,
                            executorService);
        }

        @Override
        public void receivedStatus(OperationStatus status) {
            rv.set(val, status);
        }

        @Override
        public void complete() {
            latch.countDown();
        }

        @Override
        public void onSize(int size) {
            val = new LinkedHashSet<String>(size);
        }

        @Override
        public void onData(String data) {
            val.add(data);
        }

        @Override
        public void onEmptyList() {
            val = Collections.emptySet();
        }
    }

    protected static class TranscodedBytesCallback<T> implements
            BytesReplyCallback {

        protected final GetFuture<T> rv;
        private final CountDownLatch latch;
        private final Transcoder<T> transcoder;
        private final TranscodeService tcService;
        private Future<T> val = null;

        public TranscodedBytesCallback(Transcoder<T> transcoder,
                TranscodeService tcService, long operationTimeout,
                ExecutorService executorService) {
            latch = new CountDownLatch(1);
            rv = new GetFuture<T>(latch, operationTimeout, executorService);
            this.tcService = tcService;
            this.transcoder = transcoder;
        }

        @Override
        public void receivedStatus(OperationStatus status) {
            rv.set(val, status);
        }

        @Override
        public void onReply(byte[] data) {
            val = tcService.decode(transcoder, data);
        }

        @Override
        public void complete() {
            latch.countDown();
        }
    }

    protected interface ListValueTranscoder<T> {
        Future<T> getFuture(TranscodeService tcService, byte[] object);
    }

    protected static class TranscodedListCallback<T> implements
            BytesListReplyCallback {
        protected final ListFuture<T> rv;
        private final CountDownLatch latch;
        private final Transcoder<T> transcoder;
        private final TranscodeService tcService;
        private final ListValueTranscoder<T> transcoderGetter;
        private List<Future<T>> val = null;

        public TranscodedListCallback(Transcoder<T> transcoder,
                TranscodeService tcService, long operationTimeout,
                ExecutorService executorService) {
            latch = new CountDownLatch(1);
            rv = new ListFuture<T>(latch, operationTimeout, executorService);
            this.tcService = tcService;
            this.transcoder = transcoder;
            this.transcoderGetter = null;
        }

        public TranscodedListCallback(ListValueTranscoder<T> transcoderGetter,
                TranscodeService tcService, long operationTimeout,
                ExecutorService executorService) {
            latch = new CountDownLatch(1);
            rv = new ListFuture<T>(latch, operationTimeout, executorService);
            this.tcService = tcService;
            this.transcoder = null;
            this.transcoderGetter = transcoderGetter;
        }

        @Override
        public void receivedStatus(OperationStatus status) {
            rv.set(val, status);
        }

        @Override
        public void complete() {
            latch.countDown();
        }

        @Override
        public void onSize(int size) {
            val = new ArrayList<Future<T>>(size);
        }

        @Override
        public void onEmptyList() {
            val = Collections.emptyList();
        }

        @Override
        public void onData(byte[] data) {
            if (transcoder != null) {
                val.add(tcService.decode(transcoder, data));
            } else {
                val.add(transcoderGetter.getFuture(tcService, data));
            }
        }
    }

    protected static class TranscodedSetCallback<T> implements
            BytesSetReplyCallback {
        protected final SetFuture<T> rv;
        private final CountDownLatch latch;
        private final Transcoder<T> transcoder;
        private final TranscodeService tcService;
        private List<Future<T>> val = null;

        public TranscodedSetCallback(Transcoder<T> transcoder,
                TranscodeService tcService, long operationTimeout,
                ExecutorService executorService) {
            latch = new CountDownLatch(1);
            rv = new SetFuture<T>(latch, operationTimeout, executorService);
            this.tcService = tcService;
            this.transcoder = transcoder;
        }

        @Override
        public void receivedStatus(OperationStatus status) {
            rv.set(val, status);
        }

        @Override
        public void complete() {
            latch.countDown();
        }

        @Override
        public void onSize(int size) {
            val = new ArrayList<Future<T>>(size);
        }

        @Override
        public void onEmptySet() {
            val = Collections.emptyList();
        }

        @Override
        public void onData(byte[] data) {
            val.add(tcService.decode(transcoder, data));
        }
    }

    protected static class TranscodedSetWithScoresCallback<T, U extends SortedSetEntry<T>>
            implements BytesSetWithScoresReplyCallback {
        protected final SetWithScoresFuture<T, U> rv;
        private final CountDownLatch latch;
        private final Transcoder<T> transcoder;
        private final TranscodeService tcService;
        private List<Future<T>> val = null;
        private List<Double> scores = null;

        public TranscodedSetWithScoresCallback(SortedSetCreator<T, U> creator,
                Transcoder<T> transcoder, TranscodeService tcService,
                long operationTimeout, ExecutorService executorService) {
            latch = new CountDownLatch(1);
            rv =
                    new SetWithScoresFuture<T, U>(latch, operationTimeout,
                            executorService, creator);
            this.transcoder = transcoder;
            this.tcService = tcService;
        }

        @Override
        public void receivedStatus(OperationStatus status) {
            rv.set(val, scores, status);
        }

        @Override
        public void complete() {
            latch.countDown();
        }

        @Override
        public void onSize(int size) {
            val = new ArrayList<Future<T>>(size);
            scores = new ArrayList<Double>(size);
        }

        @Override
        public void onEmptySet() {
            val = Collections.emptyList();
        }

        @Override
        public void onData(double score, byte[] data) {
            scores.add(score);
            val.add(tcService.decode(transcoder, data));
        }
    }

    protected static class BlockingPopCallback<T> implements
            BlockingPopReplyCallback {

        private final Transcoder<T> transcoder;
        private final TranscodeService tcService;
        protected final BlockingPopFuture<T> rv;
        private final CountDownLatch latch;
        private String key = null;
        private Future<T> future = null;

        public BlockingPopCallback(int timeout, Transcoder<T> transcoder,
                TranscodeService tcService, ExecutorService executorService) {
            this.transcoder = transcoder;
            this.tcService = tcService;
            latch = new CountDownLatch(1);
            rv =
                    new BlockingPopFuture<T>(latch, timeout * 1000 + 20,
                            executorService);
        }

        @Override
        public void receivedStatus(OperationStatus status) {
            rv.set(key, future, status);
        }

        @Override
        public void complete() {
            latch.countDown();
        }

        @Override
        public void onTimeout() {

        }

        @Override
        public void onReply(String key, byte[] data) {
            this.key = key;
            this.future = tcService.decode(transcoder, data);
        }

    }
}