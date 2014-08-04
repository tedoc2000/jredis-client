package com.zibobo.yedis;

import java.util.Arrays;

public class BytesSortedSetEntry extends SortedSetEntry<byte[]> {

    public BytesSortedSetEntry(byte[] value, double score) {
        super(value, score);

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(score);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result =
                prime * result + ((value == null) ? 0 : Arrays.hashCode(value));
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
        BytesSortedSetEntry other = (BytesSortedSetEntry) obj;
        if (Double.doubleToLongBits(score) != Double
                .doubleToLongBits(other.score))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!Arrays.equals(value, other.value))
            return false;
        return true;
    }
}
