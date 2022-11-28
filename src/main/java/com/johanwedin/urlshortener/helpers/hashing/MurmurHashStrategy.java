package com.johanwedin.urlshortener.helpers.hashing;

import org.apache.commons.codec.digest.MurmurHash3;

import java.nio.charset.StandardCharsets;

public class MurmurHashStrategy implements HashStrategy {
    private static final int SEED = 123;
    private static final char[] map = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();


    public MurmurHashStrategy() {
    }

    private String addPadding(String original, int padding) {
        return padding == 0 ? original : original+map[padding];
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
        String s = this.addPadding(original, padding);
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        int h = MurmurHash3.hash32x86(bytes, 0, bytes.length, SEED)>>>1;
        return this.stringFromHash(h);
    }
}
