package com.johanwedin.urlshortener.helpers.hashing;

import org.apache.commons.codec.digest.MurmurHash3;

import java.nio.charset.StandardCharsets;

public class MurmurHashStrategy implements HashStrategy {
    private static final int SEED = 123;
    private static final char[] map = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();


    public MurmurHashStrategy() {
    }

    private String stringFromHash(int hash) {
        StringBuffer urlId = new StringBuffer();

        while (hash > 0)
        {
            urlId.append(map[hash % 62]);
            hash = hash / 62;
        }

        return urlId.reverse().toString();
    }
    @Override
    public String hash(String original, int padding) {
        byte[] bytes = original.getBytes(StandardCharsets.UTF_8);
        int h = MurmurHash3.hash32x86(bytes, 0, bytes.length, SEED+padding)>>>1;
        return this.stringFromHash(h);
    }
}
