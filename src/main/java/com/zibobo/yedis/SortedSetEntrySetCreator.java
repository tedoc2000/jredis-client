package com.zibobo.yedis;

import java.util.LinkedHashSet;
import java.util.Set;

public class SortedSetEntrySetCreator<T, U extends SortedSetEntry<T>>
        implements SortedSetCreator<T, U> {

    private Set<U> set;

    @Override
    public void create() {
        set = new LinkedHashSet<U>();

    }

    @SuppressWarnings("unchecked")
    @Override
    public void addElement(T element, double score) {
        set.add((U) new SortedSetEntry<T>(element, score));
    }

    @Override
    public Set<U> finish() {
       return set;
    }

}
