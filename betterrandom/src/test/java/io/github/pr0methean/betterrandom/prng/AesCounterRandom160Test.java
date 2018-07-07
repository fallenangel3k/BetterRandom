package io.github.pr0methean.betterrandom.prng;

import io.github.pr0methean.betterrandom.seed.SeedException;
import org.testng.annotations.Test;

public class AesCounterRandom160Test extends AesCounterRandom128Test {

  @Override protected int getNewSeedLength(final BaseRandom basePrng) {
    return 20;
  }

  @Override @Test(timeOut = 15000, expectedExceptions = IllegalArgumentException.class)
  public void testSeedTooLong() throws SeedException {
    createRng(
        getTestSeedGenerator().generateSeed(49)); // Should throw an exception.
  }

  @Override public BaseRandom createRng() {
    return new AesCounterRandom(20);
  }
}
