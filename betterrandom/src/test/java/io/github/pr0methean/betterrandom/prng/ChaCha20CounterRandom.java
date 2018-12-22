package io.github.pr0methean.betterrandom.prng;

import com.google.common.base.MoreObjects;
import io.github.pr0methean.betterrandom.util.BinaryUtils;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.slf4j.LoggerFactory;

/**
 * A second subclass of {@link CipherCounterRandom}, used to test that abstract class for
 * AES-specific behavior (since it was split off as a parent of {@link AesCounterRandom}).
 */
public class ChaCha20CounterRandom extends CipherCounterRandom {
  private static final int LARGE_KEY_LENGTH = 32;
  private static final int SMALL_KEY_LENGTH = 16;
  private static final String ALGORITHM_MODE = "CHACHA/ECB/NoPadding";
  /**
   * I know this to be a valid IV because I got it when using BouncyCastle's ChaCha through JCE.
   */
  private static final byte[] FIXED_IV = {125, 13, -27, -122, 104, -89, 127, 81};
  @SuppressWarnings("CanBeFinal") private static int MAX_KEY_LENGTH_BYTES = 0;

  static {
    try {
      MAX_KEY_LENGTH_BYTES = Cipher.getMaxAllowedKeyLength(ALGORITHM_MODE) / 8;
    } catch (final GeneralSecurityException e) {
      throw new InternalError(e);
    }
    LoggerFactory.getLogger(AesCounterRandom.class)
        .info("Maximum allowed key length for ChaCha is {} bytes", MAX_KEY_LENGTH_BYTES);
    MAX_KEY_LENGTH_BYTES = Math.min(MAX_KEY_LENGTH_BYTES, LARGE_KEY_LENGTH);
  }

  // WARNING: Don't initialize any instance fields at declaration; they may be initialized too late!
  @SuppressWarnings("InstanceVariableMayNotBeInitializedByReadObject")
  private transient ChaChaEngine cipher;

  public ChaCha20CounterRandom(byte[] seed) {
    super(seed);
  }

  @Override
  public int getMaxKeyLengthBytes() {
    return MAX_KEY_LENGTH_BYTES;
  }

  @Override
  protected int getKeyLength(int inputLength) {
    return inputLength >= LARGE_KEY_LENGTH ? LARGE_KEY_LENGTH : SMALL_KEY_LENGTH;
  }

  @Override
  protected int getMinSeedLength() {
    return 16;
  }

  @Override
  public int getCounterSizeBytes() {
    return 64;
  }

  @Override
  public int getBlocksAtOnce() {
    return 1;
  }

  @Override
  protected MessageDigest createHash() {
    return new SHA3.Digest256();
  }

  @Override
  protected void createCipher() {
    cipher = new ChaChaEngine(20);
  }

  @Override
  protected void setKey(byte[] key) {
    cipher.init(true, new ParametersWithIV(new KeyParameter(key), FIXED_IV));
  }

  @Override public MoreObjects.ToStringHelper addSubclassFields(final MoreObjects.ToStringHelper original) {
    return original.add("counter", BinaryUtils.convertBytesToHexString(counter))
        .add("cipher", cipher)
        .add("index", index);
  }

  @Override
  protected void doCipher(byte[] input, byte[] output) {
    cipher.processBytes(input, 0, getBytesAtOnce(), output, 0);
  }
}
