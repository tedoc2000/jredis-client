package com.zibobo.yedis.ops.connection;

import com.zibobo.yedis.ops.Operation;
import com.zibobo.yedis.ops.OperationCallback;

public interface PingOperation extends Operation {

    /**
     * Callback for PING operation.
     */
    interface Callback extends OperationCallback {
        /**
         * Invoked once the pong is received
         * 
         */
        void pong(String pong);
    }
}
