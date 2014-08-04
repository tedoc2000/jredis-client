package com.zibobo.yedis;

import java.util.Iterator;
import java.util.Set;

import com.zibobo.yedis.transcoder.Transcoder;

public interface SyncSetsOperations {

    /* sadd */
    public long sadd(String key, String... values);

    public long sadd(String key, Iterator<String> values);

    public long sadd(String key, Iterable<String> values);

    public long saddAsBytes(String key, byte[]... values);

    public long saddAsBytes(String key, Iterator<byte[]> values);

    public long saddAsBytes(String key, Iterable<byte[]> values);

    public <T> long sadd(String key, Transcoder<T> transcoder, T... values);

    public <T> long sadd(String key, Iterator<T> values,
            Transcoder<T> transcoder);

    public <T> long sadd(String key, Iterable<T> values,
            Transcoder<T> transcoder);

    /* scard */
    public long scard(String key);

    /* sdiff */
    public Set<String> sdiff(String... keys);

    public Set<String> sdiff(Iterator<String> keys);

    public Set<String> sdiff(Iterable<String> keys);

    public Set<byte[]> sdiffAsBytes(String... keys);

    public Set<byte[]> sdiffAsBytes(Iterator<String> keys);

    public Set<byte[]> sdiffAsBytes(Iterable<String> keys);

    public <T> Set<T> sdiff(Transcoder<T> transcoder, String... keys);

    public <T> Set<T> sdiff(Iterator<String> keys, Transcoder<T> transcoder);

    /* sdiffstore */
    public long sdiffstore(String destination, String... keys);

    public long sdiffstore(String destination, Iterator<String> keys);

    public long sdiffstore(String destination, Iterable<String> keys);

    /* sinter */
    public Set<String> sinter(String... keys);

    public Set<String> sinter(Iterator<String> keys);

    public Set<String> sinter(Iterable<String> keys);

    public Set<byte[]> sinterAsBytes(String... keys);

    public Set<byte[]> sinterAsBytes(Iterator<String> keys);

    public Set<byte[]> sinterAsBytes(Iterable<String> keys);

    public <T> Set<T> sinter(Transcoder<T> transcoder, String... keys);

    public <T> Set<T> sinter(Iterator<String> keys, Transcoder<T> transcoder);

    /* sinterstore */
    public long sinterstore(String destination, String... keys);

    public long sinterstore(String destination, Iterator<String> keys);

    public long sinterstore(String destination, Iterable<String> keys);

    /* sismember */
    public boolean sismember(String key, String member);

    public boolean sismember(String key, byte[] member);

    public <T> boolean
            sismember(String key, T member, Transcoder<T> transcoder);

    /* smember */
    public Set<String> smembers(String key);

    public Set<byte[]> smembersAsBytes(String key);

    public <T> Set<T> smembers(String key, Transcoder<T> transcoder);

    /* smove */
    public boolean smove(String source, String destination, String value);

    public boolean smove(String source, String destination, byte[] value);

    public <T> boolean smove(String source, String destination, T value,
            Transcoder<T> transcoder);

    /* spop */
    public String spop(String key);

    public byte[] spopAsBytes(String key);

    public <T> T spop(String key, Transcoder<T> transcoder);

    /* srandmember */
    public String srandmember(String key);

    public byte[] srandmemberAsBytes(String key);

    public <T> T srandmember(String key, Transcoder<T> transcoder);

    public Set<String> srandmember(String key, long count);

    public Set<byte[]> srandmemberAsBytes(String key, long count);

    public <T> Set<T> srandmember(String key, long count,
            Transcoder<T> transcoder);

    /* srem */
    public long srem(String key, String... values);

    public long srem(String key, Iterator<String> values);

    public long srem(String key, Iterable<String> values);

    public long sremAsBytes(String key, byte[]... values);

    public long sremAsBytes(String key, Iterator<byte[]> values);

    public long sremAsBytes(String key, Iterable<byte[]> values);

    public <T> long srem(String key, Transcoder<T> transcoder, T... values);

    public <T> long srem(String key, Iterator<T> values,
            Transcoder<T> transcoder);

    public <T> long srem(String key, Iterable<T> values,
            Transcoder<T> transcoder);

    /* sunion */
    public Set<String> sunion(String... keys);

    public Set<String> sunion(Iterator<String> keys);

    public Set<String> sunion(Iterable<String> keys);

    public Set<byte[]> sunionAsBytes(String... keys);

    public Set<byte[]> sunionAsBytes(Iterator<String> keys);

    public Set<byte[]> sunionAsBytes(Iterable<String> keys);

    public <T> Set<T> sunion(Transcoder<T> transcoder, String... keys);

    public <T> Set<T> sunion(Iterator<String> keys, Transcoder<T> transcoder);

    /* sunionstore */
    public long sunionstore(String destination, String... keys);

    public long sunionstore(String destination, Iterator<String> keys);

    public long sunionstore(String destination, Iterable<String> keys);
}
