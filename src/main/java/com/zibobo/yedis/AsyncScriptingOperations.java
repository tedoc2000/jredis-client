package com.zibobo.yedis;

import java.util.Iterator;

import com.zibobo.yedis.transcoder.Transcoder;

public interface AsyncScriptingOperations {

    /* eval */
    /* no args to value*/
    public String eval(String script, String... keys);
    public String eval(String script, Iterator<String> keys);
    public String eval(String script, Iterable<String> keys);

    public byte[] evalAsBytes(String script, String... keys);
    public byte[] evalAsBytes(String script, Iterator<String> keys);
    public byte[] evalAsBytes(String script, Iterable<String> keys);

    public <T> T eval(String script, Transcoder<T> transcoder, String... keys);
    public <T> T eval(String script, Iterator<String> keys, Transcoder<T> transcoder);
    public <T> T eval(String script, Iterable<String> keys, Transcoder<T> transcoder);

    public String eval(String script, String[] keys, String... args);

    public String eval(String script, Iterator<String> keys,
            Iterator<String> args);

    public String eval(String script, Iterator<String> keys,
            Iterable<String> args);

    public String eval(String script, Iterable<String> keys,
            Iterable<String> args);

    public byte[] evalAsBytes(String script, String[] keys, byte[] args);

    public byte[] evalAsBytes(String script, Iterator<String> keys,
            Iterator<byte[]> args);

    public byte[] evalAsBytes(String script, Iterable<String> keys,
            Iterator<byte[]> args);

    public <T> T eval(String script, String[] keys, String[] args,
            Transcoder<T> transcoder);

}
