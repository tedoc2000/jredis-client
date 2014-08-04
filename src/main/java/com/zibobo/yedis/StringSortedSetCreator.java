package com.zibobo.yedis;

import java.util.LinkedHashSet;
import java.util.Set;

public class StringSortedSetCreator implements
        SortedSetCreator<String, StringSortedSetEntry> {

    private Set<StringSortedSetEntry> set;

    public StringSortedSetCreator() {
    }

    @Override
    public void create() {
        set = new LinkedHashSet<StringSortedSetEntry>();
    }

    @Override
    public void addElement(String element, double score) {
        set.add(new StringSortedSetEntry(element, score));
    }

    @Override
    public Set<StringSortedSetEntry> finish() {
        return set;
    }

}
