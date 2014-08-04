package com.zibobo.yedis.util;

import java.nio.charset.Charset;

public class DoubleToBytesUtils {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final byte[] NEG_INF = new byte[] { '-', 'i', 'n', 'f' };
    private static final byte[] POS_INF = new byte[] { 'i', 'n', 'f' };

    public static byte[] toBytes(double d) {
        if (Double.isInfinite(d)) {
            if (d < 0.0) {
                return NEG_INF;
            } else {
                return POS_INF;
            }
        }
        return Double.toString(d).getBytes(UTF_8);
    }

}
