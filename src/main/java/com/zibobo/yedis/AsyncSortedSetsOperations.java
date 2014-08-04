package com.zibobo.yedis;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Future;

import com.zibobo.yedis.transcoder.Transcoder;

public interface AsyncSortedSetsOperations {

    /* zadd */
    public Future<Long> asyncZadd(String key, StringSortedSetEntry... entries);

    public Future<Long> asyncZadd(String key,
            Iterator<StringSortedSetEntry> entries);

    public Future<Long> asyncZadd(String key,
            Iterable<StringSortedSetEntry> entries);

    public Future<Long> asyncZaddAsBytes(String key,
            BytesSortedSetEntry... entries);

    public Future<Long> asyncZaddAsBytes(String key,
            Iterator<BytesSortedSetEntry> entries);

    public Future<Long> asyncZaddAsBytes(String key,
            Iterable<BytesSortedSetEntry> entries);

    public <T, U extends SortedSetEntry<T>> Future<Long> asyncZadd(String key,
            Transcoder<T> transcoder, U... entries);

    public <T, U extends SortedSetEntry<T>> Future<Long> asyncZadd(String key,
            Iterator<U> entries, Transcoder<T> transcoder);

    public <T, U extends SortedSetEntry<T>> Future<Long> asyncZadd(String key,
            Iterable<U> entries, Transcoder<T> transcoder);

    /* zcard */

    public Future<Long> asyncZcard(String key);

    /* zcount */
    public Future<Long> asyncZcount(String key, double min, double max);

    /* zincrby */
    public Future<Double> asyncZincrby(String key, double increment,
            String member);

    public Future<Double> asyncZincrby(String key, double increment,
            byte[] member);

    public <T> Future<Double> asyncZincrby(String key, double increment,
            T member, Transcoder<T> transcoder);

    /* zinterstore */
    public Future<Long> asyncZinterstore(String destination, String... keys);

    public Future<Long> asyncZinterstore(String destination,
            Iterator<String> keys);

    public Future<Long> asyncZinterstore(String destination,
            Iterable<String> keys);

    public Future<Long> asyncZinterstore(String destination,
            Aggregation aggregate, String... keys);

    public Future<Long> asyncZinterstore(String destination,
            Iterator<String> keys, Aggregation aggregate);

    public Future<Long> asyncZinterstore(String destination,
            Iterable<String> keys, Aggregation aggregate);

    public Future<Long> asyncZinterstoreWeights(String destination,
            KeyWeight... keyweights);

    public Future<Long> asyncZinterstoreWeights(String destination,
            Iterator<KeyWeight> keyweights);

    public Future<Long> asyncZinterstoreWeights(String destination,
            Iterable<KeyWeight> keyweights);

    public Future<Long> asyncZinterstoreWeights(String destination,
            Aggregation aggregate, KeyWeight... keyweights);

    public Future<Long> asyncZinterstoreWeights(String destination,
            Iterator<KeyWeight> keyweights, Aggregation aggregate);

    public Future<Long> asyncZinterstoreWeights(String destination,
            Iterable<KeyWeight> keyweights, Aggregation aggregate);

    /* zrange */
    public Future<Set<String>> asyncZrange(String key, long start, long stop);

    public Future<Set<byte[]>> asyncZrangeAsBytes(String key, long start,
            long stop);

    public <T> Future<Set<T>> asyncZrange(String key, long start, long stop,
            Transcoder<T> transcoder);

    public Future<Set<StringSortedSetEntry>> asyncZrangeWithScores(String key,
            long start, long stop);

    public Future<Set<BytesSortedSetEntry>> asyncZrangeWithScoresAsBytes(
            String key, long start, long stop);

    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrangeWithScores(String key, long start, long stop,
                    Transcoder<T> transcoder);

    /* zrangebyscore */
    public Future<Set<String>> asyncZrangebyscore(String key, double min,
            double max);

    public Future<Set<String>> asyncZrangebyscore(String key,
            IntervalValue min, IntervalValue max);

    public Future<Set<String>> asyncZrangebyscore(String key, double min,
            double max, long limit, long count);

    public Future<Set<String>> asyncZrangebyscore(String key,
            IntervalValue min, IntervalValue max, long limit, long count);

    public Future<Set<byte[]>> asyncZrangebyscoreAsBytes(String key,
            double min, double max);

    public Future<Set<byte[]>> asyncZrangebyscoreAsBytes(String key,
            IntervalValue min, IntervalValue max);

    public Future<Set<byte[]>> asyncZrangebyscoreAsBytes(String key,
            double min, double max, long limit, long count);

    public Future<Set<byte[]>> asyncZrangebyscoreAsBytes(String key,
            IntervalValue min, IntervalValue max, long limit, long count);

    public <T> Future<Set<T>> asyncZrangebyscore(String key, double min,
            double max, Transcoder<T> transcoder);

    public <T> Future<Set<T>> asyncZrangebyscore(String key, IntervalValue min,
            IntervalValue max, Transcoder<T> transcoder);

    public <T> Future<Set<T>> asyncZrangebyscore(String key, double min,
            double max, long limit, long count, Transcoder<T> transcoder);

    public <T> Future<Set<T>>
            asyncZrangebyscore(String key, IntervalValue min,
                    IntervalValue max, long limit, long count,
                    Transcoder<T> transcoder);

    public Future<Set<StringSortedSetEntry>> asyncZrangebyscoreWithScores(
            String key, double min, double max);

    public Future<Set<StringSortedSetEntry>> asyncZrangebyscoreWithScores(
            String key, IntervalValue min, IntervalValue max);

    public Future<Set<StringSortedSetEntry>> asyncZrangebyscoreWithScores(
            String key, double min, double max, long limit, long count);

    public Future<Set<StringSortedSetEntry>> asyncZrangebyscoreWithScores(
            String key, IntervalValue min, IntervalValue max, long limit,
            long count);

    public Future<Set<BytesSortedSetEntry>>
            asyncZrangebyscoreWithScoresAsBytes(String key, double min,
                    double max);

    public Future<Set<BytesSortedSetEntry>>
            asyncZrangebyscoreWithScoresAsBytes(String key, IntervalValue min,
                    IntervalValue max);

    public Future<Set<BytesSortedSetEntry>>
            asyncZrangebyscoreWithScoresAsBytes(String key, double min,
                    double max, long limit, long count);

    public Future<Set<BytesSortedSetEntry>>
            asyncZrangebyscoreWithScoresAsBytes(String key, IntervalValue min,
                    IntervalValue max, long limit, long count);

    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrangebyscoreWithScores(String key, double min, double max,
                    Transcoder<T> transcoder);

    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrangebyscoreWithScores(String key, IntervalValue min,
                    IntervalValue max, Transcoder<T> transcoder);

    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrangebyscoreWithScores(String key, double min, double max,
                    long limit, long count, Transcoder<T> transcoder);

    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrangebyscoreWithScores(String key, IntervalValue min,
                    IntervalValue max, long limit, long count,
                    Transcoder<T> transcoder);

    /* zrank */
    public Future<Long> asyncZrank(String key, String member);

    public Future<Long> asyncZrank(String key, byte[] member);

    public <T> Future<Long> asyncZrank(String key, T member,
            Transcoder<T> transcoder);

    /* zrem */
    public Future<Long> asyncZrem(String key, String... members);

    public Future<Long> asyncZrem(String key, Iterator<String> members);

    public Future<Long> asyncZrem(String key, Iterable<String> members);

    public Future<Long> asyncZremAsBytes(String key, byte[]... members);

    public Future<Long> asyncZremAsBytes(String key, Iterator<byte[]> members);

    public Future<Long> asyncZremAsBytes(String key, Iterable<byte[]> members);

    public <T> Future<Long> asyncZrem(String key, Transcoder<T> transcoder,
            T... members);

    public <T> Future<Long> asyncZrem(String key, Iterator<T> members,
            Transcoder<T> transcoder);

    public <T> Future<Long> asyncZrem(String key, Iterable<T> members,
            Transcoder<T> transcoder);

    /* zremrangebyrank */
    public Future<Long> asyncZremrangebyrank(String key, long start, long stop);

    /* zremrangebyscore */
    public Future<Long>
            asyncZremrangebyscore(String key, double min, double max);

    /* zrevrange */
    public Future<Set<String>>
            asyncZrevrange(String key, long start, long stop);

    public Future<Set<byte[]>> asyncZrevrangeAsBytes(String key, long start,
            long stop);

    public <T> Future<Set<T>> asyncZrevrange(String key, long start, long stop,
            Transcoder<T> transcoder);

    public Future<Set<StringSortedSetEntry>> asyncZrevrangeWithScores(
            String key, long start, long stop);

    public Future<Set<BytesSortedSetEntry>> asyncZrevrangeWithScoresAsBytes(
            String key, long start, long stop);

    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrevrangeWithScores(String key, long start, long stop,
                    Transcoder<T> transcoder);

    /* zrevrangebyscore */
    public Future<Set<String>> asyncZrevrangebyscore(String key, double min,
            double max);

    public Future<Set<String>> asyncZrevrangebyscore(String key,
            IntervalValue min, IntervalValue max);

    public Future<Set<String>> asyncZrevrangebyscore(String key, double min,
            double max, long limit, long count);

    public Future<Set<String>> asyncZrevrangebyscore(String key,
            IntervalValue min, IntervalValue max, long limit, long count);

    public Future<Set<byte[]>> asyncZrevrangebyscoreAsBytes(String key,
            double min, double max);

    public Future<Set<byte[]>> asyncZrevrangebyscoreAsBytes(String key,
            IntervalValue min, IntervalValue max);

    public Future<Set<byte[]>> asyncZrevrangebyscoreAsBytes(String key,
            double min, double max, long limit, long count);

    public Future<Set<byte[]>> asyncZrevrangebyscoreAsBytes(String key,
            IntervalValue min, IntervalValue max, long limit, long count);

    public <T> Future<Set<T>> asyncZrevrangebyscore(String key, double min,
            double max, Transcoder<T> transcoder);

    public <T> Future<Set<T>> asyncZrevrangebyscore(String key,
            IntervalValue min, IntervalValue max, Transcoder<T> transcoder);

    public <T> Future<Set<T>> asyncZrevrangebyscore(String key, double min,
            double max, long limit, long count, Transcoder<T> transcoder);

    public <T> Future<Set<T>> asyncZrevrangebyscore(String key,
            IntervalValue min, IntervalValue max, long limit, long count,
            Transcoder<T> transcoder);

    public Future<Set<StringSortedSetEntry>> asyncZrevrangebyscoreWithScores(
            String key, double min, double max);

    public Future<Set<StringSortedSetEntry>> asyncZrevrangebyscoreWithScores(
            String key, IntervalValue min, IntervalValue max);

    public Future<Set<StringSortedSetEntry>> asyncZrevrangebyscoreWithScores(
            String key, double min, double max, long limit, long count);

    public Future<Set<StringSortedSetEntry>> asyncZrevrangebyscoreWithScores(
            String key, IntervalValue min, IntervalValue max, long limit,
            long count);

    public Future<Set<BytesSortedSetEntry>>
            asyncZrevrangebyscoreWithScoresAsBytes(String key, double min,
                    double max);

    public Future<Set<BytesSortedSetEntry>>
            asyncZrevrangebyscoreWithScoresAsBytes(String key,
                    IntervalValue min, IntervalValue max);

    public Future<Set<BytesSortedSetEntry>>
            asyncZrevrangebyscoreWithScoresAsBytes(String key, double min,
                    double max, long limit, long count);

    public
            Future<Set<BytesSortedSetEntry>>
            asyncZrevrangebyscoreWithScoresAsBytes(String key,
                    IntervalValue min, IntervalValue max, long limit, long count);

    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrevrangebyscoreWithScores(String key, double min, double max,
                    Transcoder<T> transcoder);

    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrevrangebyscoreWithScores(String key, IntervalValue min,
                    IntervalValue max, Transcoder<T> transcoder);

    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrevrangebyscoreWithScores(String key, double min, double max,
                    long limit, long count, Transcoder<T> transcoder);

    public <T, U extends SortedSetEntry<T>> Future<Set<U>>
            asyncZrevrangebyscoreWithScores(String key, IntervalValue min,
                    IntervalValue max, long limit, long count,
                    Transcoder<T> transcoder);

    /* zrevrank */
    public Future<Long> asyncZrevrank(String key, String member);

    public Future<Long> asyncZrevrank(String key, byte[] member);

    public <T> Future<Long> asyncZrevrank(String key, T member,
            Transcoder<T> transcoder);

    /* zscore */
    public Future<Double> asyncZscore(String key, String member);

    public Future<Double> asyncZscore(String key, byte[] member);

    public <T> Future<Double> asyncZscore(String key, T member,
            Transcoder<T> transcoder);

    /* zunionstore */
    public Future<Long> asyncZunionstore(String destination, String... keys);

    public Future<Long> asyncZunionstore(String destination,
            Iterator<String> keys);

    public Future<Long> asyncZunionstore(String destination,
            Iterable<String> keys);

    public Future<Long> asyncZunionstore(String destination,
            Aggregation aggregate, String... keys);

    public Future<Long> asyncZunionstore(String destination,
            Iterator<String> keys, Aggregation aggregate);

    public Future<Long> asyncZunionstore(String destination,
            Iterable<String> keys, Aggregation aggregate);

    public Future<Long> asyncZunionstoreWeights(String destination,
            KeyWeight... keyweights);

    public Future<Long> asyncZunionstoreWeights(String destination,
            Iterator<KeyWeight> keyweights);

    public Future<Long> asyncZunionstoreWeights(String destination,
            Iterable<KeyWeight> keyweights);

    public Future<Long> asyncZunionstoreWeights(String destination,
            Aggregation aggregate, KeyWeight... keyweights);

    public Future<Long> asyncZunionstoreWeights(String destination,
            Iterator<KeyWeight> keyweights, Aggregation aggregate);

    public Future<Long> asyncZunionstoreWeights(String destination,
            Iterable<KeyWeight> keyweights, Aggregation aggregate);

}
