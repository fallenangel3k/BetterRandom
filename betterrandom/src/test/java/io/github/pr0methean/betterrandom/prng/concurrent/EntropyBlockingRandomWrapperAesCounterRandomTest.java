package io.github.pr0methean.betterrandom.prng.concurrent;

import com.google.common.collect.ImmutableMap;
import io.github.pr0methean.betterrandom.FlakyRetryAnalyzer;
import io.github.pr0methean.betterrandom.TestUtils;
import io.github.pr0methean.betterrandom.prng.AesCounterRandom;
import io.github.pr0methean.betterrandom.prng.BaseRandom;
import io.github.pr0methean.betterrandom.prng.RandomTestUtils;
import io.github.pr0methean.betterrandom.seed.SeedException;
import io.github.pr0methean.betterrandom.seed.SeedGenerator;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import org.testng.annotations.Test;

@Test(testName = "EntropyBlockingRandomWrapper:AesCounterRandom")
public class EntropyBlockingRandomWrapperAesCounterRandomTest extends RandomWrapperAesCounterRandomTest {
  private static final long DEFAULT_MAX_ENTROPY = -1000L;

  @Override @Test public void testAllPublicConstructors() {
    Constructor<?>[] constructors =
        EntropyBlockingRandomWrapper.class.getDeclaredConstructors();
    ArrayList<Constructor<?>> relevantConstructors = new ArrayList<>(constructors.length);
    for (Constructor<?> constructor : constructors) {
      if (Arrays.asList(constructor.getParameterTypes()).contains(Random.class)) {
        relevantConstructors.add(constructor);
      }
    }
    TestUtils.testConstructors(false, ImmutableMap.copyOf(constructorParams()),
        (Consumer<? super EntropyBlockingRandomWrapper>) BaseRandom::nextInt,
        relevantConstructors);
  }

  @Override public Class<? extends BaseRandom> getClassUnderTest() {
    return EntropyBlockingRandomWrapper.class;
  }

  @Override protected RandomWrapper createRng() throws SeedException {
    SeedGenerator testSeedGenerator = getTestSeedGenerator();
    return new EntropyBlockingRandomWrapper(new AesCounterRandom(testSeedGenerator), DEFAULT_MAX_ENTROPY,
        testSeedGenerator);
  }

  @Override protected RandomWrapper createRng(byte[] seed) throws SeedException {
    SeedGenerator testSeedGenerator = getTestSeedGenerator();
    return new EntropyBlockingRandomWrapper(new AesCounterRandom(seed), DEFAULT_MAX_ENTROPY,
        testSeedGenerator);
  }

  @Override public Map<Class<?>, Object> constructorParams() {
    Map<Class<?>, Object> out = super.constructorParams();
    out.put(long.class, DEFAULT_MAX_ENTROPY);
    out.put(Random.class, new AesCounterRandom(getTestSeedGenerator()));
    return out;
  }

  @Override public void testThreadSafety() {
    SeedGenerator testSeedGenerator = getTestSeedGenerator();
    testThreadSafety(functionsForThreadSafetyTest, functionsForThreadSafetyTest,
        seed -> new EntropyBlockingRandomWrapper(new AesCounterRandom(seed), Long.MIN_VALUE,
            testSeedGenerator));
  }

  @Override public void testRepeatability() throws SeedException {
    SeedGenerator testSeedGenerator = getTestSeedGenerator();
    final BaseRandom rng = new EntropyBlockingRandomWrapper(new AesCounterRandom(testSeedGenerator),
        -8 * TEST_BYTES_LENGTH, testSeedGenerator);
    // Create second RNG using same seed.
    final BaseRandom duplicateRNG = createRng(rng.getSeed());
    RandomTestUtils.assertEquivalent(rng, duplicateRNG, TEST_BYTES_LENGTH, "Output mismatch");
  }

  // FIXME: Too slow!
  @Override @Test(timeOut = 90_000L, retryAnalyzer = FlakyRetryAnalyzer.class)
  public void testRandomSeederThreadIntegration() {
    super.testRandomSeederThreadIntegration();
  }
}
