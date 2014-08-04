package com.zibobo.yedis;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import com.zibobo.yedis.transcoder.Transcoder;

public interface AsyncListsOperations {

    /* blpop */

    public Future<BlockingPopResult<String>> asyncBlpop(int timeout,
            String... keys);

    public Future<BlockingPopResult<String>> asyncBlpop(Iterator<String> keys,
            int timeout);

    public Future<BlockingPopResult<String>> asyncBlpop(Iterable<String> keys,
            int timeout);

    public Future<BlockingPopResult<byte[]>> asyncBlpopAsBytes(int timeout,
            String... keys);

    public Future<BlockingPopResult<byte[]>> asyncBlpopAsBytes(
            Iterator<String> keys, int timeout);

    public Future<BlockingPopResult<byte[]>> asyncBlpopAsBytes(
            Iterable<String> keys, int timeout);

    public <T> Future<BlockingPopResult<T>> asyncBlpop(int timeout,
            Transcoder<T> transcoder, String... keys);

    public <T> Future<BlockingPopResult<T>> asyncBlpop(Iterator<String> keys,
            int timeout, Transcoder<T> transcoder);

    public <T> Future<BlockingPopResult<T>> asyncBlpop(Iterable<String> keys,
            int timeout, Transcoder<T> transcoder);

    /* brpop */
    public Future<BlockingPopResult<String>> asyncBrpop(int timeout,
            String... keys);

    public Future<BlockingPopResult<String>> asyncBrpop(Iterator<String> keys,
            int timeout);

    public Future<BlockingPopResult<String>> asyncBrpop(Iterable<String> keys,
            int timeout);

    public Future<BlockingPopResult<byte[]>> asyncBrpopAsBytes(int timeout,
            String... keys);

    public Future<BlockingPopResult<byte[]>> asyncBrpopAsBytes(
            Iterator<String> keys, int timeout);

    public Future<BlockingPopResult<byte[]>> asyncBrpopAsBytes(
            Iterable<String> keys, int timeout);

    public <T> Future<BlockingPopResult<T>> asyncBrpop(int timeout,
            Transcoder<T> transcoder, String... keys);

    public <T> Future<BlockingPopResult<T>> asyncBrpop(Iterator<String> keys,
            int timeout, Transcoder<T> transcoder);

    public <T> Future<BlockingPopResult<T>> asyncBrpop(Iterable<String> keys,
            int timeout, Transcoder<T> transcoder);

    /* brpoplpush */

    public Future<String> asyncBrpoplpush(String source, String destination,
            int timeout);

    public Future<byte[]> asyncBrpoplpushAsBytes(String source,
            String destination, int timeout);

    public <T> Future<T> asyncBrpoplpush(String source, String destination,
            int timeout, Transcoder<T> transcoder);

    /* lindex */
    public Future<String> asyncLindex(String key, long index);

    public Future<byte[]> asyncLindexAsBytes(String key, long index);

    public <T> Future<T> asyncLindex(String key, long index,
            Transcoder<T> transcoder);

    /* linsert */

    public Future<Long> asyncLinsertBefore(String key, String pivot,
            String value);

    public Future<Long> asyncLinsertBefore(String key, byte[] pivot,
            byte[] value);

    public <T> Future<Long> asyncLinsertBefore(String key, T pivot, T value,
            Transcoder<T> transcoder);

    public <P, V> Future<Long> asyncLinsertBefore(String key, P pivot,
            Transcoder<P> pivotTranscoder, V value,
            Transcoder<V> valueTranscoder);

    public Future<Long>
            asyncLinsertAfter(String key, String pivot, String value);

    public Future<Long>
            asyncLinsertAfter(String key, byte[] pivot, byte[] value);

    public <T> Future<Long> asyncLinsertAfter(String key, T pivot, T value,
            Transcoder<T> transcoder);

    public <P, V> Future<Long> asyncLinsertAfter(String key, P pivot,
            Transcoder<P> pivotTranscoder, V value,
            Transcoder<V> valueTranscoder);

    /* llen */
    public Future<Long> asyncLlen(String key);

    /* lpop */

    public Future<String> asyncLpop(String key);

    public Future<byte[]> asyncLpopAsBytes(String key);

    public <T> Future<T> asyncLpop(String key, Transcoder<T> transcoder);

    /* lpush */

    public Future<Long> asyncLpush(String key, String... values);

    public Future<Long> asyncLpush(String key, Iterator<String> values);

    public Future<Long> asyncLpush(String key, Iterable<String> values);

    public Future<Long> asyncLpushAsBytes(String key, byte[]... values);

    public Future<Long> asyncLpushAsBytes(String key, Iterator<byte[]> values);

    public Future<Long> asyncLpushAsBytes(String key, Iterable<byte[]> values);

    public <T> Future<Long> asyncLpush(String key, Transcoder<T> transcoder,
            T... values);

    public <T> Future<Long> asyncLpush(String key, Iterator<T> values,
            Transcoder<T> transcoder);

    public <T> Future<Long> asyncLpush(String key, Iterable<T> values,
            Transcoder<T> transcoder);

    /* lpushx */

    public Future<Long> asyncLpushx(String key, String value);

    public Future<Long> asyncLpushx(String key, byte[] value);

    public <T> Future<Long> asyncLpushx(String key, T value,
            Transcoder<T> transcoder);

    /* lrange */
    public Future<List<String>> asyncLrange(String key, long start, long stop);

    public Future<List<byte[]>> asyncLrangeAsBytes(String key, long start,
            long stop);

    public <T> Future<List<T>> asyncLrange(String key, long start, long stop,
            Transcoder<T> transcoder);

    /* lrem */

    public Future<Long> asyncLrem(String key, long count, String value);

    public Future<Long> asyncLrem(String key, long count, byte[] value);

    public <T> Future<Long> asyncLrem(String key, long count, T value,
            Transcoder<T> transcoder);

    /* lset */

    public Future<Void> asyncLset(String key, long index, String value);

    public Future<Void> asyncLset(String key, long index, byte[] value);

    public <T> Future<Void> asyncLset(String key, long index, T value,
            Transcoder<T> transcoder);

    /* ltrim */

    public Future<Void> asyncLtrim(String key, long start, long stop);

    /* rpop */

    public Future<String> asyncRpop(String key);

    public Future<byte[]> asyncRpopAsBytes(String key);

    public <T> Future<T> asyncRpop(String key, Transcoder<T> transcoder);

    /* rpoplpush */

    public Future<String> asyncRpoplpush(String source, String destination);

    public Future<byte[]> asyncRpoplpushAsBytes(String source,
            String destination);

    public <T> Future<T> asyncRpoplpush(String source, String destination,
            Transcoder<T> transcoder);

    /* rpush */

    public Future<Long> asyncRpush(String key, String... values);

    public Future<Long> asyncRpush(String key, Iterator<String> values);

    public Future<Long> asyncRpush(String key, Iterable<String> values);

    public Future<Long> asyncRpushAsBytes(String key, byte[]... values);

    public Future<Long> asyncRpushAsBytes(String key, Iterator<byte[]> values);

    public Future<Long> asyncRpushAsBytes(String key, Iterable<byte[]> values);

    public <T> Future<Long> asyncRpush(String key, Transcoder<T> transcoder,
            T... values);

    public <T> Future<Long> asyncRpush(String key, Iterator<T> values,
            Transcoder<T> transcoder);

    public <T> Future<Long> asyncRpush(String key, Iterable<T> values,
            Transcoder<T> transcoder);

    /* rpushx */

    public Future<Long> asyncRpushx(String key, String value);

    public Future<Long> asyncRpushx(String key, byte[] value);

    public <T> Future<Long> asyncRpushx(String key, T value,
            Transcoder<T> transcoder);
}
