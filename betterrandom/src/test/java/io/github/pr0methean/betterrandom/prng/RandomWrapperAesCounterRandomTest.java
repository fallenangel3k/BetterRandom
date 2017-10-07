package io.github.pr0methean.betterrandom.prng;

import com.google.common.collect.ImmutableMap;
import io.github.pr0methean.betterrandom.TestUtils;
import io.github.pr0methean.betterrandom.seed.DefaultSeedGenerator;
import io.github.pr0methean.betterrandom.seed.SeedException;
import io.github.pr0methean.betterrandom.seed.SeedGenerator;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import org.testng.annotations.Test;

public class RandomWrapperAesCounterRandomTest extends AesCounterRandom128Test {

  @Override
  protected Class<? extends BaseRandom> getClassUnderTest() {
    return RandomWrapper.class;
  }

  @Override
  @Test(enabled = false)
  public void testAllPublicConstructors()
      throws SeedException, IllegalAccessException, InstantiationException, InvocationTargetException {
    // No-op: redundant to super insofar as it works.
  }

  @Override
  protected RandomWrapper tryCreateRng() throws SeedException {
    return new RandomWrapper(new AesCounterRandom());
  }

  @Override
  protected RandomWrapper createRng(final byte[] seed) throws SeedException {
    return new RandomWrapper(new AesCounterRandom(seed));
  }
}
