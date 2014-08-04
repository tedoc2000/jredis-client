package com.zibobo.yedis;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zibobo.yedis.transcoder.Transcoder;

public interface SyncStringOperations {

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
    public int append(String key, String data);

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
    public int append(String key, byte[] data);

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
    public <T> int append(String key, T data, Transcoder<T> transcoder);

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
    public long bitcount(String key);

    public long bitcount(String key, int start, int end);

    /* bitops */
    public int bitOpAnd(String dstKey, String... srcKeys);

    public int bitOpAnd(String dstKey, Iterator<String> srcKeys);

    public int bitOpAnd(String dstKey, Iterable<String> srcKeys);

    public int bitOpOr(String dstKey, String... srcKeys);

    public int bitOpOr(String dstKey, Iterator<String> srcKeys);

    public int bitOpOr(String dstKey, Iterable<String> srcKeys);

    public int bitOpXOr(String dstKey, String... srcKeys);

    public int bitOpXOr(String dstKey, Iterator<String> srcKeys);

    public int bitOpXOr(String dstKey, Iterable<String> srcKeys);

    public int bitOpNot(String dstKey, String srcKey);

    /* decr */
    public long decr(String key);

    /* decrBy */
    public long decrBy(String key, long by);

    /* get */

    public String get(String key);

    public byte[] getAsBytes(String key);

    public <T> T get(String key, Transcoder<T> transcoder);

    /* getbit */
    public boolean getbit(String key, long offset);

    /* getrange */

    public String getrange(String key, int start, int end);

    public byte[] getrangeAsBytes(String key, int start, int end);

    public <T> T getrange(String key, int start, int end,
            Transcoder<T> transcoder);

    /* getset */
    public String getset(String key, String newValue);

    public byte[] getset(String key, byte[] newValue);

    public <T> T getset(String key, T data, Transcoder<T> transcoder);

    /* incr */
    public long incr(String key);

    /* incrBy */
    public long incrBy(String key, long by);

    /* incrByFloat */
    public double incrByFloat(String key, double by);

    /* mget */

    public List<String> mget(String... keys);

    public List<String> mget(Iterator<String> keys);

    public List<String> mget(Iterable<String> keys);

    public List<byte[]> mgetAsBytes(String... keys);

    public List<byte[]> mgetAsBytes(Iterator<String> keys);

    public List<byte[]> mgetAsBytes(Iterable<String> keys);

    public <T> List<T> mget(Transcoder<T> transcoder, String... keys);

    public <T> List<T> mget(Transcoder<T> transcoder, Iterator<String> keys);

    public <T> List<T> mget(Transcoder<T> transcoder, Iterable<String> keys);

    public List<Object> mget(Map<String, Transcoder<?>> keys);

    /* mset */
    public void mset(String... keyValues);

    public void mset(Map<String, String> keyValues);

    public void msetBytes(Map<String, byte[]> keyValues);

    public <T> void mset(Map<String, T> keyValues, Transcoder<T> transcoder);

    /* msetnx */
    public boolean msetnx(String... keyValues);

    public boolean msetnx(Map<String, String> keyValues);

    public boolean msetnxBytes(Map<String, byte[]> keyValues);

    public <T> boolean
            msetnx(Map<String, T> keyValues, Transcoder<T> transcoder);

    /* psetex */
    public void psetex(String key, String value, long expire);

    public void psetex(String key, byte[] value, long expire);

    public <T> void psetex(String key, T value, long expire,
            Transcoder<T> transcoder);

    /* set */
    public void set(String key, String value);

    public void set(String key, byte[] value);

    public <T> void set(String key, T value, Transcoder<T> transcoder);

    public boolean set(String key, String value, Exclusiveness exclusive);

    public boolean set(String key, byte[] value, Exclusiveness exclusive);

    public <T> boolean set(String key, T value, Exclusiveness exclusive,
            Transcoder<T> transcoder);

    public void set(String key, String value, ExpirationType expType,
            long expiration);

    public void set(String key, byte[] value, ExpirationType expType,
            long expiration);

    public <T> void set(String key, T value, ExpirationType expType,
            long expiration, Transcoder<T> transcoder);

    public boolean set(String key, String value, Exclusiveness exclusive,
            ExpirationType expType, long expiration);

    public boolean set(String key, byte[] value, Exclusiveness exclusive,
            ExpirationType expType, long expiration);

    public <T> boolean set(String key, T value, Exclusiveness exclusive,
            ExpirationType expType, long expiration, Transcoder<T> transcoder);

    /* setbit */
    public boolean setbit(String key, long offset);

    /* setex */
    public void setex(String key, String value, long expire);

    public void setex(String key, byte[] value, long expire);

    public <T> void setex(String key, T value, long expire,
            Transcoder<T> transcoder);

    /* setnx */
    public boolean setnx(String key, String value);

    public boolean setnx(String key, byte[] value);

    public <T> boolean setnx(String key, T value, Transcoder<T> transcoder);

    /* setrange */
    public int setrange(String key, int offset, String data);

    public int setrange(String key, int offset, byte[] data);

    public <T> int setrange(String key, int offset, T data,
            Transcoder<T> transcoder);

    /* strlen */
    public int strlen(String key);
}
