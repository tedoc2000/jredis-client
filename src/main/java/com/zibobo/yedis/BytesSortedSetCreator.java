package com.zibobo.yedis;

import java.util.LinkedHashSet;
import java.util.Set;

public class BytesSortedSetCreator implements
        SortedSetCreator<byte[], BytesSortedSetEntry> {

    private Set<BytesSortedSetEntry> set;

    public BytesSortedSetCreator() {
    }

    @Override
    public void create() {
        set = new LinkedHashSet<BytesSortedSetEntry>();
    }

    @Override
    public void addElement(byte[] element, double score) {
        set.add(new BytesSortedSetEntry(element, score));
    }

    @Override
    public Set<BytesSortedSetEntry> finish() {
        return set;
    }

}
