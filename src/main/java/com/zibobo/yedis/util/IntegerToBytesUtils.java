package com.zibobo.yedis.util;

import java.util.Arrays;

public class IntegerToBytesUtils {
    final static byte[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9' };

    private final static int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999,
            9999999, 99999999, 999999999, Integer.MAX_VALUE };

    final static byte[] DigitTens = { '0', '0', '0', '0', '0', '0', '0', '0',
            '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2',
            '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3',
            '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4',
            '4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
            '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7',
            '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8',
            '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9',
            '9', };

    final static byte[] DigitOnes = { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2',
            '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', };

    // Requires positive x
    public static int bytesSize(int x) {
        for (int i = 0;; i++)
            if (x <= sizeTable[i])
                return i + 1;
    }

    private static final byte[] MIN_VALUE = new byte[] { '-', '2', '1', '4',
            '7', '4', '8', '3', '6', '4', '8' };

    public static byte[] toBytes(int i) {
        if (i == Integer.MIN_VALUE) {
            return Arrays.copyOf(MIN_VALUE, MIN_VALUE.length);
        }

        int size = (i < 0) ? bytesSize(-i) + 1 : bytesSize(i);
        byte[] buf = new byte[size];
        getBytes(i, size, buf);
        return buf;
    }

    /**
     * Places ascii bytes representing the integer i into the character array
     * buf. The characters are placed into the buffer backwards starting with
     * the least significant digit at the specified index (exclusive), and
     * working backwards from there.
     *
     * Will fail if i == Integer.MIN_VALUE
     */
    private static void getBytes(int i, int index, byte[] buf) {
        int q, r;
        int charPos = index;
        byte sign = 0;

        if (i < 0) {
            sign = '-';
            i = -i;
        }

        // Generate two digits per iteration
        while (i >= 65536) {
            q = i / 100;
            // really: r = i - (q * 100);
            r = i - ((q << 6) + (q << 5) + (q << 2));
            i = q;
            buf[--charPos] = DigitOnes[r];
            buf[--charPos] = DigitTens[r];
        }

        // Fall thru to fast mode for smaller numbers
        // assert(i <= 65536, i);
        for (;;) {
            q = (i * 52429) >>> (16 + 3);
            r = i - ((q << 3) + (q << 1)); // r = i-(q*10) ...
            buf[--charPos] = digits[r];
            i = q;
            if (i == 0)
                break;
        }
        if (sign != 0) {
            buf[--charPos] = sign;
        }
    }

    public static int parseInt(byte[] s, int radix)
            throws NumberFormatException {
        /*
         * WARNING: This method may be invoked early during VM initialization
         * before IntegerCache is initialized. Care must be taken to not use the
         * valueOf method.
         */

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

        int result = 0;
        boolean negative = false;
        int i = 0, len = s.length;
        int limit = -Integer.MAX_VALUE;
        int multmin;
        int digit;

        if (len > 0) {
            char firstChar = (char) s[0];
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Integer.MIN_VALUE;
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
