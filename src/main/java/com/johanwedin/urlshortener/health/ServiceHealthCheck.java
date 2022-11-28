package com.johanwedin.urlshortener.health;

import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

public class ServiceHealthCheck extends NamedHealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }

    @Override
    public String getName() {
        return "urlshortener";
    }
}
