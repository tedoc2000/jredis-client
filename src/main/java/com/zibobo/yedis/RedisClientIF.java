package com.zibobo.yedis;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface RedisClientIF extends AsyncStringOperations,
        SyncStringOperations, AsyncKeyOperations, SyncKeyOperations,
        AsyncHashesOperations, SyncHashesOperations, AsyncListsOperations,
        SyncListsOperation, AsyncSetsOperations, SyncSetsOperations,
        AsyncSortedSetsOperations, SyncSortedSetsOperations {

    /* ping */
    public Map<SocketAddress, String> ping();

    public RedisPipelineIF pipeline();

    /**
     * Shutdown connection(s) immediately
     */
    public void shutdown();

    /**
     * Shutdown connections(s) waiting timeout to drain pending command queues.
     *
     * @param timeout
     *            time
     * @param unit
     *            unit of <code>timeout</code> param
     * @return true if the shutdown was compeleted before the timeout
     */
    public boolean shutdown(long timeout, TimeUnit unit);
}
