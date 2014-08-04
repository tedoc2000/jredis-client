package com.zibobo.yedis;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.zibobo.yedis.transcoder.Transcoder;

public interface AsyncStringOperations {

    /* Append */
    /**
     * Append a value to a key
     *
     * If key already exists and is a string, this command appends the value at
     * the end of the string. If key does not exist it is created and set as an
     * empty string, so APPEND will be similar to SET in this special case.
     *
     * @param key
     *            the key
     * @param data
     *            the data to set
     * @return the length of the string after the append operation.
     */
    public Future<Integer> asyncAppend(String key, String data);

    /**
     * Append a value to a key
     *
     * If key already exists and is a string, this command appends the value at
     * the end of the string. If key does not exist it is created and set as an
     * empty string, so APPEND will be similar to SET in this special case.
     *
     * @param key
     *            the key
     * @param data
     *            the data to set
     * @return the length of the string after the append operation.
     */
    public Future<Integer> asyncAppend(String key, byte[] data);

    /**
     * Append a value to a key
     *
     * If key already exists and is a string, this command appends the value at
     * the end of the string. If key does not exist it is created and set as an
     * empty string, so APPEND will be similar to SET in this special case.
     *
     * @param key
     *            the key
     * @param data
     *            the data to set
     * @param transcoder
     *            trancoder of the data
     * @return the length of the string after the append operation.
     */
    public <T> Future<Integer> asyncAppend(String key, T data,
            Transcoder<T> transcoder);

    /* bitcount */
    /**
     * Count the number of set bits (population counting) in a string.
     *
     * Like for the GETRANGE command start and end can contain negative values
     * in order to index bytes starting from the end of the string, where -1 is
     * the last byte, -2 is the penultimate, and so forth. Non-existent keys are
     * treated as empty strings, so the command will return zero.
     *
     * @param key
     *            the key
     * @return
     */
    public Future<Long> asyncBitcount(String key);

    public Future<Long> asyncBitcount(String key, int start, int end);

    /* bitops */
    public Future<Integer> asyncBitOpAnd(String dstKey, String... srcKeys);

    public Future<Integer>
            asyncBitOpAnd(String dstKey, Iterator<String> srcKeys);

    public Future<Integer>
            asyncBitOpAnd(String dstKey, Iterable<String> srcKeys);

    public Future<Integer> asyncBitOpOr(String dstKey, String... srcKeys);

    public Future<Integer>
            asyncBitOpOr(String dstKey, Iterator<String> srcKeys);

    public Future<Integer>
            asyncBitOpOr(String dstKey, Iterable<String> srcKeys);

    public Future<Integer> asyncBitOpXOr(String dstKey, String... srcKeys);

    public Future<Integer>
            asyncBitOpXOr(String dstKey, Iterator<String> srcKeys);

    public Future<Integer>
            asyncBitOpXOr(String dstKey, Iterable<String> srcKeys);

    public Future<Integer> asyncBitOpNot(String dstKey, String srcKey);

    /* decr */
    public Future<Long> asyncDecr(String key);

    /* decrBy */
    public Future<Long> asyncDecrBy(String key, long by);

    /* get */

    public Future<String> asyncGet(String key);

    public Future<byte[]> asyncGetAsBytes(String key);

    public <T> Future<T> asyncGet(String key, Transcoder<T> transcoder);

    /* getbit */
    public Future<Boolean> asyncGetbit(String key, long offset);

    /* getrange */

    public Future<String> asyncGetrange(String key, int start, int end);

    public Future<byte[]> asyncGetrangeAsBytes(String key, int start, int end);

    public <T> Future<T> asyncGetrange(String key, int start, int end,
            Transcoder<T> transcoder);

    /* getset */
    public Future<String> asyncGetset(String key, String newValue);

    public Future<byte[]> asyncGetset(String key, byte[] newValue);

    public <T> Future<T> asyncGetset(String key, T data,
            Transcoder<T> transcoder);

    /* incr */
    public Future<Long> asyncIncr(String key);

    /* incrBy */
    public Future<Long> asyncIncrBy(String key, long by);

    /* incrByFloat */
    public Future<Double> asyncIncrByFloat(String key, double by);

    /* mget */

    public Future<List<String>> asyncMget(String... keys);

    public Future<List<String>> asyncMget(Iterator<String> keys);

    public Future<List<String>> asyncMget(Iterable<String> keys);

    public Future<List<byte[]>> asyncMgetAsBytes(String... keys);

    public Future<List<byte[]>> asyncMgetAsBytes(Iterator<String> keys);

    public Future<List<byte[]>> asyncMgetAsBytes(Iterable<String> keys);

    public <T> Future<List<T>> asyncMget(Transcoder<T> transcoder,
            String... keys);

    public <T> Future<List<T>> asyncMget(Transcoder<T> transcoder,
            Iterator<String> keys);

    public <T> Future<List<T>> asyncMget(Transcoder<T> transcoder,
            Iterable<String> keys);

    public Future<List<Object>> asyncMget(Map<String, Transcoder<?>> keys);

    /* mset */
    public Future<Void> asyncMset(String... keyValues);

    public Future<Void> asyncMset(Map<String, String> keyValues);

    public Future<Void> asyncMsetBytes(Map<String, byte[]> keyValues);

    public <T> Future<Void> asyncMset(Map<String, T> keyValues,
            Transcoder<T> transcoder);

    /* msetnx */
    public Future<Boolean> asyncMsetnx(String... keyValues);

    public Future<Boolean> asyncMsetnx(Map<String, String> keyValues);

    public Future<Boolean> asyncMsetnxBytes(Map<String, byte[]> keyValues);

    public <T> Future<Boolean> asyncMsetnx(Map<String, T> keyValues,
            Transcoder<T> transcoder);

    /* psetex */
    public Future<Void> asyncPsetex(String key, String value, long expire);

    public Future<Void> asyncPsetex(String key, byte[] value, long expire);

    public <T> Future<Void> asyncPsetex(String key, T value, long expire,
            Transcoder<T> transcoder);

    /* set */
    public Future<Void> asyncSet(String key, String value);

    public Future<Void> asyncSet(String key, byte[] value);

    public <T> Future<Void> asyncSet(String key, T value,
            Transcoder<T> transcoder);

    public Future<Boolean> asyncSet(String key, String value,
            Exclusiveness exclusive);

    public Future<Boolean> asyncSet(String key, byte[] value,
            Exclusiveness exclusive);

    public <T> Future<Boolean> asyncSet(String key, T value,
            Exclusiveness exclusive, Transcoder<T> transcoder);

    public Future<Void> asyncSet(String key, String value,
            ExpirationType expType, long expiration);

    public Future<Void> asyncSet(String key, byte[] value,
            ExpirationType expType, long expiration);

    public <T> Future<Void> asyncSet(String key, T value,
            ExpirationType expType, long expiration, Transcoder<T> transcoder);

    public Future<Boolean> asyncSet(String key, String value,
            Exclusiveness exclusive, ExpirationType expType, long expiration);

    public Future<Boolean> asyncSet(String key, byte[] value,
            Exclusiveness exclusive, ExpirationType expType, long expiration);

    public <T> Future<Boolean> asyncSet(String key, T value,
            Exclusiveness exclusive, ExpirationType expType, long expiration,
            Transcoder<T> transcoder);

    /* setbit */
    public Future<Boolean> asyncSetbit(String key, long offset);

    /* setex */
    public Future<Void> asyncSetex(String key, String value, long expire);

    public Future<Void> asyncSetex(String key, byte[] value, long expire);

    public <T> Future<Void> asyncSetex(String key, T value, long expire,
            Transcoder<T> transcoder);

    /* setnx */
    public Future<Boolean> asyncSetnx(String key, String value);

    public Future<Boolean> asyncSetnx(String key, byte[] value);

    public <T> Future<Boolean> asyncSetnx(String key, T value,
            Transcoder<T> transcoder);

    /* setrange */
    public Future<Integer> asyncSetrange(String key, int offset, String data);

    public Future<Integer> asyncSetrange(String key, int offset, byte[] data);

    public <T> Future<Integer> asyncSetrange(String key, int offset, T data,
            Transcoder<T> transcoder);

    /* strlen */
    public Future<Integer> asyncStrlen(String key);

}
