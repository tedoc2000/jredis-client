package com.zibobo.yedis.util;

import java.nio.charset.Charset;
import java.util.Arrays;

public class LongToBytesUtils {

    private static final byte[] MIN_VALUE = "-9223372036854775808"
            .getBytes(Charset.forName("UTF-8"));

    public static byte[] toBytes(long i) {
        if (i == Long.MIN_VALUE)
            return Arrays.copyOf(MIN_VALUE, MIN_VALUE.length);
        int size = (i < 0) ? stringSize(-i) + 1 : stringSize(i);
        byte[] buf = new byte[size];
        getBytes(i, size, buf);
        return buf;
    }

    /**
     * Places characters representing the integer i into the character array
     * buf. The characters are placed into the buffer backwards starting with
     * the least significant digit at the specified index (exclusive), and
     * working backwards from there.
     *
     * Will fail if i == Long.MIN_VALUE
     */
    static void getBytes(long i, int index, byte[] buf) {
        long q;
        int r;
        int charPos = index;
        byte sign = 0;

        if (i < 0) {
            sign = '-';
            i = -i;
        }

        // Get 2 digits/iteration using longs until quotient fits into an int
        while (i > Integer.MAX_VALUE) {
            q = i / 100;
            // really: r = i - (q * 100);
            r = (int) (i - ((q << 6) + (q << 5) + (q << 2)));
            i = q;
            buf[--charPos] = IntegerToBytesUtils.DigitOnes[r];
            buf[--charPos] = IntegerToBytesUtils.DigitTens[r];
        }

        // Get 2 digits/iteration using ints
        int q2;
        int i2 = (int) i;
        while (i2 >= 65536) {
            q2 = i2 / 100;
            // really: r = i2 - (q * 100);
            r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
            i2 = q2;
            buf[--charPos] = IntegerToBytesUtils.DigitOnes[r];
            buf[--charPos] = IntegerToBytesUtils.DigitTens[r];
        }

        // Fall thru to fast mode for smaller numbers
        // assert(i2 <= 65536, i2);
        for (;;) {
            q2 = (i2 * 52429) >>> (16 + 3);
            r = i2 - ((q2 << 3) + (q2 << 1)); // r = i2-(q2*10) ...
            buf[--charPos] = IntegerToBytesUtils.digits[r];
            i2 = q2;
            if (i2 == 0)
                break;
        }
        if (sign != 0) {
            buf[--charPos] = sign;
        }
    }

    // Requires positive x
    static int stringSize(long x) {
        long p = 10;
        for (int i = 1; i < 19; i++) {
            if (x < p)
                return i;
            p = 10 * p;
        }
        return 19;
    }

    public static long parseLong(byte[] s, int radix)
            throws NumberFormatException {
        if (s == null) {
            throw new NumberFormatException("null");
        }

        if (radix < Character.MIN_RADIX) {
            throw new NumberFormatException("radix " + radix
                    + " less than Character.MIN_RADIX");
        }
        if (radix > Character.MAX_RADIX) {
            throw new NumberFormatException("radix " + radix
                    + " greater than Character.MAX_RADIX");
        }

        long result = 0;
        boolean negative = false;
        int i = 0, len = s.length;
        long limit = -Long.MAX_VALUE;
        long multmin;
        int digit;

        if (len > 0) {
            char firstChar = (char) s[0];
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Long.MIN_VALUE;
                } else if (firstChar != '+')
                    throw new NumberFormatException();

                if (len == 1) // Cannot have lone "+" or "-"
                    throw new NumberFormatException();
                i++;
            }
            multmin = limit / radix;
            while (i < len) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = Character.digit(s[i++], radix);
                if (digit < 0) {
                    throw new NumberFormatException();
                }
                if (result < multmin) {
                    throw new NumberFormatException();
                }
                result *= radix;
                if (result < limit + digit) {
                    throw new NumberFormatException();
                }
                result -= digit;
            }
        } else {
            throw new NumberFormatException();
        }
        return negative ? result : -result;
    }
}
