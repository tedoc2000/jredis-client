package com.zibobo.yedis;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Future;

import com.zibobo.yedis.transcoder.Transcoder;

public interface AsyncSetsOperations {

    /* sadd */
    public Future<Long> asyncSadd(String key, String... values);

    public Future<Long> asyncSadd(String key, Iterator<String> values);

    public Future<Long> asyncSadd(String key, Iterable<String> values);

    public Future<Long> asyncSaddAsBytes(String key, byte[]... values);

    public Future<Long> asyncSaddAsBytes(String key, Iterator<byte[]> values);

    public Future<Long> asyncSaddAsBytes(String key, Iterable<byte[]> values);

    public <T> Future<Long> asyncSadd(String key, Transcoder<T> transcoder,
            T... values);

    public <T> Future<Long> asyncSadd(String key, Iterator<T> values,
            Transcoder<T> transcoder);

    public <T> Future<Long> asyncSadd(String key, Iterable<T> values,
            Transcoder<T> transcoder);

    /* scard */
    public Future<Long> asyncScard(String key);

    /* sdiff */
    public Future<Set<String>> asyncSdiff(String... keys);

    public Future<Set<String>> asyncSdiff(Iterator<String> keys);

    public Future<Set<String>> asyncSdiff(Iterable<String> keys);

    public Future<Set<byte[]>> asyncSdiffAsBytes(String... keys);

    public Future<Set<byte[]>> asyncSdiffAsBytes(Iterator<String> keys);

    public Future<Set<byte[]>> asyncSdiffAsBytes(Iterable<String> keys);

    public <T> Future<Set<T>> asyncSdiff(Transcoder<T> transcoder,
            String... keys);

    public <T> Future<Set<T>> asyncSdiff(Iterator<String> keys,
            Transcoder<T> transcoder);

    public <T> Future<Set<T>> asyncSdiff(Iterable<String> keys,
            Transcoder<T> transcoder);

    /* sdiffstore */
    public Future<Long> asyncSdiffstore(String destination, String... keys);

    public Future<Long> asyncSdiffstore(String destination,
            Iterator<String> keys);

    public Future<Long> asyncSdiffstore(String destination,
            Iterable<String> keys);

    /* sinter */
    public Future<Set<String>> asyncSinter(String... keys);

    public Future<Set<String>> asyncSinter(Iterator<String> keys);

    public Future<Set<String>> asyncSinter(Iterable<String> keys);

    public Future<Set<byte[]>> asyncSinterAsBytes(String... keys);

    public Future<Set<byte[]>> asyncSinterAsBytes(Iterator<String> keys);

    public Future<Set<byte[]>> asyncSinterAsBytes(Iterable<String> keys);

    public <T> Future<Set<T>> asyncSinter(Transcoder<T> transcoder,
            String... keys);

    public <T> Future<Set<T>> asyncSinter(Iterator<String> keys,
            Transcoder<T> transcoder);

    public <T> Future<Set<T>> asyncSinter(Iterable<String> keys,
            Transcoder<T> transcoder);

    /* sinterstore */
    public Future<Long> asyncSinterstore(String destination, String... keys);

    public Future<Long> asyncSinterstore(String destination,
            Iterator<String> keys);

    public Future<Long> asyncSinterstore(String destination,
            Iterable<String> keys);

    /* sismember */
    public Future<Boolean> asyncSismember(String key, String member);

    public Future<Boolean> asyncSismember(String key, byte[] member);

    public <T> Future<Boolean> asyncSismember(String key, T member,
            Transcoder<T> transcoder);

    /* smembers */
    public Future<Set<String>> asyncSmembers(String key);

    public Future<Set<byte[]>> asyncSmembersAsBytes(String key);

    public <T> Future<Set<T>>
            asyncSmembers(String key, Transcoder<T> transcoder);

    /* smove */
    public Future<Boolean> asyncSmove(String source, String destination,
            String value);

    public Future<Boolean> asyncSmove(String source, String destination,
            byte[] value);

    public <T> Future<Boolean> asyncSmove(String source, String destination,
            T value, Transcoder<T> transcoder);

    /* spop */
    public Future<String> asyncSpop(String key);

    public Future<byte[]> asyncSpopAsBytes(String key);

    public <T> Future<T> asyncSpop(String key, Transcoder<T> transcoder);

    /* srandmember */
    public Future<String> asyncSrandmember(String key);

    public Future<byte[]> asyncSrandmemberAsBytes(String key);

    public <T> Future<T> asyncSrandmember(String key, Transcoder<T> transcoder);

    public Future<Set<String>> asyncSrandmember(String key, long count);

    public Future<Set<byte[]>> asyncSrandmemberAsBytes(String key, long count);

    public <T> Future<Set<T>> asyncSrandmember(String key, long count,

    Transcoder<T> transcoder);

    /* srem */
    public Future<Long> asyncSrem(String key, String... values);

    public Future<Long> asyncSrem(String key, Iterator<String> values);

    public Future<Long> asyncSrem(String key, Iterable<String> values);

    public Future<Long> asyncSremAsBytes(String key, byte[]... values);

    public Future<Long> asyncSremAsBytes(String key, Iterator<byte[]> values);

    public Future<Long> asyncSremAsBytes(String key, Iterable<byte[]> values);

    public <T> Future<Long> asyncSrem(String key, Transcoder<T> transcoder,
            T... values);

    public <T> Future<Long> asyncSrem(String key, Iterator<T> values,
            Transcoder<T> transcoder);

    public <T> Future<Long> asyncSrem(String key, Iterable<T> values,
            Transcoder<T> transcoder);

    /* sunion */
    public Future<Set<String>> asyncSunion(String... keys);

    public Future<Set<String>> asyncSunion(Iterator<String> keys);

    public Future<Set<String>> asyncSunion(Iterable<String> keys);

    public Future<Set<byte[]>> asyncSunionAsBytes(String... keys);

    public Future<Set<byte[]>> asyncSunionAsBytes(Iterator<String> keys);

    public Future<Set<byte[]>> asyncSunionAsBytes(Iterable<String> keys);

    public <T> Future<Set<T>> asyncSunion(Transcoder<T> transcoder,
            String... keys);

    public <T> Future<Set<T>> asyncSunion(Iterator<String> keys,
            Transcoder<T> transcoder);

    public <T> Future<Set<T>> asyncSunion(Iterable<String> keys,
            Transcoder<T> transcoder);

    /* sunionstore */
    public Future<Long> asyncSunionstore(String destination, String... keys);

    public Future<Long> asyncSunionstore(String destination,
            Iterator<String> keys);

    public Future<Long> asyncSunionstore(String destination,
            Iterable<String> keys);

}
