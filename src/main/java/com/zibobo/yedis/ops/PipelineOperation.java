package com.zibobo.yedis.ops;

import java.util.concurrent.atomic.AtomicLong;

public interface PipelineOperation extends Operation {

    public AtomicLong addOperation(Operation operation, long timeout);

    public void execute();
}
