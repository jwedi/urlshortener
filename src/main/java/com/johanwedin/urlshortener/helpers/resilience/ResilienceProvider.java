package com.johanwedin.urlshortener.helpers.resilience;

import dev.failsafe.FailsafeExecutor;

import javax.inject.Provider;

public class ResilienceProvider implements Provider<FailsafeExecutor<Object>> {
    private final FailsafeExecutor<Object> fs;

    public ResilienceProvider(FailsafeExecutor<Object> fs) {
        this.fs = fs;
    }

    @Override
    public FailsafeExecutor<Object> get() {
        return this.fs;
    }
}
