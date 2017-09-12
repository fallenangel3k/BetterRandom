// ============================================================================
//   Copyright 2006-2012 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package io.github.pr0methean.betterrandom.seed;

import java.security.SecureRandom;

/**
 * <p>{@link io.github.pr0methean.betterrandom.seed.SeedGenerator} implementation that uses Java's
 * bundled {@link java.security.SecureRandom} RNG to generate random seed data.</p> <p>The advantage
 * of using SecureRandom for seeding but not as the primary RNG is that we can use it to seed RNGs
 * that are much faster than SecureRandom.</p> <p>This is the only seeding strategy that is
 * guaranteed to work on all platforms and therefore is provided as a fall-back option should none
 * of the other provided {@link SeedGenerator} implementations be usable.</p>
 *
 * @author Daniel Dyer
 * @version $Id: $Id
 */
public enum SecureRandomSeedGenerator implements SeedGenerator {

  SECURE_RANDOM_SEED_GENERATOR;

  /** Constant {@code SOURCE} */
  private static final SecureRandom SOURCE = new SecureRandom();

  /**
   * <p>generateSeed.</p>
   *
   * @param length a int.
   * @return an array of byte.
   * @throws io.github.pr0methean.betterrandom.seed.SeedException if any.
   */
  public byte[] generateSeed(final int length) throws SeedException {
    return SOURCE.generateSeed(length);
  }

  @Override
  public void generateSeed(final byte[] output) throws SeedException {
    System.arraycopy(SOURCE.generateSeed(output.length), 0, output, 0, output.length);
  }

  @Override
  public String toString() {
    return "java.security.SecureRandom";
  }
}
