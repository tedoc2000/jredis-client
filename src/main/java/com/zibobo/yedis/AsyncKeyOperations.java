package com.zibobo.yedis;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import com.zibobo.yedis.transcoder.Transcoder;

public interface AsyncKeyOperations {

    /* del */
    public Future<Integer> asyncDel(String... keys);

    public Future<Integer> asyncDel(Iterator<String> keys);

    public Future<Integer> asyncDel(Iterable<String> keys);

    /* dump */
    public Future<byte[]> asyncDump(String key);

    /* exists */
    public Future<Boolean> asyncExists(String key);

    /* expire */
    public Future<Boolean> asyncExpire(String key, int ttl);

    /* expireat */
    public Future<Boolean> asyncExpireAt(String key, int timestamp);

    /* keys */
    public Future<Set<String>> asyncKeys(String pattern);

    /* migrate */
    public Future<Void> asyncMigrate(String host, int port, String key, int db,
            int timeout);

    public Future<Void> asyncMigrateCopy(String host, int port, String key,
            int db, int timeout);

    public Future<Void> asyncMigrateReplace(String host, int port, String key,
            int db, int timeout);

    public Future<Void> asyncMigrate(String host, int port, String key, int db,
            int timeout, boolean copy, boolean replace);

    /* move */
    public Future<Boolean> asyncMove(String key, int db);

    /* object encoding */
    public Future<String> asyncObjectEncoding(String key);

    /* object idletime */
    public Future<Integer> asyncObjectIdletime(String key);

    /* object refcount */
    public Future<Integer> asyncObjectRefcount(String key);

    /* persist */
    public Future<Boolean> asyncPersist(String key);

    /* pexpire */
    public Future<Boolean> asyncPexpire(String key, long ttl);

    /* pexpireat */
    public Future<Boolean> asyncPexpireAt(String key, long timestamp);

    /* pttl */
    public Future<Long> asyncPttl(String key);

    /* randomkey */
    public Future<String> asyncRandomkey();

    /* rename */
    public Future<Void> asyncRename(String key, String newKey);

    /* renamenx */
    public Future<Boolean> asyncRenamenx(String key, String newKey);

    /* restore */
    public Future<Void> asyncRestore(String key, long ttl, byte[] data);

    /* sort */
    public Future<List<String>> asyncSort(String key);

    public Future<List<String>> asyncSortDesc(String key);

    public Future<List<String>>
            asyncSortLimit(String key, int offset, int count);

    public Future<List<String>> asyncSortBy(String key, String by);

    public Future<List<String>> asyncSortGet(String key, String get);

    public Future<List<String>> asyncSortMultiget(String key, String... get);

    public Future<List<String>> asyncSortMultiget(String key,
            Iterable<String> get);

    public Future<List<String>> asyncSortMultiget(String key,
            Iterator<String> get);

    public Future<List<String>> asyncSort(String key, SortOptions options);

    /* sort bytes */
    public Future<List<byte[]>> asyncSortBytes(String key);

    public Future<List<byte[]>> asyncSortDescBytes(String key);

    public Future<List<byte[]>> asyncSortLimitBytes(String key, int offset,
            int count);

    public Future<List<byte[]>> asyncSortByBytes(String key, String by);

    public Future<List<byte[]>> asyncSortGetBytes(String key, String get);

    public Future<List<byte[]>> asyncSortMultigetBytes(String key,
            String... get);

    public Future<List<byte[]>> asyncSortMultigetBytes(String key,
            Iterable<String> get);

    public Future<List<byte[]>> asyncSortMultigetBytes(String key,
            Iterator<String> get);

    public Future<List<byte[]>> asyncSortBytes(String key, SortOptions options);

    /* sort transcoder */

    public <T> Future<List<T>> asyncSort(String key, Transcoder<T> transcoder,
            boolean alpha);

    public <T> Future<List<T>> asyncSortDesc(String key,
            Transcoder<T> transcoder, boolean alpha);

    public <T> Future<List<T>> asyncSortLimit(String key, int offset,
            int count, Transcoder<T> transcoder, boolean alpha);

    public <T> Future<List<T>> asyncSortBy(String key, String by,
            Transcoder<T> transcoder, boolean alpha);

    public <T> Future<List<T>> asyncSortGet(String key, String get,
            Transcoder<T> transcoder, boolean alpha);

    public <T> Future<List<T>> asyncSortMultiget(String key,
            Transcoder<T> transcoder, boolean alpha, String... get);

    public <T> Future<List<T>> asyncSortMultiget(String key,
            Iterable<String> get, Transcoder<T> transcoder, boolean alpha);

    public <T> Future<List<T>> asyncSortMultiget(String key,
            Iterator<String> get, Transcoder<T> transcoder, boolean alpha);

    public <T> Future<List<T>> asyncSort(String key, SortOptions options,
            Transcoder<T> transcoder, boolean alpha);

    /* sort long */
    public Future<List<Long>> asyncSortLong(String key);

    public Future<List<Long>> asyncSortDescLong(String key);

    public Future<List<Long>> asyncSortLimitLong(String key, int offset,
            int count);

    public Future<List<Long>> asyncSortByLong(String key, String by);

    public Future<List<Long>> asyncSortGetLong(String key, String get);

    public Future<List<Long>> asyncSortMultigetLong(String key, String... get);

    public Future<List<Long>> asyncSortMultigetLong(String key,
            Iterable<String> get);

    public Future<List<Long>> asyncSortMultigetLong(String key,
            Iterator<String> get);

    public Future<List<Long>> asyncSortLong(String key, SortOptions options);

    /* sort double */
    public Future<List<Double>> asyncSortDouble(String key);

    public Future<List<Double>> asyncSortDescDouble(String key);

    public Future<List<Double>> asyncSortLimitDouble(String key, int offset,
            int count);

    public Future<List<Double>> asyncSortByDouble(String key, String by);

    public Future<List<Double>> asyncSortGetDouble(String key, String get);

    public Future<List<Double>> asyncSortMultigetDouble(String key,
            String... get);

    public Future<List<Double>> asyncSortMultigetDouble(String key,
            Iterable<String> get);

    public Future<List<Double>> asyncSortMultigetDouble(String key,
            Iterator<String> get);

    public Future<List<Double>>
            asyncSortDouble(String key, SortOptions options);

    /* sort store */
    public Future<Long> asyncSortStore(String key, boolean alpha,
            String storeKey);

    public Future<Long> asyncSortDescStore(String key, boolean alpha,
            String storeKey);

    public Future<Long> asyncSortLimitStore(String key, int offset, int count,
            boolean alpha, String storeKey);

    public Future<Long> asyncSortByStore(String key, String by, boolean alpha,
            String storeKey);

    public Future<Long> asyncSortGetStore(String key, String get,
            boolean alpha, String storeKey);

    public Future<Long> asyncSortMultigetStore(String key, boolean alpha,
            String storeKey, String... get);

    public Future<Long> asyncSortMultigetStore(String key,
            Iterable<String> get, boolean alpha, String storeKey);

    public Future<Long> asyncSortMultigetStore(String key,
            Iterator<String> get, boolean alpha, String storeKey);

    public Future<Long> asyncSortStore(String key, SortOptions options,
            boolean alpha, String storeKey);

    /* ttl */
    public Future<Integer> asyncTtl(String key);

    /* type */
    public Future<String> asyncType(String key);

}
