package com.zibobo.yedis;

import java.util.Iterator;
import java.util.Set;

import com.zibobo.yedis.transcoder.Transcoder;

public interface SyncSortedSetsOperations {

    /* zadd */
    public long zadd(String key, StringSortedSetEntry... entries);

    public long zadd(String key, Iterator<StringSortedSetEntry> entries);

    public long zadd(String key, Iterable<StringSortedSetEntry> entries);

    public long zaddAsBytes(String key, BytesSortedSetEntry... entries);

    public long zaddAsBytes(String key, Iterator<BytesSortedSetEntry> entries);

    public long zaddAsBytes(String key, Iterable<BytesSortedSetEntry> entries);

    public <T, U extends SortedSetEntry<T>> long zadd(String key,
            Transcoder<T> transcoder, U... entries);

    public <T, U extends SortedSetEntry<T>> long zadd(String key,
            Iterator<U> entries, Transcoder<T> transcoder);

    public <T, U extends SortedSetEntry<T>> long zadd(String key,
            Iterable<U> entries, Transcoder<T> transcoder);

    /* zcard */
    public long zcard(String key);

    /* zcount */
    public long zcount(String key, double min, double max);

    /* zincrby */
    public double zincrby(String key, double increment, String member);

    public double zincrby(String key, double increment, byte[] member);

    public <T> double zincrby(String key, double increment, T member,
            Transcoder<T> transcoder);

    /* zinterstore */
    public long zinterstore(String destination, String... keys);

    public long zinterstore(String destination, Iterator<String> keys);

    public long zinterstore(String destination, Iterable<String> keys);

    public long zinterstore(String destination, Aggregation aggregate,
            String... keys);

    public long zinterstore(String destination, Iterator<String> keys,
            Aggregation aggregate);

    public long zinterstore(String destination, Iterable<String> keys,
            Aggregation aggregate);

    public long zinterstoreWeights(String destination, KeyWeight... keyweights);

    public long zinterstoreWeights(String destination,
            Iterator<KeyWeight> keyweights);

    public long zinterstoreWeights(String destination,
            Iterable<KeyWeight> keyweights);

    public long zinterstoreWeights(String destination, Aggregation aggregate,
            KeyWeight... keyweights);

    public long zinterstoreWeights(String destination,
            Iterator<KeyWeight> keyweights, Aggregation aggregate);

    public long zinterstoreWeights(String destination,
            Iterable<KeyWeight> keyweights, Aggregation aggregate);

    /* zrange */
    public Set<String> zrange(String key, long start, long stop);

    public Set<byte[]> zrangeAsBytes(String key, long start, long stop);

    public <T> Set<T> zrange(String key, long start, long stop,
            Transcoder<T> transcoder);

    public Set<StringSortedSetEntry> zrangeWithScores(String key, long start,
            long stop);

    public Set<BytesSortedSetEntry> zrangeWithScoresAsBytes(String key,
            long start, long stop);

    public <T, U extends SortedSetEntry<T>> Set<U> zrangeWithScores(String key,
            long start, long stop, Transcoder<T> transcoder);

    /* zrangebyscore */
    public Set<String> zrangebyscore(String key, double min, double max);

    public Set<String> zrangebyscore(String key, IntervalValue min,
            IntervalValue max);

    public Set<String> zrangebyscore(String key, double min, double max,
            long limit, long count);

    public Set<String> zrangebyscore(String key, IntervalValue min,
            IntervalValue max, long limit, long count);

    public Set<byte[]> zrangebyscoreAsBytes(String key, double min, double max);

    public Set<byte[]> zrangebyscoreAsBytes(String key, IntervalValue min,
            IntervalValue max);

    public Set<byte[]> zrangebyscoreAsBytes(String key, double min, double max,
            long limit, long count);

    public Set<byte[]> zrangebyscoreAsBytes(String key, IntervalValue min,
            IntervalValue max, long limit, long count);

    public <T> Set<T> zrangebyscore(String key, double min, double max,
            Transcoder<T> transcoder);

    public <T> Set<T> zrangebyscore(String key, IntervalValue min,
            IntervalValue max, Transcoder<T> transcoder);

    public <T> Set<T> zrangebyscore(String key, double min, double max,
            long limit, long count, Transcoder<T> transcoder);

    public <T> Set<T>
            zrangebyscore(String key, IntervalValue min, IntervalValue max,
                    long limit, long count, Transcoder<T> transcoder);

    public Set<StringSortedSetEntry> zrangebyscoreWithScores(String key,
            double min, double max);

    public Set<StringSortedSetEntry> zrangebyscoreWithScores(String key,
            IntervalValue min, IntervalValue max);

    public Set<StringSortedSetEntry> zrangebyscoreWithScores(String key,
            double min, double max, long limit, long count);

    public Set<StringSortedSetEntry> zrangebyscoreWithScores(String key,
            IntervalValue min, IntervalValue max, long limit, long count);

    public Set<BytesSortedSetEntry> zrangebyscoreWithScoresAsBytes(String key,
            double min, double max);

    public Set<BytesSortedSetEntry> zrangebyscoreWithScoresAsBytes(String key,
            IntervalValue min, IntervalValue max);

    public Set<BytesSortedSetEntry> zrangebyscoreWithScoresAsBytes(String key,
            double min, double max, long limit, long count);

    public Set<BytesSortedSetEntry> zrangebyscoreWithScoresAsBytes(String key,
            IntervalValue min, IntervalValue max, long limit, long count);

    public <T, U extends SortedSetEntry<T>> Set<U> zrangebyscoreWithScores(
            String key, double min, double max, Transcoder<T> transcoder);

    public <T, U extends SortedSetEntry<T>> Set<U> zrangebyscoreWithScores(
            String key, IntervalValue min, IntervalValue max,
            Transcoder<T> transcoder);

    public <T, U extends SortedSetEntry<T>> Set<U> zrangebyscoreWithScores(
            String key, double min, double max, long limit, long count,
            Transcoder<T> transcoder);

    public <T, U extends SortedSetEntry<T>> Set<U> zrangebyscoreWithScores(
            String key, IntervalValue min, IntervalValue max, long limit,
            long count, Transcoder<T> transcoder);

    /* zrank */
    public Long zrank(String key, String member);

    public Long zrank(String key, byte[] member);

    public <T> Long zrank(String key, T member, Transcoder<T> transcoder);

    /* zrem */
    public long zrem(String key, String... members);

    public long zrem(String key, Iterator<String> members);

    public long zrem(String key, Iterable<String> members);

    public long zremAsBytes(String key, byte[]... members);

    public long zremAsBytes(String key, Iterator<byte[]> members);

    public long zremAsBytes(String key, Iterable<byte[]> members);

    public <T> long zrem(String key, Transcoder<T> transcoder, T... members);

    public <T> long zrem(String key, Iterator<T> members,
            Transcoder<T> transcoder);

    public <T> long zrem(String key, Iterable<T> members,
            Transcoder<T> transcoder);

    /* zremrangebyrank */
    public long zremrangebyrank(String key, long start, long stop);

    /* zremrangebyscore */
    public long zremrangebyscore(String key, double min, double max);

    /* zrevrange */
    public Set<String> zrevrange(String key, long start, long stop);

    public Set<byte[]> zrevrangeAsBytes(String key, long start, long stop);

    public <T> Set<T> zrevrange(String key, long start, long stop,
            Transcoder<T> transcoder);

    public Set<StringSortedSetEntry> zrevrangeWithScores(String key,
            long start, long stop);

    public Set<BytesSortedSetEntry> zrevrangeWithScoresAsBytes(String key,
            long start, long stop);

    public <T, U extends SortedSetEntry<T>> Set<U> zrevrangeWithScores(
            String key, long start, long stop, Transcoder<T> transcoder);

    /* zrevrangebyscore */
    public Set<String> zrevrangebyscore(String key, double min, double max);

    public Set<String> zrevrangebyscore(String key, IntervalValue min,
            IntervalValue max);

    public Set<String> zrevrangebyscore(String key, double min, double max,
            long limit, long count);

    public Set<String> zrevrangebyscore(String key, IntervalValue min,
            IntervalValue max, long limit, long count);

    public Set<byte[]> zrevrangebyscoreAsBytes(String key, double min,
            double max);

    public Set<byte[]> zrevrangebyscoreAsBytes(String key, IntervalValue min,
            IntervalValue max);

    public Set<byte[]> zrevrangebyscoreAsBytes(String key, double min,
            double max, long limit, long count);

    public Set<byte[]> zrevrangebyscoreAsBytes(String key, IntervalValue min,
            IntervalValue max, long limit, long count);

    public <T> Set<T> zrevrangebyscore(String key, double min, double max,
            Transcoder<T> transcoder);

    public <T> Set<T> zrevrangebyscore(String key, IntervalValue min,
            IntervalValue max, Transcoder<T> transcoder);

    public <T> Set<T> zrevrangebyscore(String key, double min, double max,
            long limit, long count, Transcoder<T> transcoder);

    public <T> Set<T>
            zrevrangebyscore(String key, IntervalValue min, IntervalValue max,
                    long limit, long count, Transcoder<T> transcoder);

    public Set<StringSortedSetEntry> zrevrangebyscoreWithScores(String key,
            double min, double max);

    public Set<StringSortedSetEntry> zrevrangebyscoreWithScores(String key,
            IntervalValue min, IntervalValue max);

    public Set<StringSortedSetEntry> zrevrangebyscoreWithScores(String key,
            double min, double max, long limit, long count);

    public Set<StringSortedSetEntry> zrevrangebyscoreWithScores(String key,
            IntervalValue min, IntervalValue max, long limit, long count);

    public Set<BytesSortedSetEntry> zrevrangebyscoreWithScoresAsBytes(
            String key, double min, double max);

    public Set<BytesSortedSetEntry> zrevrangebyscoreWithScoresAsBytes(
            String key, IntervalValue min, IntervalValue max);

    public Set<BytesSortedSetEntry> zrevrangebyscoreWithScoresAsBytes(
            String key, double min, double max, long limit, long count);

    public Set<BytesSortedSetEntry> zrevrangebyscoreWithScoresAsBytes(
            String key, IntervalValue min, IntervalValue max, long limit,
            long count);

    public <T, U extends SortedSetEntry<T>> Set<U> zrevrangebyscoreWithScores(
            String key, double min, double max, Transcoder<T> transcoder);

    public <T, U extends SortedSetEntry<T>> Set<U> zrevrangebyscoreWithScores(
            String key, IntervalValue min, IntervalValue max,
            Transcoder<T> transcoder);

    public <T, U extends SortedSetEntry<T>> Set<U> zrevrangebyscoreWithScores(
            String key, double min, double max, long limit, long count,
            Transcoder<T> transcoder);

    public <T, U extends SortedSetEntry<T>> Set<U> zrevrangebyscoreWithScores(
            String key, IntervalValue min, IntervalValue max, long limit,
            long count, Transcoder<T> transcoder);

    /* zrevrank */
    public Long zrevrank(String key, String member);

    public Long zrevrank(String key, byte[] member);

    public <T> Long zrevrank(String key, T member, Transcoder<T> transcoder);

    /* zscore */
    public Double zscore(String key, String member);

    public Double zscore(String key, byte[] member);

    public <T> Double zscore(String key, T member, Transcoder<T> transcoder);

    /* zunionstore */
    public long zunionstore(String destination, String... keys);

    public long zunionstore(String destination, Iterator<String> keys);

    public long zunionstore(String destination, Iterable<String> keys);

    public long zunionstore(String destination, Aggregation aggregate,
            String... keys);

    public long zunionstore(String destination, Iterator<String> keys,
            Aggregation aggregate);

    public long zunionstore(String destination, Iterable<String> keys,
            Aggregation aggregate);

    public long zunionstoreWeights(String destination, KeyWeight... keyweights);

    public long zunionstoreWeights(String destination,
            Iterator<KeyWeight> keyweights);

    public long zunionstoreWeights(String destination,
            Iterable<KeyWeight> keyweights);

    public long zunionstoreWeights(String destination, Aggregation aggregate,
            KeyWeight... keyweights);

    public long zunionstoreWeights(String destination,
            Iterator<KeyWeight> keyweights, Aggregation aggregate);

    public long zunionstoreWeights(String destination,
            Iterable<KeyWeight> keyweights, Aggregation aggregate);
}
