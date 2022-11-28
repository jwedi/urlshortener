package com.johanwedin.urlshortener.helpers.hashing;

public interface HashStrategy {
    String hash(String original, int padding);
}
