package com.newland.tianyan.common.utils;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

public class RandomString {


    private static final String HEX = "0123456789abcdef";

    private final Random random;

    private final char[] symbols;

    private final char[] buf;

    public RandomString(int length, Random random, String symbols) {
        if (length < 1) {
            throw new IllegalArgumentException();
        }
        int maxLength = 2;
        if (symbols.length() < maxLength) {
            throw new IllegalArgumentException();
        }
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    /**
     * Create an alphanumeric string generator.
     */
    public RandomString(int length, Random random) {
        this(length, random, HEX);
    }

    /**
     * Create an alphanumeric strings from a secure generator.
     */
    public RandomString(int length) {
        this(length, new SecureRandom());
    }

    /**
     * Generate a random string.
     */
    public String nextString() {
        for (int index = 0; index < buf.length; ++index){
            buf[index] = symbols[random.nextInt(symbols.length)];
        }
        return new String(buf);
    }

}
