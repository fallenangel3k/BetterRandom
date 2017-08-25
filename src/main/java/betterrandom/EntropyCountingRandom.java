package betterrandom;

/**
 * A {@link java.util.Random} that can track its inflow and outflow of entropy (assuming that seeds
 * have entropy equal to their length) so we can decide whether or not it needs reseeding again
 * yet.
 */
public interface EntropyCountingRandom {

  /**
   * @return The current amount of entropy (bits seeded with or bits of internal state, whichever
   * was less at the time, minus bits returned).
   */
  long entropyBits();
}
