package com.zibobo.yedis;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import com.zibobo.yedis.transcoder.Transcoder;

public interface AsyncHashesOperations {

    /* hdel */
    public Future<Integer> asyncHdel(String key, String... fields);

    public Future<Integer> asyncHdel(String key, Iterable<String> fields);

    public Future<Integer> asyncHdel(String key, Iterator<String> fields);

    /* hexists */
    public Future<Boolean> asyncHexists(String key, String field);

    /* hget */
    public Future<String> asyncHget(String key, String field);

    public Future<byte[]> asyncHgetAsBytes(String key, String field);

    public <T> Future<T> asyncHget(String key, String field,
            Transcoder<T> transcoder);

    /* hgetall */
    public Future<Map<String, String>> asyncHgetall(String key);

    public Future<Map<String, byte[]>> asyncHgetallAsBytes(String key);

    public <T> Future<Map<String, T>> asyncHgetall(String key,
            Transcoder<T> transcoder);

    public <T> Future<Map<String, Object>> asyncHgetall(String key,
            Map<String, Transcoder<?>> transcoders);

    /* hincrby */
    public Future<Long> asyncHincrBy(String key, String field, long by);

    /* hincrbyfloat */
    public Future<Double>
            asyncHincrByFloat(String key, String field, double by);

    /* hkeys */
    public Future<Set<String>> asyncHkeys(String key);

    /* hlen */
    public Future<Integer> asyncHlen(String key);

    /* hmget */
    public Future<Map<String, String>> asyncHmget(String key, String... fields);

    public Future<Map<String, String>> asyncHmget(String key,
            Iterator<String> fields);

    public Future<Map<String, String>> asyncHmget(String key,
            Iterable<String> fields);

    public Future<Map<String, byte[]>> asyncHmgetAsBytes(String key,
            String... fields);

    public Future<Map<String, byte[]>> asyncHmgetAsBytes(String key,
            Iterator<String> fields);

    public Future<Map<String, byte[]>> asyncHmgetAsBytes(String key,
            Iterable<String> fields);

    public <T> Future<Map<String, T>> asyncHmget(String key,
            Transcoder<T> transcoder, String... fields);

    public <T> Future<Map<String, T>> asyncHmget(String key,
            Iterator<String> fields, Transcoder<T> transcoder);

    public <T> Future<Map<String, T>> asyncHmget(String key,
            Iterable<String> fields, Transcoder<T> transcoder);

    public Future<Map<String, Object>> asyncHmget(String key,
            Map<String, Transcoder<?>> transcoders);

    /* hmset */
    public Future<Void> asyncHmset(String key, String... fieldValues);

    public Future<Void> asyncHmset(String key, Map<String, String> fieldValues);

    public Future<Void> asyncHmsetBytes(String key,
            Map<String, byte[]> fieldValues);

    public <T> Future<Void> asyncHmset(String key, Map<String, T> fieldValues,
            Transcoder<T> transcoder);

    /* hset */
    public Future<Boolean> asyncHset(String key, String field, String value);

    public Future<Boolean> asyncHset(String key, String field, byte[] value);

    public <T> Future<Boolean> asyncHset(String key, String field, T value,
            Transcoder<T> transcoder);

    /* hsetnx */
    public Future<Boolean> asyncHsetnx(String key, String field, String value);

    public Future<Boolean> asyncHsetnx(String key, String field, byte[] value);

    public <T> Future<Boolean> asyncHsetnx(String key, String field, T value,
            Transcoder<T> transcoder);

    /* hvals */
    public Future<List<String>> asyncHvals(String key);

    public Future<List<byte[]>> asyncHvalsAsBytes(String key);

    public <T> Future<List<T>> asyncHvals(String key, Transcoder<T> transcoder);

}
