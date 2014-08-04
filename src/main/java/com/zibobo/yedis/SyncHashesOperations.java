package com.zibobo.yedis;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zibobo.yedis.transcoder.Transcoder;

public interface SyncHashesOperations {

    /* hdel */
    public int hdel(String key, String... fields);

    public int hdel(String key, Iterable<String> fields);

    public int hdel(String key, Iterator<String> fields);

    /* hexists */
    public boolean hexists(String key, String field);

    /* hget */
    public String hget(String key, String field);

    public byte[] hgetAsBytes(String key, String field);

    public <T> T hget(String key, String field, Transcoder<T> transcoder);

    /* hgetall */
    public Map<String, String> hgetall(String key);

    public Map<String, byte[]> hgetallAsBytes(String key);

    public <T> Map<String, T> hgetall(String key, Transcoder<T> transcoder);

    public Map<String, Object> hgetall(String key,
            Map<String, Transcoder<?>> transcoders);

    /* hincrby */
    public long hincrBy(String key, String field, long by);

    /* hincrbyfloat */
    public double hincrByFloat(String key, String field, double by);

    /* hkeys */
    public Set<String> hkeys(String key);

    /* hlen */
    public int hlen(String key);

    /* hmget */
    public Map<String, String> hmget(String key, String... fields);

    public Map<String, String> hmget(String key, Iterator<String> fields);

    public Map<String, String> hmget(String key, Iterable<String> fields);

    public Map<String, byte[]> mgetAsBytes(String key, String... fields);

    public Map<String, byte[]>
            hmgetAsBytes(String key, Iterator<String> fields);

    public Map<String, byte[]>
            hmgetAsBytes(String key, Iterable<String> fields);

    public <T> Map<String, T> hmget(String key, Transcoder<T> transcoder,
            String... fields);

    public <T> Map<String, T> hmget(String key, Iterator<String> fields,
            Transcoder<T> transcoder);

    public <T> Map<String, T> hmget(String key, Iterable<String> fields,
            Transcoder<T> transcoder);

    public Map<String, Object> hmget(String key,
            Map<String, Transcoder<?>> transcoders);

    /* hmset */
    public void hmset(String key, String... fieldValues);

    public void hmset(String key, Map<String, String> fieldValues);

    public void hmsetBytes(String key, Map<String, byte[]> fieldValues);

    public <T> void hmset(String key, Map<String, T> fieldValues,
            Transcoder<T> transcoder);

    /* hset */
    public boolean hset(String key, String field, String value);

    public boolean hset(String key, String field, byte[] value);

    public <T> boolean hset(String key, String field, T value,
            Transcoder<T> transcoder);

    /* hsetnx */
    public boolean hsetnx(String key, String field, String value);

    public boolean hsetnx(String key, String field, byte[] value);

    public <T> boolean hsetnx(String key, String field, T value,
            Transcoder<T> transcoder);

    /* hvals */
    public List<String> hvals(String key);

    public List<byte[]> hvalsAsBytes(String key);

    public <T> List<T> hvals(String key, Transcoder<T> transcoder);

}
