package com.johanwedin.urlshortener.benchmarks;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;

public class JmhRunner {

    @Test
    void runBenchmarks() throws RunnerException {
        new File("benchmark-results/jmh-report/").mkdirs();
        Options opt = new OptionsBuilder().include(HashBenchmark.class.getSimpleName()).forks(1).shouldDoGC(true).resultFormat(ResultFormatType.JSON).result("benchmark-results/jmh-report/" + System.currentTimeMillis() + ".json").build();
        new Runner(opt).run();
    }
}
