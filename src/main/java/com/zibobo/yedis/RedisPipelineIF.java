package com.zibobo.yedis;

public interface RedisPipelineIF extends AsyncStringOperations,
        AsyncKeyOperations, AsyncHashesOperations, AsyncListsOperations,
        AsyncSetsOperations, AsyncSortedSetsOperations {

}
