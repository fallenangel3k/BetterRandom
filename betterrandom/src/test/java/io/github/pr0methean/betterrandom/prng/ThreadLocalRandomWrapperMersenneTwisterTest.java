package io.github.pr0methean.betterrandom.prng;

import static org.testng.Assert.assertEquals;

import io.github.pr0methean.betterrandom.seed.SeedException;
import io.github.pr0methean.betterrandom.util.CloneViaSerialization;
import java.io.Serializable;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import org.testng.annotations.Test;

public class ThreadLocalRandomWrapperMersenneTwisterTest extends ThreadLocalRandomWrapperTest {

  @Override public Map<Class<?>, Object> constructorParams() {
    final Map<Class<?>, Object> params = super.constructorParams();
    params.put(Supplier.class, (Supplier<BaseRandom>) MersenneTwisterRandom::new);
    params
        .put(Function.class, (Function<byte[], BaseRandom>) MersenneTwisterRandom::new);
    return params;
  }

  @Override protected BaseRandom createRng() throws SeedException {
    return new ThreadLocalRandomWrapper(
        (Serializable & Supplier<BaseRandom>) MersenneTwisterRandom::new);
  }
}