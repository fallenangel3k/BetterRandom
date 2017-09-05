package betterrandom.prng.adapter;

import betterrandom.seed.SeedException;
import betterrandom.seed.SeedGenerator;
import betterrandom.util.BinaryUtils;
import com.google.common.base.MoreObjects.ToStringHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;
import java.util.SplittableRandom;
import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.initialization.qual.UnknownInitialization;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

public class SingleThreadSplittableRandomAdapter extends DirectSplittableRandomAdapter {

  private static final long serialVersionUID = -1125374167384636394L;
  private boolean deserializedAndNotUsedSince = false;

  public SingleThreadSplittableRandomAdapter(SeedGenerator seedGenerator) throws SeedException {
    this(seedGenerator.generateSeed(SEED_LENGTH_BYTES));
  }

  @Override
  public ToStringHelper addSubclassFields(ToStringHelper original) {
    return original
        .add("underlying", underlying);
  }

  public SingleThreadSplittableRandomAdapter(byte[] seed) {
    super(seed);
  }

  @Override
  protected SplittableRandom getSplittableRandom() {
    deserializedAndNotUsedSince = false;
    return underlying;
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    setSeed(seed);
    if (!deserializedAndNotUsedSince) {
      underlying = underlying.split(); // Ensures we aren't rewinding
      deserializedAndNotUsedSince = true; // Ensures serializing and deserializing is idempotent
    }
  }

  @Override
  public synchronized void setSeed(@UnknownInitialization SingleThreadSplittableRandomAdapter this,
      long seed) {
    underlying = new SplittableRandom(seed);
    this.seed = BinaryUtils.convertLongToBytes(seed);
  }

  @EnsuresNonNull({"underlying", "this.seed"})
  @Override
  public void setSeedInitial(
      @UnknownInitialization(Random.class) SingleThreadSplittableRandomAdapter this, byte[] seed) {
    underlying = new SplittableRandom(BinaryUtils.convertBytesToLong(seed, 0));
    super.setSeedInitial(seed);
  }
}
