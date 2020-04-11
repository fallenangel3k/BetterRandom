package io.github.pr0methean.betterrandom.seed;

import java.util.UUID;
import org.testng.SkipException;

/**
 * Utility methods used in {@link RandomDotOrgApi2ClientHermeticTest} and
 * {@link RandomDotOrgApi2ClientLiveTest}.
 */
public enum RandomDotOrgUtils {
  ;

  public static boolean haveApiKey() {
    return System.getenv("RANDOM_DOT_ORG_KEY") != null;
  }

}
