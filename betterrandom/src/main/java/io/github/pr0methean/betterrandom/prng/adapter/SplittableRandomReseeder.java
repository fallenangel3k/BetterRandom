package io.github.pr0methean.betterrandom.prng.adapter;

import io.github.pr0methean.betterrandom.util.LogPreFormatter;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.SplittableRandom;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

/**
 * Utility class to re-seed a {@link java.util.SplittableRandom} instance using reflection and
 * {@link sun.misc.Unsafe#putLongVolatile(Object, long, long)}. Since SplittableRandom doesn't
 * support reseeding through its public API, this is the only way a {@link
 * ReseedingSplittableRandomAdapter} can avoid a ridiculous load on the garbage collector.
 */
@SuppressWarnings("argument.type.incompatible") // Field.get(null) is OK when the field is static!
public final class SplittableRandomReseeder {
  private static final LogPreFormatter LOG = new LogPreFormatter(SplittableRandomReseeder.class);
  private static final Objenesis OBJENESIS = new ObjenesisStd();
  @Nullable private static MethodHandle PUT_LONG_VOLATILE;
  private static long GAMMA_FIELD_OFFSET;
  private static long SEED_FIELD_OFFSET;
  private static long GOLDEN_GAMMA;
  private static boolean CAN_RESEED_REFLECTIVELY;

  private SplittableRandomReseeder() {}

  static {
    try {
      Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
      Object unsafe;
      try {
        Field unsafeInstance = unsafeClass.getDeclaredField("theUnsafe");
        unsafeInstance.setAccessible(true);
        unsafe = unsafeInstance.get(null);
      } catch (NoSuchFieldException | IllegalAccessException e) {
        unsafe = OBJENESIS.newInstance(unsafeClass);
      }
      Method getFieldOffset = unsafeClass.getDeclaredMethod("objectFieldOffset", Field.class);
      GAMMA_FIELD_OFFSET = (long) (getFieldOffset.invoke(unsafe, SplittableRandom.class.getDeclaredField("gamma")));
      SEED_FIELD_OFFSET = (long) (getFieldOffset.invoke(unsafe, SplittableRandom.class.getDeclaredField("seed")));
      Field goldenGammaField = SplittableRandom.class.getDeclaredField("GOLDEN_GAMMA");
      goldenGammaField.setAccessible(true);
      GOLDEN_GAMMA = (long) (goldenGammaField.get(null));
      Method putVolatileLong = unsafeClass.getDeclaredMethod("putLongVolatile", Object.class, long.class, long.class);
      putVolatileLong.setAccessible(true);
      PUT_LONG_VOLATILE = MethodHandles.lookup().unreflect(putVolatileLong).bindTo(unsafe);
      CAN_RESEED_REFLECTIVELY = true;
    } catch (ReflectiveOperationException e) {
      LOG.error("Can't reflectively reseed SplittableRandom instances: %s", e);
      CAN_RESEED_REFLECTIVELY = false;
    }
  }

  public static SplittableRandom reseed(SplittableRandom original, long seed) {
    if (CAN_RESEED_REFLECTIVELY && PUT_VOLATILE_LONG != null && original != null) {
      try {
        PUT_LONG_VOLATILE.invoke(original, SEED_FIELD_OFFSET, seed);
        PUT_LONG_VOLATILE.invoke(original, GAMMA_FIELD_OFFSET, GOLDEN_GAMMA);
        return original;
      } catch (Throwable throwable) {
        throw new RuntimeException(throwable);
      }
    } else {
      return new SplittableRandom(seed);
    }
  }
}
