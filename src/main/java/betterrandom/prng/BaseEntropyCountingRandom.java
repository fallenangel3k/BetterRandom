package betterrandom.prng;

import betterrandom.EntropyCountingRandom;
import betterrandom.seed.RandomSeederThread;
import betterrandom.seed.SeedException;
import betterrandom.seed.SeedGenerator;
import java.util.concurrent.atomic.AtomicLong;

public abstract class BaseEntropyCountingRandom extends BaseRandom implements
    EntropyCountingRandom {

  private static final long serialVersionUID = 1838766748070164286L;
  protected AtomicLong entropyBits = new AtomicLong(0);

  public BaseEntropyCountingRandom(int seedLength) throws SeedException {
    super(seedLength);
  }

  public BaseEntropyCountingRandom(SeedGenerator seedGenerator, int seedLength)
      throws SeedException {
    super(seedGenerator, seedLength);
  }

  public BaseEntropyCountingRandom(byte[] seed) {
    super(seed);
  }

  @Override
  public void setSeed(byte[] seed) {
    super.setSeed(seed);
    entropyBits.updateAndGet(oldCount -> Math.max(oldCount, seed.length * 8));
  }

  @Override
  public long entropyOctets() {
    return entropyBits.get();
  }

  protected final void recordEntropySpent(long bits) {
    if (entropyBits.addAndGet(-bits) <= 0) {
      RandomSeederThread.asyncReseed(this);
    }
  }
}
