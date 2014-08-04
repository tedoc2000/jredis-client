package com.zibobo.yedis.protocol.unified.response;

import net.spy.memcached.compat.SpyObject;

public abstract class AbstractParser extends SpyObject implements Parser {

    protected boolean done;

    @Override
    public boolean done() {
        return done;
    }

}
