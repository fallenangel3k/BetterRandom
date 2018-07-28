package io.github.pr0methean.betterrandom.prng

import io.github.pr0methean.betterrandom.TestUtils.assertGreaterOrEqual
import io.github.pr0methean.betterrandom.TestUtils.isAppveyor

import io.github.pr0methean.betterrandom.TestingDeficiency
import io.github.pr0methean.betterrandom.prng.RandomTestUtils.EntropyCheckMode
import io.github.pr0methean.betterrandom.seed.DefaultSeedGenerator
import io.github.pr0methean.betterrandom.seed.RandomSeederThread
import io.github.pr0methean.betterrandom.seed.SecureRandomSeedGenerator
import io.github.pr0methean.betterrandom.seed.SeedException
import io.github.pr0methean.betterrandom.seed.SeedGenerator
import java.io.Serializable
import java.util.Arrays
import java.util.Random
import java.util.function.Supplier
import org.testng.SkipException
import org.testng.annotations.Test

@Test(testName = "ReseedingThreadLocalRandomWrapper:MersenneTwisterRandom")
class ReseedingThreadLocalRandomWrapperMersenneTwisterTest : ThreadLocalRandomWrapperMersenneTwisterTest() {

    private val mtSupplier: Supplier<out BaseRandom>

    protected override// FIXME: Statistical tests often fail when using SEMIFAKE_SEED_GENERATOR
    val testSeedGenerator: SeedGenerator
        @TestingDeficiency
        get() = SecureRandomSeedGenerator.SECURE_RANDOM_SEED_GENERATOR

    protected override val entropyCheckMode: EntropyCheckMode
        get() = EntropyCheckMode.LOWER_BOUND

    protected override val classUnderTest: Class<out BaseRandom>
        get() = ReseedingThreadLocalRandomWrapper::class.java

    init {
        // Must be done first, or else lambda won't be serializable.
        val seedGenerator = testSeedGenerator

        mtSupplier = { MersenneTwisterRandom(seedGenerator) } as Serializable
    }

    @Throws(SeedException::class)
    override fun testWrapLegacy() {
        ReseedingThreadLocalRandomWrapper.wrapLegacy(LongFunction<Random> { Random(it) }, testSeedGenerator).nextInt()
    }

    @Test
    override fun testReseeding() {
        if (isAppveyor) {
            throw SkipException("This test often fails spuriously on AppVeyor") // FIXME
        }
        val rng = ReseedingThreadLocalRandomWrapper(testSeedGenerator, mtSupplier)
        rng.nextLong()
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }

        val oldSeed = rng.getSeed()
        var newSeed: ByteArray
        RandomSeederThread.setPriority(testSeedGenerator, Thread.MAX_PRIORITY)
        try {
            do {
                rng.nextLong()
                Thread.sleep(10)
                newSeed = rng.getSeed()
            } while (Arrays.equals(newSeed, oldSeed))
            Thread.sleep((if (isAppveyor) 1000 else 100).toLong())
            assertGreaterOrEqual(rng.getEntropyBits(), newSeed.size * 8L - 1)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        } finally {
            RandomSeederThread.setPriority(testSeedGenerator, Thread.NORM_PRIORITY)
        }
    }

    /** Assertion-free since reseeding may cause divergent output.  */
    @Test(timeOut = 10000)
    override fun testSetSeedLong() {
        createRng().setSeed(0x0123456789ABCDEFL)
    }

    /** Test for crashes only, since setSeed is a no-op.  */
    @Test
    @Throws(SeedException::class)
    override fun testSetSeedAfterNextLong() {
        val prng = createRng()
        prng.nextLong()
        prng.setSeed(testSeedGenerator.generateSeed(16))
        prng.nextLong()
    }

    /** Test for crashes only, since setSeed is a no-op.  */
    @Test
    @Throws(SeedException::class)
    override fun testSetSeedAfterNextInt() {
        val prng = createRng()
        prng.nextInt()
        prng.setSeed(testSeedGenerator.generateSeed(16))
        prng.nextInt()
    }

    @Throws(SeedException::class)
    override fun createRng(): BaseRandom {
        return ReseedingThreadLocalRandomWrapper(testSeedGenerator,
                Supplier<BaseRandom> { MersenneTwisterRandom() } as Serializable)
    }
}
