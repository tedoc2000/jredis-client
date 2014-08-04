package com.zibobo.yedis;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.zibobo.yedis.exception.RedisException;
import com.zibobo.yedis.internal.OpFuture;
import com.zibobo.yedis.ops.Operation;
import com.zibobo.yedis.ops.OperationStatus;
import com.zibobo.yedis.ops.StringReplyCallback;
import com.zibobo.yedis.transcoder.Transcoder;

public class RedisClient extends AsyncRedisOperator implements RedisClientIF {

    protected volatile boolean shuttingDown = false;

    protected final ConnectionFactory connFactory;

    /**
     * Get a redis client operating on the specified redis locations.
     *
     * @param writeAddr
     *            the location of the redis
     * @throws IOException
     *             if connections cannot be established
     */
    public RedisClient(InetSocketAddress writeAddr) throws IOException {
        this(new DefaultConnectionFactory(), writeAddr, Arrays
                .asList(new InetSocketAddress[] { writeAddr }));
    }

    /**
     * Get a redis client operating on the specified redis locations.
     *
     * @param writeAddr
     *            the write addr
     * @param ia
     *            the redis locations
     * @throws IOException
     *             if connections cannot be established
     */
    public RedisClient(InetSocketAddress writeAddr, InetSocketAddress... ia)
            throws IOException {
        this(new DefaultConnectionFactory(), writeAddr, Arrays.asList(ia));
    }

    /**
     * Get a redis client over the specified redis locations.
     *
     * @param writeAddr
     *            the write node
     * @param addrs
     *            the redis locations
     * @throws IOException
     *             if connections cannot be established
     */
    public RedisClient(InetSocketAddress writeAddr,
            List<InetSocketAddress> addrs) throws IOException {
        this(new DefaultConnectionFactory(), writeAddr, addrs);
    }

    /**
     * Get a redis client over the specified redis locations.
     *
     * @param cf
     *            the connection factory to configure connections for this
     *            client
     * @param addrs
     *            the socket addresses
     * @throws IOException
     *             if connections cannot be established
     */
    public RedisClient(ConnectionFactory cf, InetSocketAddress writeAddr,
            List<InetSocketAddress> addrs) throws IOException {
        super(cf, cf.createConnection(writeAddr, addrs));
        connFactory = cf;

    }

    @Override
    public int append(String key, String data) {
        return doSync(asyncAppend(key, data), "append");
    }

    @Override
    public int append(String key, byte[] data) {
        return doSync(asyncAppend(key, data), "append");
    }

    @Override
    public <T> int append(String key, T data, Transcoder<T> transcoder) {
        return doSync(asyncAppend(key, data, transcoder), "append");
    }

    @Override
    public long bitcount(String key) {
        return doSync(asyncBitcount(key), "bitcount");
    }

    @Override
    public long bitcount(String key, int start, int end) {
        return doSync(asyncBitcount(key, start, end), "bitcount");
    }

    @Override
    public int bitOpAnd(String dstKey, String... srcKeys) {
        return doSync(asyncBitOpAnd(dstKey, srcKeys), "bitop AND");
    }

    @Override
    public int bitOpAnd(String dstKey, Iterator<String> srcKeys) {
        return doSync(asyncBitOpAnd(dstKey, srcKeys), "bitop AND");
    }

    @Override
    public int bitOpAnd(String dstKey, Iterable<String> srcKeys) {
        return doSync(asyncBitOpAnd(dstKey, srcKeys), "bitop AND");
    }

    @Override
    public int bitOpOr(String dstKey, String... srcKeys) {
        return doSync(asyncBitOpOr(dstKey, srcKeys), "bitop OR");
    }

    @Override
    public int bitOpOr(String dstKey, Iterator<String> srcKeys) {
        return doSync(asyncBitOpOr(dstKey, srcKeys), "bitop OR");
    }

    @Override
    public int bitOpOr(String dstKey, Iterable<String> srcKeys) {
        return doSync(asyncBitOpOr(dstKey, srcKeys), "bitop OR");
    }

    @Override
    public int bitOpXOr(String dstKey, String... srcKeys) {
        return doSync(asyncBitOpXOr(dstKey, srcKeys), "bitop XOR");
    }

    @Override
    public int bitOpXOr(String dstKey, Iterator<String> srcKeys) {
        return doSync(asyncBitOpXOr(dstKey, srcKeys), "bitop XOR");
    }

    @Override
    public int bitOpXOr(String dstKey, Iterable<String> srcKeys) {
        return doSync(asyncBitOpXOr(dstKey, srcKeys), "bitop XOR");
    }

    @Override
    public int bitOpNot(String dstKey, String srcKey) {
        return doSync(asyncBitOpNot(dstKey, srcKey), "bitop NOT");
    }

    @Override
    public long decr(String key) {
        return doSync(asyncDecr(key), "decr");
    }

    @Override
    public long decrBy(String key, long by) {
        return doSync(asyncDecrBy(key, by), "decrby");
    }

    @Override
    public String get(String key) {
        return doSync(asyncGet(key), "get");
    }

    @Override
    public byte[] getAsBytes(String key) {
        return doSync(asyncGetAsBytes(key), "get");
    }

    @Override
    public <T> T get(String key, Transcoder<T> transcoder) {
        return doSync(asyncGet(key, transcoder), "get");
    }

    @Override
    public boolean getbit(String key, long offset) {
        return doSync(asyncGetbit(key, offset), "getbit");
    }

    @Override
    public String getrange(String key, int start, int end) {
        return doSync(asyncGetrange(key, start, end), "getrange");
    }

    @Override
    public byte[] getrangeAsBytes(String key, int start, int end) {
        return doSync(asyncGetrangeAsBytes(key, start, end), "getrange");
    }

    @Override
    public <T> T getrange(String key, int start, int end,
            Transcoder<T> transcoder) {
        return doSync(asyncGetrange(key, start, end, transcoder), "getrange");
    }

    @Override
    public String getset(String key, String newValue) {
        return doSync(asyncGetset(key, newValue), "getset");
    }

    @Override
    public byte[] getset(String key, byte[] newValue) {
        return doSync(asyncGetset(key, newValue), "getset");
    }

    @Override
    public <T> T getset(String key, T newValue, Transcoder<T> transcoder) {
        return doSync(asyncGetset(key, newValue, transcoder), "getset");
    }

    @Override
    public long incr(String key) {
        return doSync(asyncIncr(key), "incr");
    }

    @Override
    public long incrBy(String key, long by) {
        return doSync(asyncIncrBy(key, by), "incr");
    }

    @Override
    public double incrByFloat(String key, double by) {
        return doSync(asyncIncrByFloat(key, by), "incrByFloat");
    }

    @Override
    public List<String> mget(String... keys) {
        return doSync(asyncMget(keys), "mget");
    }

    @Override
    public List<String> mget(Iterator<String> keys) {
        return doSync(asyncMget(keys), "mget");
    }

    @Override
    public List<String> mget(Iterable<String> keys) {
        return doSync(asyncMget(keys), "mget");
    }

    @Override
    public List<byte[]> mgetAsBytes(String... keys) {
        return doSync(asyncMgetAsBytes(keys), "mget");
    }

    @Override
    public List<byte[]> mgetAsBytes(Iterator<String> keys) {
        return doSync(asyncMgetAsBytes(keys), "mget");
    }

    @Override
    public List<byte[]> mgetAsBytes(Iterable<String> keys) {
        return doSync(asyncMgetAsBytes(keys), "mget");
    }

    @Override
    public <T> List<T> mget(Transcoder<T> transcoder, String... keys) {
        return doSync(asyncMget(transcoder, keys), "mget");
    }

    @Override
    public <T> List<T> mget(Transcoder<T> transcoder, Iterator<String> keys) {
        return doSync(asyncMget(transcoder, keys), "mget");
    }

    @Override
    public <T> List<T> mget(Transcoder<T> transcoder, Iterable<String> keys) {
        return doSync(asyncMget(transcoder, keys), "mget");
    }

    @Override
    public List<Object> mget(Map<String, Transcoder<?>> keys) {
        return doSync(asyncMget(keys), "mget");
    }

    @Override
    public void mset(String... keyValues) {
        doSync(asyncMset(keyValues), "mset");
    }

    @Override
    public void mset(Map<String, String> keyValues) {
        doSync(asyncMset(keyValues), "mset");
    }

    @Override
    public void msetBytes(Map<String, byte[]> keyValues) {
        doSync(asyncMsetBytes(keyValues), "mset");
    }

    @Override
    public <T> void mset(Map<String, T> keyValues, Transcoder<T> transcoder) {
        doSync(asyncMset(keyValues, transcoder), "mset");
    }

    @Override
    public boolean msetnx(String... keyValues) {
        return doSync(asyncMsetnx(keyValues), "mset");
    }

    @Override
    public boolean msetnx(Map<String, String> keyValues) {
        return doSync(asyncMsetnx(keyValues), "mset");
    }

    @Override
    public boolean msetnxBytes(Map<String, byte[]> keyValues) {
        return doSync(asyncMsetnxBytes(keyValues), "mset");
    }

    @Override
    public <T> boolean
            msetnx(Map<String, T> keyValues, Transcoder<T> transcoder) {
        return doSync(asyncMsetnx(keyValues, transcoder), "mset");
    }

    @Override
    public void psetex(String key, String value, long expire) {
        doSync(asyncPsetex(key, value, expire), "psetex");
    }

    @Override
    public void psetex(String key, byte[] value, long expire) {
        doSync(asyncPsetex(key, value, expire), "psetex");
    }

    @Override
    public <T> void psetex(String key, T value, long expire,
            Transcoder<T> transcoder) {
        doSync(asyncPsetex(key, value, expire, transcoder), "psetex");
    }

    @Override
    public void set(String key, String value) {
        doSync(asyncSet(key, value), "set");
    }

    @Override
    public void set(String key, byte[] value) {
        doSync(asyncSet(key, value), "set");
    }

    @Override
    public <T> void set(String key, T value, Transcoder<T> transcoder) {
        doSync(asyncSet(key, value, transcoder), "set");
    }

    @Override
    public boolean set(String key, String value, Exclusiveness exclusive) {
        return doSync(asyncSet(key, value, exclusive), "set");
    }

    @Override
    public boolean set(String key, byte[] value, Exclusiveness exclusive) {
        return doSync(asyncSet(key, value, exclusive), "set");
    }

    @Override
    public <T> boolean set(String key, T value, Exclusiveness exclusive,
            Transcoder<T> transcoder) {
        return doSync(asyncSet(key, value, exclusive, transcoder), "set");
    }

    @Override
    public void set(String key, String value, ExpirationType expType,
            long expiration) {
        doSync(asyncSet(key, value, expType, expiration), "set");
    }

    @Override
    public void set(String key, byte[] value, ExpirationType expType,
            long expiration) {
        doSync(asyncSet(key, value, expType, expiration), "set");
    }

    @Override
    public <T> void set(String key, T value, ExpirationType expType,
            long expiration, Transcoder<T> transcoder) {
        doSync(asyncSet(key, value, expType, expiration, transcoder), "set");
    }

    @Override
    public boolean set(String key, String value, Exclusiveness exclusive,
            ExpirationType expType, long expiration) {
        return doSync(asyncSet(key, value, exclusive, expType, expiration),
                "set");
    }

    @Override
    public boolean set(String key, byte[] value, Exclusiveness exclusive,
            ExpirationType expType, long expiration) {
        return doSync(asyncSet(key, value, exclusive, expType, expiration),
                "set");
    }

    @Override
    public <T> boolean set(String key, T value, Exclusiveness exclusive,
            ExpirationType expType, long expiration, Transcoder<T> transcoder) {
        return doSync(
                asyncSet(key, value, exclusive, expType, expiration, transcoder),
                "set");
    }

    @Override
    public boolean setbit(String key, long offset) {
        return doSync(asyncSetbit(key, offset), "setbit");
    }

    @Override
    public void setex(String key, String value, long expire) {
        doSync(asyncSetex(key, value, expire), "setex");
    }

    @Override
    public void setex(String key, byte[] value, long expire) {
        doSync(asyncSetex(key, value, expire), "setex");
    }

    @Override
    public <T> void setex(String key, T value, long expire,
            Transcoder<T> transcoder) {
        doSync(asyncSetex(key, value, expire, transcoder), "setex");
    }

    @Override
    public boolean setnx(String key, String value) {
        return doSync(asyncSetnx(key, value), "setnx");
    }

    @Override
    public boolean setnx(String key, byte[] value) {
        return doSync(asyncSetnx(key, value), "setnx");
    }

    @Override
    public <T> boolean setnx(String key, T value, Transcoder<T> transcoder) {
        return doSync(asyncSetnx(key, value, transcoder), "setnx");
    }

    @Override
    public int setrange(String key, int offset, String data) {
        return doSync(asyncSetrange(key, offset, data), "setrange");
    }

    @Override
    public int setrange(String key, int offset, byte[] data) {
        return doSync(asyncSetrange(key, offset, data), "setrange");
    }

    @Override
    public <T> int setrange(String key, int offset, T data,
            Transcoder<T> transcoder) {
        return doSync(asyncSetrange(key, offset, data, transcoder), "setrange");
    }

    @Override
    public int del(String... keys) {
        return doSync(asyncDel(keys), "del");
    }

    @Override
    public int del(Iterator<String> keys) {
        return doSync(asyncDel(keys), "del");
    }

    @Override
    public int del(Iterable<String> keys) {
        return doSync(asyncDel(keys), "del");
    }

    @Override
    public int strlen(String key) {
        return doSync(asyncStrlen(key), "strlen");
    }

    @Override
    public byte[] dump(String key) {
        return doSync(asyncDump(key), "dump");
    }

    @Override
    public boolean exists(String key) {
        return doSync(asyncExists(key), "exists");
    }

    @Override
    public boolean expire(String key, int ttl) {
        return doSync(asyncExpire(key, ttl), "expire");
    }

    @Override
    public boolean expireAt(String key, int timestamp) {
        return doSync(asyncExpireAt(key, timestamp), "expireat");
    }

    @Override
    public Set<String> keys(String pattern) {
        return doSync(asyncKeys(pattern), "keys");
    }

    @Override
    public void migrate(String host, int port, String key, int db, int timeout) {
        doSync(asyncMigrate(host, port, key, db, timeout), "migrate");
    }

    @Override
    public void migrateCopy(String host, int port, String key, int db,
            int timeout) {
        doSync(asyncMigrateCopy(host, port, key, db, timeout), "migrate");
    }

    @Override
    public void migrateReplace(String host, int port, String key, int db,
            int timeout) {
        doSync(asyncMigrateReplace(host, port, key, db, timeout), "migrate");
    }

    @Override
    public void migrate(String host, int port, String key, int db, int timeout,
            boolean copy, boolean replace) {
        doSync(asyncMigrate(host, port, key, db, timeout, copy, replace),
                "migrate");
    }

    @Override
    public boolean move(String key, int db) {
        return doSync(asyncMove(key, db), "move");
    }

    @Override
    public String objectEncoding(String key) {
        return doSync(asyncObjectEncoding(key), "object encoding");
    }

    @Override
    public int objectIdletime(String key) {
        return doSync(asyncObjectIdletime(key), "object idletime");
    }

    @Override
    public int objectRefcount(String key) {
        return doSync(asyncObjectRefcount(key), "object refcount");
    }

    @Override
    public boolean persist(String key) {
        return doSync(asyncPersist(key), "persist");
    }

    @Override
    public boolean pexpire(String key, long ttl) {
        return doSync(asyncPexpire(key, ttl), "pexpire");
    }

    @Override
    public boolean pexpireAt(String key, long timestamp) {
        return doSync(asyncPexpireAt(key, timestamp), "pexpireat");
    }

    @Override
    public long pttl(String key) {
        return doSync(asyncPttl(key), "pttl");
    }

    @Override
    public String randomkey() {
        return doSync(asyncRandomkey(), "randomkey");
    }

    @Override
    public void rename(String key, String newKey) {
        doSync(asyncRename(key, newKey), "rename");
    }

    @Override
    public boolean renamenx(String key, String newKey) {
        return doSync(asyncRenamenx(key, newKey), "renamenx");
    }

    @Override
    public void restore(String key, long ttl, byte[] data) {
        doSync(asyncRestore(key, ttl, data), "restore");
    }

    @Override
    public List<String> sort(String key) {
        return doSync(asyncSort(key), "sort");
    }

    @Override
    public List<String> sortDesc(String key) {
        return doSync(asyncSortDesc(key), "sort");
    }

    @Override
    public List<String> sortLimit(String key, int offset, int count) {
        return doSync(asyncSortLimit(key, offset, count), "sort");
    }

    @Override
    public List<String> sortBy(String key, String by) {
        return doSync(asyncSortBy(key, by), "sort");
    }

    @Override
    public List<String> sortGet(String key, String get) {
        return doSync(asyncSortGet(key, get), "sort");
    }

    @Override
    public List<String> sortMultiget(String key, String... get) {
        return doSync(asyncSortMultiget(key, get), "sort");
    }

    @Override
    public List<String> sortMultiget(String key, Iterable<String> get) {
        return doSync(asyncSortMultiget(key, get), "sort");
    }

    @Override
    public List<String> sortMultiget(String key, Iterator<String> get) {
        return doSync(asyncSortMultiget(key, get), "sort");
    }

    @Override
    public List<String> sort(String key, SortOptions options) {
        return doSync(asyncSort(key, options), "sort");
    }

    @Override
    public List<byte[]> sortBytes(String key) {
        return doSync(asyncSortBytes(key), "sort");
    }

    @Override
    public List<byte[]> sortDescBytes(String key) {
        return doSync(asyncSortDescBytes(key), "sort");
    }

    @Override
    public List<byte[]> sortLimitBytes(String key, int offset, int count) {
        return doSync(asyncSortLimitBytes(key, offset, count), "sort");
    }

    @Override
    public List<byte[]> sortByBytes(String key, String by) {
        return doSync(asyncSortByBytes(key, by), "sort");
    }

    @Override
    public List<byte[]> sortGetBytes(String key, String get) {
        return doSync(asyncSortGetBytes(key, get), "sort");
    }

    @Override
    public List<byte[]> sortMultigetBytes(String key, String... get) {
        return doSync(asyncSortMultigetBytes(key, get), "sort");
    }

    @Override
    public List<byte[]> sortMultigetBytes(String key, Iterable<String> get) {
        return doSync(asyncSortMultigetBytes(key, get), "sort");
    }

    @Override
    public List<byte[]> sortMultigetBytes(String key, Iterator<String> get) {
        return doSync(asyncSortMultigetBytes(key, get), "sort");
    }

    @Override
    public List<byte[]> sortBytes(String key, SortOptions options) {
        return doSync(asyncSortBytes(key, options), "sort");
    }

    @Override
    public <T> List<T>
            sort(String key, Transcoder<T> transcoder, boolean alpha) {
        return doSync(asyncSort(key, transcoder, alpha), "sort");
    }

    @Override
    public <T> List<T> sortDesc(String key, Transcoder<T> transcoder,
            boolean alpha) {
        return doSync(asyncSortDesc(key, transcoder, alpha), "sort");
    }

    @Override
    public <T> List<T> sortLimit(String key, int offset, int count,
            Transcoder<T> transcoder, boolean alpha) {
        return doSync(asyncSortLimit(key, offset, count, transcoder, alpha),
                "sort");
    }

    @Override
    public <T> List<T> sortBy(String key, String by, Transcoder<T> transcoder,
            boolean alpha) {
        return doSync(asyncSortBy(key, by, transcoder, alpha), "sort");
    }

    @Override
    public <T> List<T> sortGet(String key, String get,
            Transcoder<T> transcoder, boolean alpha) {
        return doSync(asyncSortGet(key, get, transcoder, alpha), "sort");
    }

    @Override
    public <T> List<T> sortMultiget(String key, Transcoder<T> transcoder,
            boolean alpha, String... get) {
        return doSync(asyncSortMultiget(key, transcoder, alpha, get), "sort");
    }

    @Override
    public <T> List<T> sortMultiget(String key, Iterable<String> get,
            Transcoder<T> transcoder, boolean alpha) {
        return doSync(asyncSortMultiget(key, get, transcoder, alpha), "sort");
    }

    @Override
    public <T> List<T> sortMultiget(String key, Iterator<String> get,
            Transcoder<T> transcoder, boolean alpha) {
        return doSync(asyncSortMultiget(key, get, transcoder, alpha), "sort");
    }

    @Override
    public <T> List<T> sort(String key, SortOptions options,
            Transcoder<T> transcoder, boolean alpha) {
        return doSync(asyncSort(key, options, transcoder, alpha), "sort");
    }

    @Override
    public List<Long> sortLong(String key) {
        return doSync(asyncSortLong(key), "sort");
    }

    @Override
    public List<Long> sortDescLong(String key) {
        return doSync(asyncSortDescLong(key), "sort");
    }

    @Override
    public List<Long> sortLimitLong(String key, int offset, int count) {
        return doSync(asyncSortLimitLong(key, offset, count), "sort");
    }

    @Override
    public List<Long> sortByLong(String key, String by) {
        return doSync(asyncSortByLong(key, by), "sort");
    }

    @Override
    public List<Long> sortGetLong(String key, String get) {
        return doSync(asyncSortGetLong(key, get), "sort");
    }

    @Override
    public List<Long> sortMultigetLong(String key, String... get) {
        return doSync(asyncSortMultigetLong(key, get), "sort");
    }

    @Override
    public List<Long> sortMultigetLong(String key, Iterable<String> get) {
        return doSync(asyncSortMultigetLong(key, get), "sort");
    }

    @Override
    public List<Long> sortMultigetLong(String key, Iterator<String> get) {
        return doSync(asyncSortMultigetLong(key, get), "sort");
    }

    @Override
    public List<Long> sortLong(String key, SortOptions options) {
        return doSync(asyncSortLong(key, options), "sort");
    }

    @Override
    public List<Double> sortDouble(String key) {
        return doSync(asyncSortDouble(key), "sort");
    }

    @Override
    public List<Double> sortDescDouble(String key) {
        return doSync(asyncSortDescDouble(key), "sort");
    }

    @Override
    public List<Double> sortLimitDouble(String key, int offset, int count) {
        return doSync(asyncSortLimitDouble(key, offset, count), "sort");
    }

    @Override
    public List<Double> sortByDouble(String key, String by) {
        return doSync(asyncSortByDouble(key, by), "sort");
    }

    @Override
    public List<Double> sortGetDouble(String key, String get) {
        return doSync(asyncSortGetDouble(key, get), "sort");
    }

    @Override
    public List<Double> sortMultigetDouble(String key, String... get) {
        return doSync(asyncSortMultigetDouble(key, get), "sort");
    }

    @Override
    public List<Double> sortMultigetDouble(String key, Iterable<String> get) {
        return doSync(asyncSortMultigetDouble(key, get), "sort");
    }

    @Override
    public List<Double> sortMultigetDouble(String key, Iterator<String> get) {
        return doSync(asyncSortMultigetDouble(key, get), "sort");
    }

    @Override
    public List<Double> sortDouble(String key, SortOptions options) {
        return doSync(asyncSortDouble(key, options), "sort");
    }

    @Override
    public long sortStore(String key, boolean alpha, String storeKey) {
        return doSync(asyncSortStore(key, alpha, storeKey), "sort");
    }

    @Override
    public long sortDescStore(String key, boolean alpha, String storeKey) {
        return doSync(asyncSortDescStore(key, alpha, storeKey), "sort");
    }

    @Override
    public long sortLimitStore(String key, int offset, int count,
            boolean alpha, String storeKey) {
        return doSync(asyncSortLimitStore(key, offset, count, alpha, storeKey),
                "sort");
    }

    @Override
    public long sortByStore(String key, String by, boolean alpha,
            String storeKey) {
        return doSync(asyncSortByStore(key, by, alpha, storeKey), "sort");
    }

    @Override
    public long sortGetStore(String key, String get, boolean alpha,
            String storeKey) {
        return doSync(asyncSortGetStore(key, get, alpha, storeKey), "sort");
    }

    @Override
    public long sortMultigetStore(String key, boolean alpha, String storeKey,
            String... get) {
        return doSync(asyncSortMultigetStore(key, alpha, storeKey, get), "sort");
    }

    @Override
    public long sortMultigetStore(String key, Iterable<String> get,
            boolean alpha, String storeKey) {
        return doSync(asyncSortMultigetStore(key, get, alpha, storeKey), "sort");
    }

    @Override
    public long sortMultigetStore(String key, Iterator<String> get,
            boolean alpha, String storeKey) {
        return doSync(asyncSortMultigetStore(key, get, alpha, storeKey), "sort");
    }

    @Override
    public long sortStore(String key, SortOptions options, boolean alpha,
            String storeKey) {
        return doSync(asyncSortStore(key, options, alpha, storeKey), "sort");
    }

    @Override
    public int ttl(String key) {
        return doSync(asyncTtl(key), "ttl");
    }

    @Override
    public String type(String key) {
        return doSync(asyncType(key), "type");
    }

    @Override
    public int hdel(String key, String... fields) {
        return doSync(asyncHdel(key, fields), "hdel");
    }

    @Override
    public int hdel(String key, Iterable<String> fields) {
        return doSync(asyncHdel(key, fields), "hdel");
    }

    @Override
    public int hdel(String key, Iterator<String> fields) {
        return doSync(asyncHdel(key, fields), "hdel");
    }

    @Override
    public boolean hexists(String key, String field) {
        return doSync(asyncHexists(key, field), "hexists");
    }

    @Override
    public String hget(String key, String field) {
        return doSync(asyncHget(key, field), "hget");
    }

    @Override
    public byte[] hgetAsBytes(String key, String field) {
        return doSync(asyncHgetAsBytes(key, field), "hget");
    }

    @Override
    public <T> T hget(String key, String field, Transcoder<T> transcoder) {
        return doSync(asyncHget(key, field, transcoder), "hget");
    }

    @Override
    public Map<String, String> hgetall(String key) {
        return doSync(asyncHgetall(key), "hgetall");
    }

    @Override
    public Map<String, byte[]> hgetallAsBytes(String key) {
        return doSync(asyncHgetallAsBytes(key), "hgetall");
    }

    @Override
    public <T> Map<String, T> hgetall(String key, Transcoder<T> transcoder) {
        return doSync(asyncHgetall(key, transcoder), "hgetall");
    }

    @Override
    public Map<String, Object> hgetall(String key,
            Map<String, Transcoder<?>> transcoders) {
        return doSync(asyncHgetall(key, transcoders), "hgetall");
    }

    @Override
    public long hincrBy(String key, String field, long by) {
        return doSync(asyncHincrBy(key, field, by), "hincrby");
    }

    @Override
    public double hincrByFloat(String key, String field, double by) {
        return doSync(asyncHincrByFloat(key, field, by), "hincrbyfloat");
    }

    @Override
    public Set<String> hkeys(String key) {
        return doSync(asyncHkeys(key), "hkeys");
    }

    @Override
    public int hlen(String key) {
        return doSync(asyncHlen(key), "hlen");
    }

    @Override
    public Map<String, String> hmget(String key, String... fields) {
        return doSync(asyncHmget(key, fields), "hmget");
    }

    @Override
    public Map<String, String> hmget(String key, Iterator<String> fields) {
        return doSync(asyncHmget(key, fields), "hmget");
    }

    @Override
    public Map<String, String> hmget(String key, Iterable<String> fields) {
        return doSync(asyncHmget(key, fields), "hmget");
    }

    @Override
    public Map<String, byte[]> mgetAsBytes(String key, String... fields) {
        return doSync(asyncHmgetAsBytes(key, fields), "hmget");
    }

    @Override
    public Map<String, byte[]>
            hmgetAsBytes(String key, Iterator<String> fields) {
        return doSync(asyncHmgetAsBytes(key, fields), "hmget");
    }

    @Override
    public Map<String, byte[]>
            hmgetAsBytes(String key, Iterable<String> fields) {
        return doSync(asyncHmgetAsBytes(key, fields), "hmget");
    }

    @Override
    public <T> Map<String, T> hmget(String key, Transcoder<T> transcoder,
            String... fields) {
        return doSync(asyncHmget(key, transcoder, fields), "hmget");
    }

    @Override
    public <T> Map<String, T> hmget(String key, Iterator<String> fields,
            Transcoder<T> transcoder) {
        return doSync(asyncHmget(key, fields, transcoder), "hmget");
    }

    @Override
    public <T> Map<String, T> hmget(String key, Iterable<String> fields,
            Transcoder<T> transcoder) {
        return doSync(asyncHmget(key, fields, transcoder), "hmget");
    }

    @Override
    public Map<String, Object> hmget(String key,
            Map<String, Transcoder<?>> transcoders) {
        return doSync(asyncHmget(key, transcoders), "hmget");
    }

    @Override
    public void hmset(String key, String... fieldValues) {
        doSync(asyncHmset(key, fieldValues), "hmset");
    }

    @Override
    public void hmset(String key, Map<String, String> fieldValues) {
        doSync(asyncHmset(key, fieldValues), "hmset");
    }

    @Override
    public void hmsetBytes(String key, Map<String, byte[]> fieldValues) {
        doSync(asyncHmsetBytes(key, fieldValues), "hmset");
    }

    @Override
    public <T> void hmset(String key, Map<String, T> fieldValues,
            Transcoder<T> transcoder) {
        doSync(asyncHmset(key, fieldValues, transcoder), "hmset");
    }

    @Override
    public boolean hset(String key, String field, String value) {
        return doSync(asyncHset(key, field, value), "hset");
    }

    @Override
    public boolean hset(String key, String field, byte[] value) {
        return doSync(asyncHset(key, field, value), "hset");
    }

    @Override
    public <T> boolean hset(String key, String field, T value,
            Transcoder<T> transcoder) {
        return doSync(asyncHset(key, field, value, transcoder), "hset");
    }

    @Override
    public boolean hsetnx(String key, String field, String value) {
        return doSync(asyncHsetnx(key, field, value), "hsetnx");
    }

    @Override
    public boolean hsetnx(String key, String field, byte[] value) {
        return doSync(asyncHsetnx(key, field, value), "hsetnx");
    }

    @Override
    public <T> boolean hsetnx(String key, String field, T value,
            Transcoder<T> transcoder) {
        return doSync(asyncHsetnx(key, field, value, transcoder), "hsetnx");
    }

    @Override
    public List<String> hvals(String key) {
        return doSync(asyncHvals(key), "hvals");
    }

    @Override
    public List<byte[]> hvalsAsBytes(String key) {
        return doSync(asyncHvalsAsBytes(key), "hvals");
    }

    @Override
    public <T> List<T> hvals(String key, Transcoder<T> transcoder) {
        return doSync(asyncHvals(key, transcoder), "hvals");
    }

    @Override
    public BlockingPopResult<String> blpop(int timeout, String... keys) {
        return doSyncTimeout(asyncBlpop(timeout, keys), "blpop", timeout);
    }

    @Override
    public BlockingPopResult<String> blpop(Iterator<String> keys, int timeout) {
        return doSyncTimeout(asyncBlpop(keys, timeout), "blpop", timeout);
    }

    @Override
    public BlockingPopResult<String> blpop(Iterable<String> keys, int timeout) {
        return doSyncTimeout(asyncBlpop(keys, timeout), "blpop", timeout);
    }

    @Override
    public BlockingPopResult<byte[]> blpopAsBytes(int timeout, String... keys) {
        return doSyncTimeout(asyncBlpopAsBytes(timeout, keys), "blpop", timeout);
    }

    @Override
    public BlockingPopResult<byte[]> blpopAsBytes(Iterator<String> keys,
            int timeout) {
        return doSyncTimeout(asyncBlpopAsBytes(keys, timeout), "blpop", timeout);
    }

    @Override
    public BlockingPopResult<byte[]> blpopAsBytes(Iterable<String> keys,
            int timeout) {
        return doSyncTimeout(asyncBlpopAsBytes(keys, timeout), "blpop", timeout);
    }

    @Override
    public <T> BlockingPopResult<T> blpop(int timeout,
            Transcoder<T> transcoder, String... keys) {
        return doSyncTimeout(asyncBlpop(timeout, transcoder, keys), "blpop",
                timeout);
    }

    @Override
    public <T> BlockingPopResult<T> blpop(Iterator<String> keys, int timeout,
            Transcoder<T> transcoder) {
        return doSyncTimeout(asyncBlpop(keys, timeout, transcoder), "blpop",
                timeout);
    }

    @Override
    public <T> BlockingPopResult<T> blpop(Iterable<String> keys, int timeout,
            Transcoder<T> transcoder) {
        return doSyncTimeout(asyncBlpop(keys, timeout, transcoder), "blpop",
                timeout);
    }

    @Override
    public BlockingPopResult<String> brpop(int timeout, String... keys) {
        return doSyncTimeout(asyncBrpop(timeout, keys), "brpop", timeout);
    }

    @Override
    public BlockingPopResult<String> brpop(Iterator<String> keys, int timeout) {
        return doSyncTimeout(asyncBrpop(keys, timeout), "brpop", timeout);
    }

    @Override
    public BlockingPopResult<String> brpop(Iterable<String> keys, int timeout) {
        return doSyncTimeout(asyncBrpop(keys, timeout), "brpop", timeout);
    }

    @Override
    public BlockingPopResult<byte[]> brpopAsBytes(int timeout, String... keys) {
        return doSyncTimeout(asyncBrpopAsBytes(timeout, keys), "brpop", timeout);
    }

    @Override
    public BlockingPopResult<byte[]> brpopAsBytes(Iterator<String> keys,
            int timeout) {
        return doSyncTimeout(asyncBrpopAsBytes(keys, timeout), "brpop", timeout);
    }

    @Override
    public BlockingPopResult<byte[]> brpopAsBytes(Iterable<String> keys,
            int timeout) {
        return doSyncTimeout(asyncBrpopAsBytes(keys, timeout), "brpop", timeout);
    }

    @Override
    public <T> BlockingPopResult<T> brpop(int timeout,
            Transcoder<T> transcoder, String... keys) {
        return doSyncTimeout(asyncBrpop(timeout, transcoder, keys), "brpop",
                timeout);
    }

    @Override
    public <T> BlockingPopResult<T> brpop(Iterator<String> keys, int timeout,
            Transcoder<T> transcoder) {
        return doSyncTimeout(asyncBrpop(keys, timeout, transcoder), "brpop",
                timeout);
    }

    @Override
    public <T> BlockingPopResult<T> brpop(Iterable<String> keys, int timeout,
            Transcoder<T> transcoder) {
        return doSyncTimeout(asyncBrpop(keys, timeout, transcoder), "brpop",
                timeout);
    }

    @Override
    public String brpoplpush(String source, String destination, int timeout) {
        return doSyncTimeout(asyncBrpoplpush(source, destination, timeout),
                "brpoplpush", timeout);
    }

    @Override
    public byte[] brpoplpushAsBytes(String source, String destination,
            int timeout) {
        return doSyncTimeout(
                asyncBrpoplpushAsBytes(source, destination, timeout),
                "brpoplpush", timeout);
    }

    @Override
    public <T> T brpoplpush(String source, String destination, int timeout,
            Transcoder<T> transcoder) {
        return doSyncTimeout(
                asyncBrpoplpush(source, destination, timeout, transcoder),
                "brpoplpush", timeout);
    }

    @Override
    public String lindex(String key, long index) {
        return doSync(asyncLindex(key, index), "lindex");
    }

    @Override
    public byte[] lindexAsBytes(String key, long index) {
        return doSync(asyncLindexAsBytes(key, index), "lindex");
    }

    @Override
    public <T> T lindex(String key, long index, Transcoder<T> transcoder) {
        return doSync(asyncLindex(key, index, transcoder), "lindex");
    }

    @Override
    public long linsertBefore(String key, String pivot, String value) {
        return doSync(asyncLinsertBefore(key, pivot, value), "linsert");
    }

    @Override
    public long linsertBefore(String key, byte[] pivot, byte[] value) {
        return doSync(asyncLinsertBefore(key, pivot, value), "linsert");
    }

    @Override
    public <T> long linsertBefore(String key, T pivot, T value,
            Transcoder<T> transcoder) {
        return doSync(asyncLinsertBefore(key, pivot, value, transcoder),
                "linsert");
    }

    @Override
    public <P, V> long linsertBefore(String key, P pivot,
            Transcoder<P> pivotTranscoder, V value,
            Transcoder<V> valueTranscoder) {
        return doSync(
                asyncLinsertBefore(key, pivot, pivotTranscoder, value,
                        valueTranscoder), "linsert");
    }

    @Override
    public long linsertAfter(String key, String pivot, String value) {
        return doSync(asyncLinsertAfter(key, pivot, value), "linsert");
    }

    @Override
    public long linsertAfter(String key, byte[] pivot, byte[] value) {
        return doSync(asyncLinsertAfter(key, pivot, value), "linsert");
    }

    @Override
    public <T> long linsertAfter(String key, T pivot, T value,
            Transcoder<T> transcoder) {
        return doSync(asyncLinsertAfter(key, pivot, value, transcoder),
                "linsert");
    }

    @Override
    public <P, V> long linsertAfter(String key, P pivot,
            Transcoder<P> pivotTranscoder, V value,
            Transcoder<V> valueTranscoder) {
        return doSync(
                asyncLinsertAfter(key, pivot, pivotTranscoder, value,
                        valueTranscoder), "linsert");
    }

    @Override
    public long llen(String key) {
        return doSync(asyncLlen(key), "llen");
    }

    @Override
    public String lpop(String key) {
        return doSync(asyncLpop(key), "lpop");
    }

    @Override
    public byte[] lpopAsBytes(String key) {
        return doSync(asyncLpopAsBytes(key), "lpop");
    }

    @Override
    public <T> T lpop(String key, Transcoder<T> transcoder) {
        return doSync(asyncLpop(key, transcoder), "lpop");
    }

    @Override
    public long lpush(String key, String... values) {
        return doSync(asyncLpush(key, values), "lpush");
    }

    @Override
    public long lpush(String key, Iterator<String> values) {
        return doSync(asyncLpush(key, values), "lpush");
    }

    @Override
    public long lpush(String key, Iterable<String> values) {
        return doSync(asyncLpush(key, values), "lpush");
    }

    @Override
    public long lpushAsBytes(String key, byte[]... values) {
        return doSync(asyncLpushAsBytes(key, values), "lpush");
    }

    @Override
    public long lpushAsBytes(String key, Iterator<byte[]> values) {
        return doSync(asyncLpushAsBytes(key, values), "lpush");
    }

    @Override
    public long lpushAsBytes(String key, Iterable<byte[]> values) {
        return doSync(asyncLpushAsBytes(key, values), "lpush");
    }

    @Override
    public <T> long lpush(String key, Transcoder<T> transcoder, T... values) {
        return doSync(asyncLpush(key, transcoder, values), "lpush");
    }

    @Override
    public <T> long lpush(String key, Iterator<T> values,
            Transcoder<T> transcoder) {
        return doSync(asyncLpush(key, values, transcoder), "lpush");
    }

    @Override
    public <T> long lpush(String key, Iterable<T> values,
            Transcoder<T> transcoder) {
        return doSync(asyncLpush(key, values, transcoder), "lpush");
    }

    @Override
    public long lpushx(String key, String value) {
        return doSync(asyncLpushx(key, value), "lpushx");
    }

    @Override
    public long lpushx(String key, byte[] value) {
        return doSync(asyncLpushx(key, value), "lpushx");
    }

    @Override
    public <T> long lpushx(String key, T value, Transcoder<T> transcoder) {
        return doSync(asyncLpushx(key, value, transcoder), "lpushx");
    }

    @Override
    public List<String> lrange(String key, long start, long stop) {
        return doSync(asyncLrange(key, start, stop), "lrange");
    }

    @Override
    public List<byte[]> lrangeAsBytes(String key, long start, long stop) {
        return doSync(asyncLrangeAsBytes(key, start, stop), "lrange");
    }

    @Override
    public <T> List<T> lrange(String key, long start, long stop,
            Transcoder<T> transcoder) {
        return doSync(asyncLrange(key, start, stop, transcoder), "lrange");
    }

    @Override
    public long lrem(String key, long count, String value) {
        return doSync(asyncLrem(key, count, value), "lrem");
    }

    @Override
    public long lrem(String key, long count, byte[] value) {
        return doSync(asyncLrem(key, count, value), "lrem");
    }

    @Override
    public <T> long lrem(String key, long count, T value,
            Transcoder<T> transcoder) {
        return doSync(asyncLrem(key, count, value, transcoder), "lrem");
    }

    @Override
    public void lset(String key, long index, String value) {
        doSync(asyncLset(key, index, value), "lset");
    }

    @Override
    public void lset(String key, long index, byte[] value) {
        doSync(asyncLset(key, index, value), "lset");
    }

    @Override
    public <T> void lset(String key, long index, T value,
            Transcoder<T> transcoder) {
        doSync(asyncLset(key, index, value, transcoder), "lset");
    }

    @Override
    public void ltrim(String key, long start, long stop) {
        doSync(asyncLtrim(key, start, stop), "ltrim");
    }

    @Override
    public String rpop(String key) {
        return doSync(asyncRpop(key), "rpop");
    }

    @Override
    public byte[] rpopAsBytes(String key) {
        return doSync(asyncRpopAsBytes(key), "rpop");
    }

    @Override
    public String rpoplpush(String source, String destination) {
        return doSync(asyncRpoplpush(source, destination), "rpoplpush");
    }

    @Override
    public byte[] rpoplpushAsBytes(String source, String destination) {
        return doSync(asyncRpoplpushAsBytes(source, destination), "rpoplpush");
    }

    @Override
    public <T> T rpoplpush(String source, String destination,
            Transcoder<T> transcoder) {
        return doSync(asyncRpoplpush(source, destination, transcoder),
                "rpoplpush");
    }

    @Override
    public <T> T rpop(String key, Transcoder<T> transcoder) {
        return doSync(asyncRpop(key, transcoder), "rpop");
    }

    @Override
    public long rpush(String key, String... values) {
        return doSync(asyncRpush(key, values), "rpush");
    }

    @Override
    public long rpush(String key, Iterator<String> values) {
        return doSync(asyncRpush(key, values), "rpush");
    }

    @Override
    public long rpush(String key, Iterable<String> values) {
        return doSync(asyncRpush(key, values), "rpush");
    }

    @Override
    public long rpushAsBytes(String key, byte[]... values) {
        return doSync(asyncRpushAsBytes(key, values), "rpush");
    }

    @Override
    public long rpushAsBytes(String key, Iterator<byte[]> values) {
        return doSync(asyncRpushAsBytes(key, values), "rpush");
    }

    @Override
    public long rpushAsBytes(String key, Iterable<byte[]> values) {
        return doSync(asyncRpushAsBytes(key, values), "rpush");
    }

    @Override
    public <T> long rpush(String key, Transcoder<T> transcoder, T... values) {
        return doSync(asyncRpush(key, transcoder, values), "rpush");
    }

    @Override
    public <T> long rpush(String key, Iterator<T> values,
            Transcoder<T> transcoder) {
        return doSync(asyncRpush(key, values, transcoder), "rpush");
    }

    @Override
    public <T> long rpush(String key, Iterable<T> values,
            Transcoder<T> transcoder) {
        return doSync(asyncRpush(key, values, transcoder), "rpush");
    }

    @Override
    public long rpushx(String key, String value) {
        return doSync(asyncRpushx(key, value), "rpushx");
    }

    @Override
    public long rpushx(String key, byte[] value) {
        return doSync(asyncRpushx(key, value), "rpushx");
    }

    @Override
    public <T> long rpushx(String key, T value, Transcoder<T> transcoder) {
        return doSync(asyncRpushx(key, value, transcoder), "rpushx");
    }

    @Override
    public long sadd(String key, String... values) {
        return doSync(asyncSadd(key, values), "sadd");
    }

    @Override
    public long sadd(String key, Iterator<String> values) {
        return doSync(asyncSadd(key, values), "sadd");
    }

    @Override
    public long sadd(String key, Iterable<String> values) {
        return doSync(asyncSadd(key, values), "sadd");
    }

    @Override
    public long saddAsBytes(String key, byte[]... values) {
        return doSync(asyncSaddAsBytes(key, values), "sadd");
    }

    @Override
    public long saddAsBytes(String key, Iterator<byte[]> values) {
        return doSync(asyncSaddAsBytes(key, values), "sadd");
    }

    @Override
    public long saddAsBytes(String key, Iterable<byte[]> values) {
        return doSync(asyncSaddAsBytes(key, values), "sadd");
    }

    @Override
    public <T> long sadd(String key, Transcoder<T> transcoder, T... values) {
        return doSync(asyncSadd(key, transcoder, values), "sadd");
    }

    @Override
    public <T> long sadd(String key, Iterator<T> values,
            Transcoder<T> transcoder) {
        return doSync(asyncSadd(key, values, transcoder), "sadd");
    }

    @Override
    public <T> long sadd(String key, Iterable<T> values,
            Transcoder<T> transcoder) {
        return doSync(asyncSadd(key, values, transcoder), "sadd");
    }

    @Override
    public long scard(String key) {
        return doSync(asyncScard(key), "scard");
    }

    @Override
    public Set<String> sdiff(String... keys) {
        return doSync(asyncSdiff(keys), "sdiff");
    }

    @Override
    public Set<String> sdiff(Iterator<String> keys) {
        return doSync(asyncSdiff(keys), "sdiff");
    }

    @Override
    public Set<String> sdiff(Iterable<String> keys) {
        return doSync(asyncSdiff(keys), "sdiff");
    }

    @Override
    public Set<byte[]> sdiffAsBytes(String... keys) {
        return doSync(asyncSdiffAsBytes(keys), "sdiff");
    }

    @Override
    public Set<byte[]> sdiffAsBytes(Iterator<String> keys) {
        return doSync(asyncSdiffAsBytes(keys), "sdiff");
    }

    @Override
    public Set<byte[]> sdiffAsBytes(Iterable<String> keys) {
        return doSync(asyncSdiffAsBytes(keys), "sdiff");
    }

    @Override
    public <T> Set<T> sdiff(Transcoder<T> transcoder, String... keys) {
        return doSync(asyncSdiff(transcoder, keys), "sdiff");
    }

    @Override
    public <T> Set<T> sdiff(Iterator<String> keys, Transcoder<T> transcoder) {
        return doSync(asyncSdiff(keys, transcoder), "sdiff");
    }

    @Override
    public long sdiffstore(String destination, String... keys) {
        return doSync(asyncSdiffstore(destination, keys), "sdiffstore");
    }

    @Override
    public long sdiffstore(String destination, Iterator<String> keys) {
        return doSync(asyncSdiffstore(destination, keys), "sdiffstore");
    }

    @Override
    public long sdiffstore(String destination, Iterable<String> keys) {
        return doSync(asyncSdiffstore(destination, keys), "sdiffstore");
    }

    @Override
    public Set<String> sinter(String... keys) {
        return doSync(asyncSinter(keys), "sinter");
    }

    @Override
    public Set<String> sinter(Iterator<String> keys) {
        return doSync(asyncSinter(keys), "sinter");
    }

    @Override
    public Set<String> sinter(Iterable<String> keys) {
        return doSync(asyncSinter(keys), "sinter");
    }

    @Override
    public Set<byte[]> sinterAsBytes(String... keys) {
        return doSync(asyncSinterAsBytes(keys), "sinter");
    }

    @Override
    public Set<byte[]> sinterAsBytes(Iterator<String> keys) {
        return doSync(asyncSinterAsBytes(keys), "sinter");
    }

    @Override
    public Set<byte[]> sinterAsBytes(Iterable<String> keys) {
        return doSync(asyncSinterAsBytes(keys), "sinter");
    }

    @Override
    public <T> Set<T> sinter(Transcoder<T> transcoder, String... keys) {
        return doSync(asyncSinter(transcoder, keys), "sinter");
    }

    @Override
    public <T> Set<T> sinter(Iterator<String> keys, Transcoder<T> transcoder) {
        return doSync(asyncSinter(keys, transcoder), "sinter");
    }

    @Override
    public long sinterstore(String destination, String... keys) {
        return doSync(asyncSinterstore(destination, keys), "sinterstore");
    }

    @Override
    public long sinterstore(String destination, Iterator<String> keys) {
        return doSync(asyncSinterstore(destination, keys), "sinterstore");
    }

    @Override
    public long sinterstore(String destination, Iterable<String> keys) {
        return doSync(asyncSinterstore(destination, keys), "sinterstore");
    }

    @Override
    public boolean sismember(String key, String member) {
        return doSync(asyncSismember(key, member), "sismember");
    }

    @Override
    public boolean sismember(String key, byte[] member) {
        return doSync(asyncSismember(key, member), "sismember");
    }

    @Override
    public <T> boolean
            sismember(String key, T member, Transcoder<T> transcoder) {
        return doSync(asyncSismember(key, member, transcoder), "sismember");
    }

    @Override
    public Set<String> smembers(String key) {
        return doSync(asyncSmembers(key), "smembers");
    }

    @Override
    public Set<byte[]> smembersAsBytes(String key) {
        return doSync(asyncSmembersAsBytes(key), "smembers");
    }

    @Override
    public <T> Set<T> smembers(String key, Transcoder<T> transcoder) {
        return doSync(asyncSmembers(key, transcoder), "smembers");
    }

    @Override
    public boolean smove(String source, String destination, String value) {
        return doSync(asyncSmove(source, destination, value), "smove");
    }

    @Override
    public boolean smove(String source, String destination, byte[] value) {
        return doSync(asyncSmove(source, destination, value), "smove");
    }

    @Override
    public <T> boolean smove(String source, String destination, T value,
            Transcoder<T> transcoder) {
        return doSync(asyncSmove(source, destination, value, transcoder),
                "smove");
    }

    @Override
    public String spop(String key) {
        return doSync(asyncSpop(key), "spop");
    }

    @Override
    public byte[] spopAsBytes(String key) {
        return doSync(asyncSpopAsBytes(key), "spop");
    }

    @Override
    public <T> T spop(String key, Transcoder<T> transcoder) {
        return doSync(asyncSpop(key, transcoder), "spop");
    }

    @Override
    public String srandmember(String key) {
        return doSync(asyncSrandmember(key), "srandmember");
    }

    @Override
    public byte[] srandmemberAsBytes(String key) {
        return doSync(asyncSrandmemberAsBytes(key), "srandmember");
    }

    @Override
    public <T> T srandmember(String key, Transcoder<T> transcoder) {
        return doSync(asyncSrandmember(key, transcoder), "srandmember");
    }

    @Override
    public Set<String> srandmember(String key, long count) {
        return doSync(asyncSrandmember(key, count), "srandmember");
    }

    @Override
    public Set<byte[]> srandmemberAsBytes(String key, long count) {
        return doSync(asyncSrandmemberAsBytes(key, count), "srandmember");
    }

    @Override
    public <T> Set<T> srandmember(String key, long count,
            Transcoder<T> transcoder) {
        return doSync(asyncSrandmember(key, count, transcoder), "srandmember");
    }

    @Override
    public long srem(String key, String... values) {
        return doSync(asyncSrem(key, values), "srem");
    }

    @Override
    public long srem(String key, Iterator<String> values) {
        return doSync(asyncSrem(key, values), "srem");
    }

    @Override
    public long srem(String key, Iterable<String> values) {
        return doSync(asyncSrem(key, values), "srem");
    }

    @Override
    public long sremAsBytes(String key, byte[]... values) {
        return doSync(asyncSremAsBytes(key, values), "srem");
    }

    @Override
    public long sremAsBytes(String key, Iterator<byte[]> values) {
        return doSync(asyncSremAsBytes(key, values), "srem");
    }

    @Override
    public long sremAsBytes(String key, Iterable<byte[]> values) {
        return doSync(asyncSremAsBytes(key, values), "srem");
    }

    @Override
    public <T> long srem(String key, Transcoder<T> transcoder, T... values) {
        return doSync(asyncSrem(key, transcoder, values), "srem");
    }

    @Override
    public <T> long srem(String key, Iterator<T> values,
            Transcoder<T> transcoder) {
        return doSync(asyncSrem(key, values, transcoder), "srem");
    }

    @Override
    public <T> long srem(String key, Iterable<T> values,
            Transcoder<T> transcoder) {
        return doSync(asyncSrem(key, values, transcoder), "srem");
    }

    @Override
    public Set<String> sunion(String... keys) {
        return doSync(asyncSunion(keys), "sunion");
    }

    @Override
    public Set<String> sunion(Iterator<String> keys) {
        return doSync(asyncSunion(keys), "sunion");
    }

    @Override
    public Set<String> sunion(Iterable<String> keys) {
        return doSync(asyncSunion(keys), "sunion");
    }

    @Override
    public Set<byte[]> sunionAsBytes(String... keys) {
        return doSync(asyncSunionAsBytes(keys), "sunion");
    }

    @Override
    public Set<byte[]> sunionAsBytes(Iterator<String> keys) {
        return doSync(asyncSunionAsBytes(keys), "sunion");
    }

    @Override
    public Set<byte[]> sunionAsBytes(Iterable<String> keys) {
        return doSync(asyncSunionAsBytes(keys), "sunion");
    }

    @Override
    public <T> Set<T> sunion(Transcoder<T> transcoder, String... keys) {
        return doSync(asyncSunion(transcoder, keys), "sunion");
    }

    @Override
    public <T> Set<T> sunion(Iterator<String> keys, Transcoder<T> transcoder) {
        return doSync(asyncSunion(keys, transcoder), "sunion");
    }

    @Override
    public long sunionstore(String destination, String... keys) {
        return doSync(asyncSunionstore(destination, keys), "sunionstore");
    }

    @Override
    public long sunionstore(String destination, Iterator<String> keys) {
        return doSync(asyncSunionstore(destination, keys), "sunionstore");
    }

    @Override
    public long sunionstore(String destination, Iterable<String> keys) {
        return doSync(asyncSunionstore(destination, keys), "sunionstore");
    }

    @Override
    public long zadd(String key, StringSortedSetEntry... entries) {
        return doSync(asyncZadd(key, entries), "zadd");
    }

    @Override
    public long zadd(String key, Iterator<StringSortedSetEntry> entries) {
        return doSync(asyncZadd(key, entries), "zadd");
    }

    @Override
    public long zadd(String key, Iterable<StringSortedSetEntry> entries) {
        return doSync(asyncZadd(key, entries), "zadd");
    }

    @Override
    public long zaddAsBytes(String key, BytesSortedSetEntry... entries) {
        return doSync(asyncZaddAsBytes(key, entries), "zadd");
    }

    @Override
    public long zaddAsBytes(String key, Iterator<BytesSortedSetEntry> entries) {
        return doSync(asyncZaddAsBytes(key, entries), "zadd");
    }

    @Override
    public long zaddAsBytes(String key, Iterable<BytesSortedSetEntry> entries) {
        return doSync(asyncZaddAsBytes(key, entries), "zadd");
    }

    @Override
    public <T, U extends SortedSetEntry<T>> long zadd(String key,
            Transcoder<T> transcoder, U... entries) {
        return doSync(asyncZadd(key, transcoder, entries), "zadd");
    }

    @Override
    public <T, U extends SortedSetEntry<T>> long zadd(String key,
            Iterator<U> entries, Transcoder<T> transcoder) {
        return doSync(asyncZadd(key, entries, transcoder), "zadd");
    }

    @Override
    public <T, U extends SortedSetEntry<T>> long zadd(String key,
            Iterable<U> entries, Transcoder<T> transcoder) {
        return doSync(asyncZadd(key, entries, transcoder), "zadd");
    }

    @Override
    public long zcard(String key) {
        return doSync(asyncZcard(key), "zcard");
    }

    @Override
    public long zcount(String key, double min, double max) {
        return doSync(asyncZcount(key, min, max), "zcount");
    }

    @Override
    public double zincrby(String key, double increment, String member) {
        return doSync(asyncZincrby(key, increment, member), "zincrby");
    }

    @Override
    public double zincrby(String key, double increment, byte[] member) {
        return doSync(asyncZincrby(key, increment, member), "zincrby");
    }

    @Override
    public <T> double zincrby(String key, double increment, T member,
            Transcoder<T> transcoder) {
        return doSync(asyncZincrby(key, increment, member, transcoder),
                "zincrby");
    }

    @Override
    public long zinterstore(String destination, String... keys) {
        return doSync(asyncZinterstore(destination, keys), "zinterstore");
    }

    @Override
    public long zinterstore(String destination, Iterator<String> keys) {
        return doSync(asyncZinterstore(destination, keys), "zinterstore");
    }

    @Override
    public long zinterstore(String destination, Iterable<String> keys) {
        return doSync(asyncZinterstore(destination, keys), "zinterstore");
    }

    @Override
    public long zinterstore(String destination, Aggregation aggregate,
            String... keys) {
        return doSync(asyncZinterstore(destination, aggregate, keys),
                "zinterstore");
    }

    @Override
    public long zinterstore(String destination, Iterator<String> keys,
            Aggregation aggregate) {
        return doSync(asyncZinterstore(destination, keys, aggregate),
                "zinterstore");
    }

    @Override
    public long zinterstore(String destination, Iterable<String> keys,
            Aggregation aggregate) {
        return doSync(asyncZinterstore(destination, keys, aggregate),
                "zinterstore");
    }

    @Override
    public long zinterstoreWeights(String destination, KeyWeight... keyweights) {
        return doSync(asyncZinterstoreWeights(destination, keyweights),
                "zinterstore");
    }

    @Override
    public long zinterstoreWeights(String destination,
            Iterator<KeyWeight> keyweights) {
        return doSync(asyncZinterstoreWeights(destination, keyweights),
                "zinterstore");
    }

    @Override
    public long zinterstoreWeights(String destination,
            Iterable<KeyWeight> keyweights) {
        return doSync(asyncZinterstoreWeights(destination, keyweights),
                "zinterstore");
    }

    @Override
    public long zinterstoreWeights(String destination, Aggregation aggregate,
            KeyWeight... keyweights) {
        return doSync(
                asyncZinterstoreWeights(destination, aggregate, keyweights),
                "zinterstore");
    }

    @Override
    public long zinterstoreWeights(String destination,
            Iterator<KeyWeight> keyweights, Aggregation aggregate) {
        return doSync(
                asyncZinterstoreWeights(destination, keyweights, aggregate),
                "zinterstore");
    }

    @Override
    public long zinterstoreWeights(String destination,
            Iterable<KeyWeight> keyweights, Aggregation aggregate) {
        return doSync(
                asyncZinterstoreWeights(destination, keyweights, aggregate),
                "zinterstore");
    }

    @Override
    public Set<String> zrange(String key, long start, long stop) {
        return doSync(asyncZrange(key, start, stop), "zrange");
    }

    @Override
    public Set<byte[]> zrangeAsBytes(String key, long start, long stop) {
        return doSync(asyncZrangeAsBytes(key, start, stop), "zrange");
    }

    @Override
    public <T> Set<T> zrange(String key, long start, long stop,
            Transcoder<T> transcoder) {
        return doSync(asyncZrange(key, start, stop, transcoder), "zrange");
    }

    @Override
    public Set<StringSortedSetEntry> zrangeWithScores(String key, long start,
            long stop) {
        return doSync(asyncZrangeWithScores(key, start, stop), "zrange");
    }

    @Override
    public Set<BytesSortedSetEntry> zrangeWithScoresAsBytes(String key,
            long start, long stop) {
        return doSync(asyncZrangeWithScoresAsBytes(key, start, stop), "zrange");
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Set<U> zrangeWithScores(String key,
            long start, long stop, Transcoder<T> transcoder) {
        Future<Set<U>> s = asyncZrangeWithScores(key, start, stop, transcoder);
        return doSync(s, "zrange");
    }

    @Override
    public Set<String> zrangebyscore(String key, double min, double max) {
        return doSync(asyncZrangebyscore(key, min, max), "zrangebyscore");
    }

    @Override
    public Set<String> zrangebyscore(String key, IntervalValue min,
            IntervalValue max) {
        return doSync(asyncZrangebyscore(key, min, max), "zrangebyscore");
    }

    @Override
    public Set<String> zrangebyscore(String key, double min, double max,
            long limit, long count) {
        return doSync(asyncZrangebyscore(key, min, max, limit, count),
                "zrangebyscore");
    }

    @Override
    public Set<String> zrangebyscore(String key, IntervalValue min,
            IntervalValue max, long limit, long count) {
        return doSync(asyncZrangebyscore(key, min, max, limit, count),
                "zrangebyscore");
    }

    @Override
    public Set<byte[]> zrangebyscoreAsBytes(String key, double min, double max) {
        return doSync(asyncZrangebyscoreAsBytes(key, min, max), "zrangebyscore");
    }

    @Override
    public Set<byte[]> zrangebyscoreAsBytes(String key, IntervalValue min,
            IntervalValue max) {
        return doSync(asyncZrangebyscoreAsBytes(key, min, max), "zrangebyscore");
    }

    @Override
    public Set<byte[]> zrangebyscoreAsBytes(String key, double min, double max,
            long limit, long count) {
        return doSync(asyncZrangebyscoreAsBytes(key, min, max, limit, count),
                "zrangebyscore");
    }

    @Override
    public Set<byte[]> zrangebyscoreAsBytes(String key, IntervalValue min,
            IntervalValue max, long limit, long count) {
        return doSync(asyncZrangebyscoreAsBytes(key, min, max, limit, count),
                "zrangebyscore");
    }

    @Override
    public <T> Set<T> zrangebyscore(String key, double min, double max,
            Transcoder<T> transcoder) {
        return doSync(asyncZrangebyscore(key, min, max, transcoder),
                "zrangebyscore");
    }

    @Override
    public <T> Set<T> zrangebyscore(String key, IntervalValue min,
            IntervalValue max, Transcoder<T> transcoder) {
        return doSync(asyncZrangebyscore(key, min, max, transcoder),
                "zrangebyscore");
    }

    @Override
    public <T> Set<T> zrangebyscore(String key, double min, double max,
            long limit, long count, Transcoder<T> transcoder) {
        return doSync(
                asyncZrangebyscore(key, min, max, limit, count, transcoder),
                "zrangebyscore");
    }

    @Override
    public <T> Set<T>
            zrangebyscore(String key, IntervalValue min, IntervalValue max,
                    long limit, long count, Transcoder<T> transcoder) {
        return doSync(
                asyncZrangebyscore(key, min, max, limit, count, transcoder),
                "zrangebyscore");
    }

    @Override
    public Set<StringSortedSetEntry> zrangebyscoreWithScores(String key,
            double min, double max) {
        return doSync(asyncZrangebyscoreWithScores(key, min, max),
                "zrangebyscore");
    }

    @Override
    public Set<StringSortedSetEntry> zrangebyscoreWithScores(String key,
            IntervalValue min, IntervalValue max) {
        return doSync(asyncZrangebyscoreWithScores(key, min, max),
                "zrangebyscore");
    }

    @Override
    public Set<StringSortedSetEntry> zrangebyscoreWithScores(String key,
            double min, double max, long limit, long count) {
        return doSync(
                asyncZrangebyscoreWithScores(key, min, max, limit, count),
                "zrangebyscore");
    }

    @Override
    public Set<StringSortedSetEntry> zrangebyscoreWithScores(String key,
            IntervalValue min, IntervalValue max, long limit, long count) {
        return doSync(
                asyncZrangebyscoreWithScores(key, min, max, limit, count),
                "zrangebyscore");
    }

    @Override
    public Set<BytesSortedSetEntry> zrangebyscoreWithScoresAsBytes(String key,
            double min, double max) {
        return doSync(asyncZrangebyscoreWithScoresAsBytes(key, min, max),
                "zrangebyscore");
    }

    @Override
    public Set<BytesSortedSetEntry> zrangebyscoreWithScoresAsBytes(String key,
            IntervalValue min, IntervalValue max) {
        return doSync(asyncZrangebyscoreWithScoresAsBytes(key, min, max),
                "zrangebyscore");
    }

    @Override
    public Set<BytesSortedSetEntry> zrangebyscoreWithScoresAsBytes(String key,
            double min, double max, long limit, long count) {
        return doSync(
                asyncZrangebyscoreWithScoresAsBytes(key, min, max, limit, count),
                "zrangebyscore");
    }

    @Override
    public Set<BytesSortedSetEntry> zrangebyscoreWithScoresAsBytes(String key,
            IntervalValue min, IntervalValue max, long limit, long count) {
        return doSync(
                asyncZrangebyscoreWithScoresAsBytes(key, min, max, limit, count),
                "zrangebyscore");
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Set<U> zrangebyscoreWithScores(
            String key, double min, double max, Transcoder<T> transcoder) {
        Future<Set<U>> s =
                asyncZrangebyscoreWithScores(key, min, max, transcoder);
        return doSync(s, "zrangebyscore");
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Set<U> zrangebyscoreWithScores(
            String key, IntervalValue min, IntervalValue max,
            Transcoder<T> transcoder) {
        Future<Set<U>> s =
                asyncZrangebyscoreWithScores(key, min, max, transcoder);
        return doSync(s, "zrangebyscore");
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Set<U> zrangebyscoreWithScores(
            String key, double min, double max, long limit, long count,
            Transcoder<T> transcoder) {
        Future<Set<U>> s =
                asyncZrangebyscoreWithScores(key, min, max, limit, count,
                        transcoder);
        return doSync(s, "zrangebyscore");
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Set<U> zrangebyscoreWithScores(
            String key, IntervalValue min, IntervalValue max, long limit,
            long count, Transcoder<T> transcoder) {
        Future<Set<U>> s =
                asyncZrangebyscoreWithScores(key, min, max, limit, count,
                        transcoder);
        return doSync(s, "zrangebyscore");
    }

    @Override
    public Long zrank(String key, String member) {
        return doSync(asyncZrank(key, member), "zrank");
    }

    @Override
    public Long zrank(String key, byte[] member) {
        return doSync(asyncZrank(key, member), "zrank");
    }

    @Override
    public <T> Long zrank(String key, T member, Transcoder<T> transcoder) {
        return doSync(asyncZrank(key, member, transcoder), "zrank");
    }

    @Override
    public long zrem(String key, String... members) {
        return doSync(asyncZrem(key, members), "zrem");
    }

    @Override
    public long zrem(String key, Iterator<String> members) {
        return doSync(asyncZrem(key, members), "zrem");
    }

    @Override
    public long zrem(String key, Iterable<String> members) {
        return doSync(asyncZrem(key, members), "zrem");
    }

    @Override
    public long zremAsBytes(String key, byte[]... members) {
        return doSync(asyncZremAsBytes(key, members), "zrem");
    }

    @Override
    public long zremAsBytes(String key, Iterator<byte[]> members) {
        return doSync(asyncZremAsBytes(key, members), "zrem");
    }

    @Override
    public long zremAsBytes(String key, Iterable<byte[]> members) {
        return doSync(asyncZremAsBytes(key, members), "zrem");
    }

    @Override
    public <T> long zrem(String key, Transcoder<T> transcoder, T... members) {
        return doSync(asyncZrem(key, transcoder, members), "zrem");
    }

    @Override
    public <T> long zrem(String key, Iterator<T> members,
            Transcoder<T> transcoder) {
        return doSync(asyncZrem(key, members, transcoder), "zrem");
    }

    @Override
    public <T> long zrem(String key, Iterable<T> members,
            Transcoder<T> transcoder) {
        return doSync(asyncZrem(key, members, transcoder), "zrem");
    }

    @Override
    public long zremrangebyrank(String key, long start, long stop) {
        return doSync(asyncZremrangebyrank(key, start, stop), "zremrangebyrank");
    }

    @Override
    public long zremrangebyscore(String key, double min, double max) {
        return doSync(asyncZremrangebyscore(key, min, max), "zremrangebyscore");
    }

    @Override
    public Set<String> zrevrange(String key, long start, long stop) {
        return doSync(asyncZrevrange(key, start, stop), "zrevrange");
    }

    @Override
    public Set<byte[]> zrevrangeAsBytes(String key, long start, long stop) {
        return doSync(asyncZrevrangeAsBytes(key, start, stop), "zrevrange");
    }

    @Override
    public <T> Set<T> zrevrange(String key, long start, long stop,
            Transcoder<T> transcoder) {
        return doSync(asyncZrevrange(key, start, stop, transcoder), "zrevrange");
    }

    @Override
    public Set<StringSortedSetEntry> zrevrangeWithScores(String key,
            long start, long stop) {
        return doSync(asyncZrevrangeWithScores(key, start, stop), "zrevrange");
    }

    @Override
    public Set<BytesSortedSetEntry> zrevrangeWithScoresAsBytes(String key,
            long start, long stop) {
        return doSync(asyncZrevrangeWithScoresAsBytes(key, start, stop),
                "zrevrange");
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Set<U> zrevrangeWithScores(
            String key, long start, long stop, Transcoder<T> transcoder) {
        Future<Set<U>> s =
                asyncZrevrangeWithScores(key, start, stop, transcoder);
        return doSync(s, "zrevrange");
    }

    @Override
    public Set<String> zrevrangebyscore(String key, double min, double max) {
        return doSync(asyncZrevrangebyscore(key, min, max), "zrevrangebyscore");
    }

    @Override
    public Set<String> zrevrangebyscore(String key, IntervalValue min,
            IntervalValue max) {
        return doSync(asyncZrevrangebyscore(key, min, max), "zrevrangebyscore");
    }

    @Override
    public Set<String> zrevrangebyscore(String key, double min, double max,
            long limit, long count) {
        return doSync(asyncZrevrangebyscore(key, min, max, limit, count),
                "zrevrangebyscore");
    }

    @Override
    public Set<String> zrevrangebyscore(String key, IntervalValue min,
            IntervalValue max, long limit, long count) {
        return doSync(asyncZrevrangebyscore(key, min, max, limit, count),
                "zrevrangebyscore");
    }

    @Override
    public Set<byte[]> zrevrangebyscoreAsBytes(String key, double min,
            double max) {
        return doSync(asyncZrevrangebyscoreAsBytes(key, min, max),
                "zrevrangebyscore");
    }

    @Override
    public Set<byte[]> zrevrangebyscoreAsBytes(String key, IntervalValue min,
            IntervalValue max) {
        return doSync(asyncZrevrangebyscoreAsBytes(key, min, max),
                "zrevrangebyscore");
    }

    @Override
    public Set<byte[]> zrevrangebyscoreAsBytes(String key, double min,
            double max, long limit, long count) {
        return doSync(
                asyncZrevrangebyscoreAsBytes(key, min, max, limit, count),
                "zrevrangebyscore");
    }

    @Override
    public Set<byte[]> zrevrangebyscoreAsBytes(String key, IntervalValue min,
            IntervalValue max, long limit, long count) {
        return doSync(
                asyncZrevrangebyscoreAsBytes(key, min, max, limit, count),
                "zrevrangebyscore");
    }

    @Override
    public <T> Set<T> zrevrangebyscore(String key, double min, double max,
            Transcoder<T> transcoder) {
        return doSync(asyncZrevrangebyscore(key, min, max, transcoder),
                "zrevrangebyscore");
    }

    @Override
    public <T> Set<T> zrevrangebyscore(String key, IntervalValue min,
            IntervalValue max, Transcoder<T> transcoder) {
        return doSync(asyncZrevrangebyscore(key, min, max, transcoder),
                "zrevrangebyscore");
    }

    @Override
    public <T> Set<T> zrevrangebyscore(String key, double min, double max,
            long limit, long count, Transcoder<T> transcoder) {
        return doSync(
                asyncZrevrangebyscore(key, min, max, limit, count, transcoder),
                "zrevrangebyscore");
    }

    @Override
    public <T> Set<T>
            zrevrangebyscore(String key, IntervalValue min, IntervalValue max,
                    long limit, long count, Transcoder<T> transcoder) {
        return doSync(
                asyncZrevrangebyscore(key, min, max, limit, count, transcoder),
                "zrevrangebyscore");
    }

    @Override
    public Set<StringSortedSetEntry> zrevrangebyscoreWithScores(String key,
            double min, double max) {
        return doSync(asyncZrevrangebyscoreWithScores(key, min, max),
                "zrevrangebyscore");
    }

    @Override
    public Set<StringSortedSetEntry> zrevrangebyscoreWithScores(String key,
            IntervalValue min, IntervalValue max) {
        return doSync(asyncZrevrangebyscoreWithScores(key, min, max),
                "zrevrangebyscore");
    }

    @Override
    public Set<StringSortedSetEntry> zrevrangebyscoreWithScores(String key,
            double min, double max, long limit, long count) {
        return doSync(
                asyncZrevrangebyscoreWithScores(key, min, max, limit, count),
                "zrevrangebyscore");
    }

    @Override
    public Set<StringSortedSetEntry> zrevrangebyscoreWithScores(String key,
            IntervalValue min, IntervalValue max, long limit, long count) {
        return doSync(
                asyncZrevrangebyscoreWithScores(key, min, max, limit, count),
                "zrevrangebyscore");
    }

    @Override
    public Set<BytesSortedSetEntry> zrevrangebyscoreWithScoresAsBytes(
            String key, double min, double max) {
        return doSync(asyncZrevrangebyscoreWithScoresAsBytes(key, min, max),
                "zrevrangebyscore");
    }

    @Override
    public Set<BytesSortedSetEntry> zrevrangebyscoreWithScoresAsBytes(
            String key, IntervalValue min, IntervalValue max) {
        return doSync(asyncZrevrangebyscoreWithScoresAsBytes(key, min, max),
                "zrevrangebyscore");
    }

    @Override
    public Set<BytesSortedSetEntry> zrevrangebyscoreWithScoresAsBytes(
            String key, double min, double max, long limit, long count) {
        return doSync(
                asyncZrevrangebyscoreWithScoresAsBytes(key, min, max, limit,
                        count), "zrevrangebyscore");
    }

    @Override
    public Set<BytesSortedSetEntry> zrevrangebyscoreWithScoresAsBytes(
            String key, IntervalValue min, IntervalValue max, long limit,
            long count) {
        return doSync(
                asyncZrevrangebyscoreWithScoresAsBytes(key, min, max, limit,
                        count), "zrevrangebyscore");
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Set<U> zrevrangebyscoreWithScores(
            String key, double min, double max, Transcoder<T> transcoder) {
        Future<Set<U>> s =
                asyncZrevrangebyscoreWithScores(key, min, max, transcoder);
        return doSync(s, "zrevrangebyscore");
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Set<U> zrevrangebyscoreWithScores(
            String key, IntervalValue min, IntervalValue max,
            Transcoder<T> transcoder) {
        Future<Set<U>> s =
                asyncZrevrangebyscoreWithScores(key, min, max, transcoder);
        return doSync(s, "zrevrangebyscore");
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Set<U> zrevrangebyscoreWithScores(
            String key, double min, double max, long limit, long count,
            Transcoder<T> transcoder) {
        Future<Set<U>> s =
                asyncZrevrangebyscoreWithScores(key, min, max, limit, count,
                        transcoder);
        return doSync(s, "zrevrangebyscore");
    }

    @Override
    public <T, U extends SortedSetEntry<T>> Set<U> zrevrangebyscoreWithScores(
            String key, IntervalValue min, IntervalValue max, long limit,
            long count, Transcoder<T> transcoder) {
        Future<Set<U>> s =
                asyncZrevrangebyscoreWithScores(key, min, max, limit, count,
                        transcoder);
        return doSync(s, "zrevrangebyscore");
    }

    @Override
    public Long zrevrank(String key, String member) {
        return doSync(asyncZrevrank(key, member), "zrevrank");
    }

    @Override
    public Long zrevrank(String key, byte[] member) {
        return doSync(asyncZrevrank(key, member), "zrevrank");
    }

    @Override
    public <T> Long zrevrank(String key, T member, Transcoder<T> transcoder) {
        return doSync(asyncZrevrank(key, member, transcoder), "zrevrank");
    }

    @Override
    public Double zscore(String key, String member) {
        return doSync(asyncZscore(key, member), "zscore");
    }

    @Override
    public Double zscore(String key, byte[] member) {
        return doSync(asyncZscore(key, member), "zscore");
    }

    @Override
    public <T> Double zscore(String key, T member, Transcoder<T> transcoder) {
        return doSync(asyncZscore(key, member, transcoder), "zscore");
    }

    @Override
    public long zunionstore(String destination, String... keys) {
        return doSync(asyncZunionstore(destination, keys), "zunionstore");
    }

    @Override
    public long zunionstore(String destination, Iterator<String> keys) {
        return doSync(asyncZunionstore(destination, keys), "zunionstore");
    }

    @Override
    public long zunionstore(String destination, Iterable<String> keys) {
        return doSync(asyncZunionstore(destination, keys), "zunionstore");
    }

    @Override
    public long zunionstore(String destination, Aggregation aggregate,
            String... keys) {
        return doSync(asyncZunionstore(destination, aggregate, keys),
                "zunionstore");
    }

    @Override
    public long zunionstore(String destination, Iterator<String> keys,
            Aggregation aggregate) {
        return doSync(asyncZunionstore(destination, keys, aggregate),
                "zunionstore");
    }

    @Override
    public long zunionstore(String destination, Iterable<String> keys,
            Aggregation aggregate) {
        return doSync(asyncZunionstore(destination, keys, aggregate),
                "zunionstore");
    }

    @Override
    public long zunionstoreWeights(String destination, KeyWeight... keyweights) {
        return doSync(asyncZunionstoreWeights(destination, keyweights),
                "zunionstore");
    }

    @Override
    public long zunionstoreWeights(String destination,
            Iterator<KeyWeight> keyweights) {
        return doSync(asyncZunionstoreWeights(destination, keyweights),
                "zunionstore");
    }

    @Override
    public long zunionstoreWeights(String destination,
            Iterable<KeyWeight> keyweights) {
        return doSync(asyncZunionstoreWeights(destination, keyweights),
                "zunionstore");
    }

    @Override
    public long zunionstoreWeights(String destination, Aggregation aggregate,
            KeyWeight... keyweights) {
        return doSync(
                asyncZunionstoreWeights(destination, aggregate, keyweights),
                "zunionstore");
    }

    @Override
    public long zunionstoreWeights(String destination,
            Iterator<KeyWeight> keyweights, Aggregation aggregate) {
        return doSync(
                asyncZunionstoreWeights(destination, keyweights, aggregate),
                "zunionstore");
    }

    @Override
    public long zunionstoreWeights(String destination,
            Iterable<KeyWeight> keyweights, Aggregation aggregate) {
        return doSync(
                asyncZunionstoreWeights(destination, keyweights, aggregate),
                "zunionstore");
    }

    @Override
    public Map<SocketAddress, String> ping() {
        final Map<SocketAddress, String> rv =
                new HashMap<SocketAddress, String>();
        CountDownLatch blatch = broadcastOp(new BroadcastOpFactory() {

            @Override
            public Operation
                    newOp(final RedisNode n, final CountDownLatch latch) {
                final SocketAddress sa = n.getSocketAddress();
                return opFact.ping(new StringReplyCallback() {

                    @Override
                    public void receivedStatus(OperationStatus status) {
                        if (!status.isSuccess()) {
                            getLogger().warn("Unsuccessful stat fetch: %s",
                                    status);
                        }
                    }

                    @Override
                    public void complete() {
                        latch.countDown();
                    }

                    @Override
                    public void onReply(String pong) {
                        rv.put(sa, pong);
                    }
                });
            }
        });
        try {
            blatch.await(operationTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted waiting for stats", e);
        }
        return rv;
    }

    @Override
    public RedisPipelineIF pipeline() {
        return new RedisPipeline(connFactory, rconn);
    }

    @Override
    protected <T> Future<T> addOperation(String key, OpFuture<T> opFuture) {
        rconn.enqueueOperation(key, opFuture.op);
        return opFuture.future;
    }

    @Override
    protected <T> Future<T> addWriteOperation(OpFuture<T> opFuture) {
        rconn.addWriteOperation(opFuture.op);
        return opFuture.future;
    }

    protected <T> T doSync(Future<T> future, String api) {
        return doSync(future, api, operationTimeout);
    }

    protected <T> T doSyncTimeout(Future<T> future, String api, int timeoutSec) {
        return doSync(future, api, timeoutSec == 0 ? Long.MAX_VALUE
                : timeoutSec * 1000 + 20);
    }

    protected <T> T doSync(Future<T> future, String api, long timeout) {
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted waiting for " + api, e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof CancellationException) {
                throw (CancellationException) cause;
            } else if (cause instanceof RedisException) {
                throw (RedisException) cause;
            } else {
                throw new RuntimeException(cause);
            }
        } catch (TimeoutException e) {
            throw new OperationTimeoutException("Timeout waiting for "
                    + api
                    + ": "
                    + buildTimeoutMessage(operationTimeout,
                            TimeUnit.MILLISECONDS), e);
        }
    }

    public CountDownLatch broadcastOp(final BroadcastOpFactory of) {
        return broadcastOp(of, rconn.getLocator().getAll(), true);
    }

    public CountDownLatch broadcastOp(final BroadcastOpFactory of,
            Collection<RedisNode> nodes) {
        return broadcastOp(of, nodes, true);
    }

    private CountDownLatch broadcastOp(BroadcastOpFactory of,
            Collection<RedisNode> nodes, boolean checkShuttingDown) {
        if (checkShuttingDown && shuttingDown) {
            throw new IllegalStateException("Shutting down");
        }
        return rconn.broadcastOperation(of, nodes);
    }

    /**
     * Shut down immediately.
     */
    @Override
    public void shutdown() {
        shutdown(-1, TimeUnit.MILLISECONDS);
    }

    /**
     * Shut down this client gracefully.
     *
     * @param timeout
     *            the amount of time time for shutdown
     * @param unit
     *            the TimeUnit for the timeout
     * @return result of the shutdown request
     */
    @Override
    public boolean shutdown(long timeout, TimeUnit unit) {
        // Guard against double shutdowns (bug 8).
        if (shuttingDown) {
            getLogger().info("Suppressing duplicate attempt to shut down");
            return false;
        }
        shuttingDown = true;
        String baseName = rconn.getName();
        rconn.setName(baseName + " - SHUTTING DOWN");
        boolean rv = true;
        try {
            // Conditionally wait
            if (timeout > 0) {
                rconn.setName(baseName + " - SHUTTING DOWN (waiting)");
                rv = waitForQueues(timeout, unit);
            }
        } finally {
            // But always begin the shutdown sequence
            try {
                rconn.setName(baseName + " - SHUTTING DOWN (telling client)");
                rconn.shutdown();
                rconn.setName(baseName + " - SHUTTING DOWN (informed client)");
                tcService.shutdown();
            } catch (IOException e) {
                getLogger().warn("exception while shutting down", e);
            }
        }
        return rv;
    }

    /**
     * Wait for the queues to die down.
     *
     * @param timeout
     *            the amount of time time for shutdown
     * @param unit
     *            the TimeUnit for the timeout
     * @return result of the request for the wait
     * @throws IllegalStateException
     *             in the rare circumstance where queue is too full to accept
     *             any more requests
     */
    public boolean waitForQueues(long timeout, TimeUnit unit) {
        CountDownLatch blatch = broadcastOp(new BroadcastOpFactory() {
            @Override
            public Operation
                    newOp(final RedisNode n, final CountDownLatch latch) {
                return opFact.ping(new StringReplyCallback() {

                    @Override
                    public void receivedStatus(OperationStatus status) {

                    }

                    @Override
                    public void complete() {
                        latch.countDown();
                    }

                    @Override
                    public void onReply(String pong) {
                        // Nothing special when receiving status, only
                        // necessary to complete the interface
                    }
                });
            }
        }, rconn.getLocator().getAll(), false);
        try {
            // XXX: Perhaps IllegalStateException should be caught here
            // and the check retried.
            return blatch.await(timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted waiting for queues", e);
        }
    }

    public static void main(String[] args) throws Exception {
        Logger root = Logger.getRootLogger();
        if (!root.getAllAppenders().hasMoreElements()) {
            root.addAppender(new ConsoleAppender(new PatternLayout(
                    "%p\t%d{ISO8601}\t%t\t%c\t- %m%n")));
        }
        root.setLevel(Level.DEBUG);

        InetSocketAddress sa = new InetSocketAddress("127.0.0.1", 6379);
        InetSocketAddress sa1 = new InetSocketAddress("127.0.0.1", 6380);
        RedisClientIF r = new RedisClient(sa, sa1);
        try {
            RedisPipelineIF p = r.pipeline();
            Future<List<String>> f1 = p.asyncMget("moo", "moo1");
            Future<Void> f2 = p.asyncSet("moo2", "moo2");
            System.out.println(f1.get());
            System.out.println(f2.get());
        } catch (RedisException e) {
            System.out.println(e);
        } finally {
            r.shutdown(1, TimeUnit.SECONDS);
        }
    }
}
