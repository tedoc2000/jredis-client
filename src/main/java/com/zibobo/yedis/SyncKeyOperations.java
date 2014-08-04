package com.zibobo.yedis;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.zibobo.yedis.transcoder.Transcoder;

public interface SyncKeyOperations {

    /* del */
    public int del(String... keys);

    public int del(Iterator<String> keys);

    public int del(Iterable<String> keys);

    /* dump */
    public byte[] dump(String key);

    /* exists */
    public boolean exists(String key);

    /* expire */
    public boolean expire(String key, int ttl);

    /* expireat */
    public boolean expireAt(String key, int timestamp);

    /* keys */
    public Set<String> keys(String pattern);

    /* migrate */
    public void migrate(String host, int port, String key, int db, int timeout);

    public void migrateCopy(String host, int port, String key, int db,
            int timeout);

    public void migrateReplace(String host, int port, String key, int db,
            int timeout);

    public void migrate(String host, int port, String key, int db, int timeout,
            boolean copy, boolean replace);

    /* move */
    public boolean move(String key, int db);

    /* object encoding */
    public String objectEncoding(String key);

    /* object idletime */
    public int objectIdletime(String key);

    /* object refcount */
    public int objectRefcount(String key);

    /* persist */
    public boolean persist(String key);

    /* pexpire */
    public boolean pexpire(String key, long ttl);

    /* pexpireat */
    public boolean pexpireAt(String key, long timestamp);

    /* pttl */
    public long pttl(String key);

    /* randomkey */
    public String randomkey();

    /* rename */
    public void rename(String key, String newKey);

    /* renamenx */
    public boolean renamenx(String key, String newKey);

    /* restore */
    public void restore(String key, long ttl, byte[] data);

    /* sort */
    public List<String> sort(String key);

    public List<String> sortDesc(String key);

    public List<String> sortLimit(String key, int offset, int count);

    public List<String> sortBy(String key, String by);

    public List<String> sortGet(String key, String get);

    public List<String> sortMultiget(String key, String... get);

    public List<String> sortMultiget(String key, Iterable<String> get);

    public List<String> sortMultiget(String key, Iterator<String> get);

    public List<String> sort(String key, SortOptions options);

    /* sort bytes */
    public List<byte[]> sortBytes(String key);

    public List<byte[]> sortDescBytes(String key);

    public List<byte[]> sortLimitBytes(String key, int offset, int count);

    public List<byte[]> sortByBytes(String key, String by);

    public List<byte[]> sortGetBytes(String key, String get);

    public List<byte[]> sortMultigetBytes(String key, String... get);

    public List<byte[]> sortMultigetBytes(String key, Iterable<String> get);

    public List<byte[]> sortMultigetBytes(String key, Iterator<String> get);

    public List<byte[]> sortBytes(String key, SortOptions options);

    /* sort transcoder */
    public <T> List<T>
            sort(String key, Transcoder<T> transcoder, boolean alpha);

    public <T> List<T> sortDesc(String key, Transcoder<T> transcoder,
            boolean alpha);

    public <T> List<T> sortLimit(String key, int offset, int count,
            Transcoder<T> transcoder, boolean alpha);

    public <T> List<T> sortBy(String key, String by, Transcoder<T> transcoder,
            boolean alpha);

    public <T> List<T> sortGet(String key, String get,
            Transcoder<T> transcoder, boolean alpha);

    public <T> List<T> sortMultiget(String key, Transcoder<T> transcoder,
            boolean alpha, String... get);

    public <T> List<T> sortMultiget(String key, Iterable<String> get,
            Transcoder<T> transcoder, boolean alpha);

    public <T> List<T> sortMultiget(String key, Iterator<String> get,
            Transcoder<T> transcoder, boolean alpha);

    public <T> List<T> sort(String key, SortOptions options,
            Transcoder<T> transcoder, boolean alpha);

    /* sort long */
    public List<Long> sortLong(String key);

    public List<Long> sortDescLong(String key);

    public List<Long> sortLimitLong(String key, int offset, int count);

    public List<Long> sortByLong(String key, String by);

    public List<Long> sortGetLong(String key, String get);

    public List<Long> sortMultigetLong(String key, String... get);

    public List<Long> sortMultigetLong(String key, Iterable<String> get);

    public List<Long> sortMultigetLong(String key, Iterator<String> get);

    public List<Long> sortLong(String key, SortOptions options);

    /* sort double */
    public List<Double> sortDouble(String key);

    public List<Double> sortDescDouble(String key);

    public List<Double> sortLimitDouble(String key, int offset, int count);

    public List<Double> sortByDouble(String key, String by);

    public List<Double> sortGetDouble(String key, String get);

    public List<Double> sortMultigetDouble(String key, String... get);

    public List<Double> sortMultigetDouble(String key, Iterable<String> get);

    public List<Double> sortMultigetDouble(String key, Iterator<String> get);

    public List<Double> sortDouble(String key, SortOptions options);

    /* sort store */
    public long sortStore(String key, boolean alpha, String storeKey);

    public long sortDescStore(String key, boolean alpha, String storeKey);

    public long sortLimitStore(String key, int offset, int count,
            boolean alpha, String storeKey);

    public long sortByStore(String key, String by, boolean alpha,
            String storeKey);

    public long sortGetStore(String key, String get, boolean alpha,
            String storeKey);

    public long sortMultigetStore(String key, boolean alpha, String storeKey,
            String... get);

    public long sortMultigetStore(String key, Iterable<String> get,
            boolean alpha, String storeKey);

    public long sortMultigetStore(String key, Iterator<String> get,
            boolean alpha, String storeKey);

    public long sortStore(String key, SortOptions options, boolean alpha,
            String storeKey);

    /* ttl */
    public int ttl(String key);

    /* type */
    public String type(String key);
}
