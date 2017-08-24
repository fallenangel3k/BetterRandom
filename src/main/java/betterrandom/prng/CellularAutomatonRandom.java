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
package betterrandom.prng;

import betterrandom.EntropyCountingRandom;
import betterrandom.RepeatableRandom;
import betterrandom.seed.DefaultSeedGenerator;
import betterrandom.seed.SeedException;
import betterrandom.seed.SeedGenerator;
import betterrandom.util.BinaryUtils;
import java.util.Arrays;
import java.util.Random;
import org.checkerframework.checker.initialization.qual.UnknownInitialization;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

/**
 * <p>Java port of the <a href="http://home.southernct.edu/~pasqualonia1/ca/report.html"
 * target="_top">cellular automaton pseudorandom number generator</a> developed by Tony
 * Pasqualoni.</p>
 *
 * <p><em>NOTE: Instances of this class do not use the seeding mechanism inherited from {@link
 * Random}.  Calls to the {@link #setSeed(long)} method will have no effect.  Instead the seed must
 * be set by a constructor.</em></p>
 *
 * @author Tony Pasqualoni (original C version)
 * @author Daniel Dyer (Java port)
 */
public class CellularAutomatonRandom extends BaseEntropyCountingRandom implements RepeatableRandom {

  private static final long serialVersionUID = 5959251752288589909L;
  private static final int SEED_SIZE_BYTES = 4;
  private static final int AUTOMATON_LENGTH = 2056;
  private static final int[] RNG_RULE =
      {
          100, 75, 16, 3, 229, 51, 197, 118, 24, 62, 198, 11, 141, 152, 241, 188,
          2, 17, 71, 47, 179, 177, 126, 231, 202, 243, 59, 25, 77, 196, 30, 134,
          199, 163, 34, 216, 21, 84, 37, 182, 224, 186, 64, 79, 225, 45, 143, 20,
          48, 147, 209, 221, 125, 29, 99, 12, 46, 190, 102, 220, 80, 215, 242, 105,
          15, 53, 0, 67, 68, 69, 70, 89, 109, 195, 170, 78, 210, 131, 42, 110,
          181, 145, 40, 114, 254, 85, 107, 87, 72, 192, 90, 201, 162, 122, 86, 252,
          94, 129, 98, 132, 193, 249, 156, 172, 219, 230, 153, 54, 180, 151, 83, 214,
          123, 88, 164, 167, 116, 117, 7, 27, 23, 213, 235, 5, 65, 124, 60, 127,
          236, 149, 44, 28, 58, 121, 191, 13, 250, 10, 232, 112, 101, 217, 183, 239,
          8, 32, 228, 174, 49, 113, 247, 158, 106, 218, 154, 66, 226, 157, 50, 26,
          253, 93, 205, 41, 133, 165, 61, 161, 187, 169, 6, 171, 81, 248, 56, 175,
          246, 36, 178, 52, 57, 212, 39, 176, 184, 185, 245, 63, 35, 189, 206, 76,
          104, 233, 194, 19, 43, 159, 108, 55, 200, 155, 14, 74, 244, 255, 222, 207,
          208, 137, 128, 135, 96, 144, 18, 95, 234, 139, 173, 92, 1, 203, 115, 223,
          130, 97, 91, 227, 146, 4, 31, 120, 211, 38, 22, 138, 140, 237, 238, 251,
          240, 160, 142, 119, 73, 103, 166, 33, 148, 9, 111, 136, 168, 150, 82, 204,
          100, 75, 16, 3, 229, 51, 197, 118, 24, 62, 198, 11, 141, 152, 241, 188,
          2, 17, 71, 47, 179, 177, 126, 231, 202, 243, 59, 25, 77, 196, 30, 134,
          199, 163, 34, 216, 21, 84, 37, 182, 224, 186, 64, 79, 225, 45, 143, 20,
          48, 147, 209, 221, 125, 29, 99, 12, 46, 190, 102, 220, 80, 215, 242, 105,
          15, 53, 0, 67, 68, 69, 70, 89, 109, 195, 170, 78, 210, 131, 42, 110,
          181, 145, 40, 114, 254, 85, 107, 87, 72, 192, 90, 201, 162, 122, 86, 252,
          94, 129, 98, 132, 193, 249, 156, 172, 219, 230, 153, 54, 180, 151, 83, 214,
          123, 88, 164, 167, 116, 117, 7, 27, 23, 213, 235, 5, 65, 124, 60, 127,
          236, 149, 44, 28, 58, 121, 191, 13, 250, 10, 232, 112, 101, 217, 183, 239,
          8, 32, 228, 174, 49, 113, 247, 158, 106, 218, 154, 66, 226, 157, 50, 26,
          253, 93, 205, 41, 133, 165, 61, 161, 187, 169, 6, 171, 81, 248, 56, 175,
          246, 36, 178, 52, 57, 212, 39, 176, 184, 185, 245, 63, 35, 189, 206, 76,
          104, 233, 194, 19, 43, 159, 108, 55, 200, 155, 14, 74, 244, 255, 222, 207,
          208, 137, 128, 135, 96, 144, 18, 95, 234, 139, 173, 92, 1, 203, 115, 223,
          130, 97, 91, 227, 146, 4, 31, 120, 211, 38, 22, 138, 140, 237, 238, 251,
          240, 160, 142, 119, 73, 103, 166, 33, 148, 9, 111, 136, 168, 150, 82
      };
  private transient int[] cells;

  private transient int currentCellIndex;

  public CellularAutomatonRandom() throws SeedException {
    this(DefaultSeedGenerator.INSTANCE);
  }

  public CellularAutomatonRandom(SeedGenerator seedGenerator) throws SeedException {
    this(seedGenerator.generateSeed(SEED_SIZE_BYTES));
  }

  /**
   * Creates an RNG and seeds it with the specified seed data.
   *
   * @param seed The seed data used to initialise the RNG.
   */
  public CellularAutomatonRandom(byte[] seed) {
    super(seed);
    initSubclassTransientFields();
  }

  private static int convertCellsToInt(int[] cells, int offset) {
    return cells[offset]
        + (cells[offset + 1] << 8)
        + (cells[offset + 2] << 16)
        + (cells[offset + 3] << 24);
  }

  @EnsuresNonNull("cells")
  @RequiresNonNull({"seed", "lock"})
  protected void copySeedToCellsAndPreEvolve(@UnknownInitialization CellularAutomatonRandom this) {
    lock.lock();
    try {
      cells = new int[AUTOMATON_LENGTH];
      // Set initial cell states using seed.
      cells[AUTOMATON_LENGTH - 1] = seed[0] + 128;
      cells[AUTOMATON_LENGTH - 2] = seed[1] + 128;
      cells[AUTOMATON_LENGTH - 3] = seed[2] + 128;
      cells[AUTOMATON_LENGTH - 4] = seed[3] + 128;
      currentCellIndex = AUTOMATON_LENGTH - 1;

      int seedAsInt = BinaryUtils.convertBytesToInt(seed, 0);
      if (seedAsInt != 0xFFFFFFFF) {
        seedAsInt++;
      }
      for (int i = 0; i < AUTOMATON_LENGTH - 4; i++) {
        cells[i] = 0x000000FF & (seedAsInt >> (i % 32));
      }

      // Evolve automaton before returning integers.
      for (int i = 0; i < AUTOMATON_LENGTH * AUTOMATON_LENGTH / 4; i++) {
        internalNext(32);
      }
    } finally {
      lock.unlock();
    }
  }

  @EnsuresNonNull("cells")
  @RequiresNonNull({"lock", "seed"})
  @Override
  protected void initSubclassTransientFields(
      @UnknownInitialization CellularAutomatonRandom this) {
    cells = new int[AUTOMATON_LENGTH];
    currentCellIndex = AUTOMATON_LENGTH - 1;
    lock.lock();
    try {
      setSeed(seed);
      copySeedToCellsAndPreEvolve();
    } finally {
      lock.unlock();
    }
  }

  @RequiresNonNull("cells")
  private int internalNext(@UnknownInitialization CellularAutomatonRandom this, int bits) {
    int result;
    // Set cell addresses using address of current cell.
    int cellC = currentCellIndex - 1;
    int cellB = cellC - 1;

    // Update cell states using rule table.
    cells[currentCellIndex] = RNG_RULE[cells[cellC] + cells[currentCellIndex]];
    cells[cellC] = RNG_RULE[cells[cellB] + cells[cellC]];
    int cellA = cellB - 1;
    cells[cellB] = RNG_RULE[cells[cellA] + cells[cellB]];

    // Update the state of cellA and shift current cell to the left by 4 bytes.
    if (cellA == 0) {
      cells[0] = RNG_RULE[cells[0]];
      currentCellIndex = AUTOMATON_LENGTH - 1;
    } else {
      cells[cellA] = RNG_RULE[cells[cellA - 1] + cells[cellA]];
      currentCellIndex -= 4;
    }
    result = convertCellsToInt(cells, cellA);
    return result >>> (32 - bits);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int next(int bits) {
    lock.lock();
    try {
      return internalNext(bits);
    } finally {
      recordEntropySpent(bits);
      lock.unlock();
    }
  }

  @SuppressWarnings("NonFinalFieldReferenceInEquals")
  @Override
  public boolean equals(Object o) {
    return o instanceof CellularAutomatonRandom
        && Arrays.equals(seed, ((CellularAutomatonRandom) o).seed);
  }

  @SuppressWarnings("NonFinalFieldReferencedInHashCode")
  @Override
  public int hashCode() {
    return Arrays.hashCode(seed);
  }

  @SuppressWarnings("NonSynchronizedMethodOverridesSynchronizedMethod")
  @Override
  public void setSeed(@UnknownInitialization CellularAutomatonRandom this, long seed) {
    if (lock == null || this.seed == null) {
      // setSeed can't work until seed array and lock are allocated
      return;
    }
    lock.lock();
    try {
      this.seed[0] = (byte) (seed);
      this.seed[1] = (byte) (seed >> 8);
      this.seed[2] = (byte) (seed >> 16);
      this.seed[3] = (byte) (seed >> 24);
      currentCellIndex = AUTOMATON_LENGTH - 1;
      copySeedToCellsAndPreEvolve();
    } finally {
      lock.unlock();
    }
  }

  @Override
  @RequiresNonNull("lock")
  @SuppressWarnings("contracts.precondition.override.invalid")
  public void setSeed(@UnknownInitialization CellularAutomatonRandom this, byte[] seed) {
    if (seed.length != SEED_SIZE_BYTES) {
      throw new IllegalArgumentException("Cellular Automaton RNG requires a 32-bit (4-byte) seed.");
    }
    lock.lock();
    try {
      if (this.seed == null) {
        this.seed = new byte[SEED_SIZE_BYTES];
      }
      System.arraycopy(seed, 0, this.seed, 0, 4);
      copySeedToCellsAndPreEvolve();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public int getNewSeedLength() {
    return SEED_SIZE_BYTES;
  }
}
