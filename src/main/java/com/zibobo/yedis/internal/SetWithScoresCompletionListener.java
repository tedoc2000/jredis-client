package com.zibobo.yedis.internal;

import com.zibobo.yedis.SortedSetEntry;

public interface SetWithScoresCompletionListener extends
        GenericCompletionListener<SetWithScoresFuture<?,SortedSetEntry<?>>> {

}
