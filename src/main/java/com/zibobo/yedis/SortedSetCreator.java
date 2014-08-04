package com.zibobo.yedis;

import java.util.Set;

public interface SortedSetCreator<T, U extends SortedSetEntry<T>> {
    public void create();

    public void addElement(T element, double score);

    public Set<U> finish();
}