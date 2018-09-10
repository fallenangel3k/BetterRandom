package io.github.pr0methean.betterrandom.benchmark;

import static io.github.pr0methean.betterrandom.seed.SecureRandomSeedGenerator.SECURE_RANDOM_SEED_GENERATOR;

import io.github.pr0methean.betterrandom.prng.AesCounterRandom;
import io.github.pr0methean.betterrandom.prng.ReseedingThreadLocalRandomWrapper;
import java.util.Random;
import java8.util.function.Supplier;

public class ReseedingThreadLocalRandomWrapperAesCounterRandom128Benchmark
    extends AbstractRandomBenchmark {

  @Override protected Random createPrng() {
    return new ReseedingThreadLocalRandomWrapper(SECURE_RANDOM_SEED_GENERATOR,
        new Supplier<AesCounterRandom>() {
          @Override public AesCounterRandom get() {
            return new AesCounterRandom(SECURE_RANDOM_SEED_GENERATOR.generateSeed(16));
          }
        });
  }
}