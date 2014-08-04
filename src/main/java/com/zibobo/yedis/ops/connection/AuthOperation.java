package com.zibobo.yedis.ops.connection;

import com.zibobo.yedis.ops.Operation;
import com.zibobo.yedis.ops.OperationCallback;

public interface AuthOperation extends Operation {

    /**
     * Callback for AUTH operation
     * 
     */
    interface Callback extends OperationCallback {
        /**
         * Invoked if pong was successful
         */
        void authed();
    }
}
