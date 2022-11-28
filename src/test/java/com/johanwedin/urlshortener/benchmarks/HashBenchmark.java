package com.johanwedin.urlshortener.benchmarks;

import com.johanwedin.urlshortener.helpers.hashing.MurmurHashStrategy;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;


import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class HashBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        private String[] testList;
        private Random r;

        public String getString() {
            return testList[r.nextInt(testList.length)];
        }


        @Setup(Level.Trial)
        public void setUp() {
            r = new Random();
            testList = new String[]{"http://youtube.com/asd"};
        }
    }
    private MurmurHashStrategy hasher;
    @Setup
    public void setup() {
        hasher = new MurmurHashStrategy();
    }


    @Benchmark
    @Warmup(iterations = 1)
    @Measurement(iterations = 1)
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void hash(Blackhole blackHole, BenchmarkState state) {
        blackHole.consume(hasher.hash(state.getString(), 0));
    }
}