package com.zibobo.yedis.protocol.unified.response;

import java.nio.ByteBuffer;

public interface Parser {

    public void parse(ByteBuffer data);

    public boolean done();

}
