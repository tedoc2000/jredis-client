package com.zibobo.yedis;

import java.util.Iterator;
import java.util.List;

import com.zibobo.yedis.transcoder.Transcoder;

public interface SyncListsOperation {
    /* blpop */
    public BlockingPopResult<String> blpop(int timeout, String... keys);

    public BlockingPopResult<String> blpop(Iterator<String> keys, int timeout);

    public BlockingPopResult<String> blpop(Iterable<String> keys, int timeout);

    public BlockingPopResult<byte[]> blpopAsBytes(int timeout, String... keys);

    public BlockingPopResult<byte[]> blpopAsBytes(Iterator<String> keys,
            int timeout);

    public BlockingPopResult<byte[]> blpopAsBytes(Iterable<String> keys,
            int timeout);

    public <T> BlockingPopResult<T> blpop(int timeout,
            Transcoder<T> transcoder, String... keys);

    public <T> BlockingPopResult<T> blpop(Iterator<String> keys, int timeout,
            Transcoder<T> transcoder);

    public <T> BlockingPopResult<T> blpop(Iterable<String> keys, int timeout,
            Transcoder<T> transcoder);

    /* brpop */
    public BlockingPopResult<String> brpop(int timeout, String... keys);

    public BlockingPopResult<String> brpop(Iterator<String> keys, int timeout);

    public BlockingPopResult<String> brpop(Iterable<String> keys, int timeout);

    public BlockingPopResult<byte[]> brpopAsBytes(int timeout, String... keys);

    public BlockingPopResult<byte[]> brpopAsBytes(Iterator<String> keys,
            int timeout);

    public BlockingPopResult<byte[]> brpopAsBytes(Iterable<String> keys,
            int timeout);

    public <T> BlockingPopResult<T> brpop(int timeout,
            Transcoder<T> transcoder, String... keys);

    public <T> BlockingPopResult<T> brpop(Iterator<String> keys, int timeout,
            Transcoder<T> transcoder);

    public <T> BlockingPopResult<T> brpop(Iterable<String> keys, int timeout,
            Transcoder<T> transcoder);

    /* brpoplpush */
    public String brpoplpush(String source, String destination, int timeout);

    public byte[] brpoplpushAsBytes(String source, String destination,
            int timeout);

    public <T> T brpoplpush(String source, String destination, int timeout,
            Transcoder<T> transcoder);

    /* lindex */
    public String lindex(String key, long index);

    public byte[] lindexAsBytes(String key, long index);

    public <T> T lindex(String key, long index, Transcoder<T> transcoder);

    /* linsert */

    public long linsertBefore(String key, String pivot, String value);

    public long linsertBefore(String key, byte[] pivot, byte[] value);

    public <T> long linsertBefore(String key, T pivot, T value,
            Transcoder<T> transcoder);

    public <P, V> long linsertBefore(String key, P pivot,
            Transcoder<P> pivotTranscoder, V value,
            Transcoder<V> valueTranscoder);

    public long linsertAfter(String key, String pivot, String value);

    public long linsertAfter(String key, byte[] pivot, byte[] value);

    public <T> long linsertAfter(String key, T pivot, T value,
            Transcoder<T> transcoder);

    public <P, V> long linsertAfter(String key, P pivot,
            Transcoder<P> pivotTranscoder, V value,
            Transcoder<V> valueTranscoder);

    /* llen */
    public long llen(String key);

    /* lpop */
    public String lpop(String key);

    public byte[] lpopAsBytes(String key);

    public <T> T lpop(String key, Transcoder<T> transcoder);

    /* lpush */
    public long lpush(String key, String... values);

    public long lpush(String key, Iterator<String> values);

    public long lpush(String key, Iterable<String> values);

    public long lpushAsBytes(String key, byte[]... values);

    public long lpushAsBytes(String key, Iterator<byte[]> values);

    public long lpushAsBytes(String key, Iterable<byte[]> values);

    public <T> long lpush(String key, Transcoder<T> transcoder, T... values);

    public <T> long lpush(String key, Iterator<T> values,
            Transcoder<T> transcoder);

    public <T> long lpush(String key, Iterable<T> values,
            Transcoder<T> transcoder);

    /* lpushx */
    public long lpushx(String key, String value);

    public long lpushx(String key, byte[] value);

    public <T> long lpushx(String key, T value, Transcoder<T> transcoder);

    /* lrange */
    public List<String> lrange(String key, long start, long stop);

    public List<byte[]> lrangeAsBytes(String key, long start, long stop);

    public <T> List<T> lrange(String key, long start, long stop,
            Transcoder<T> transcoder);

    /* lrem */
    public long lrem(String key, long count, String value);

    public long lrem(String key, long count, byte[] value);

    public <T> long lrem(String key, long count, T value,
            Transcoder<T> transcoder);

    /* lset */
    public void lset(String key, long index, String value);

    public void lset(String key, long index, byte[] value);

    public <T> void lset(String key, long index, T value,
            Transcoder<T> transcoder);

    /* ltrim */
    public void ltrim(String key, long start, long stop);

    /* rpop */
    public String rpop(String key);

    public byte[] rpopAsBytes(String key);

    public <T> T rpop(String key, Transcoder<T> transcoder);

    /* rpoplpush */
    public String rpoplpush(String source, String destination);

    public byte[] rpoplpushAsBytes(String source, String destination);

    public <T> T rpoplpush(String source, String destination,
            Transcoder<T> transcoder);

    /* rpush */
    public long rpush(String key, String... values);

    public long rpush(String key, Iterator<String> values);

    public long rpush(String key, Iterable<String> values);

    public long rpushAsBytes(String key, byte[]... values);

    public long rpushAsBytes(String key, Iterator<byte[]> values);

    public long rpushAsBytes(String key, Iterable<byte[]> values);

    public <T> long rpush(String key, Transcoder<T> transcoder, T... values);

    public <T> long rpush(String key, Iterator<T> values,
            Transcoder<T> transcoder);

    public <T> long rpush(String key, Iterable<T> values,
            Transcoder<T> transcoder);

    /* rpushx */
    public long rpushx(String key, String value);

    public long rpushx(String key, byte[] value);

    public <T> long rpushx(String key, T value, Transcoder<T> transcoder);
}
