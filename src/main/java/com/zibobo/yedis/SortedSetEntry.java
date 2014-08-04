package com.zibobo.yedis;

public class SortedSetEntry<T> {

    public final T value;
    public final double score;

    public SortedSetEntry(T value, double score) {
        this.value = value;
        this.score = score;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(score);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("rawtypes")
        SortedSetEntry other = (SortedSetEntry) obj;
        if (Double.doubleToLongBits(score) != Double
                .doubleToLongBits(other.score))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SortedSetEntry [value=" + value + ", score=" + score + "]";
    }

}
