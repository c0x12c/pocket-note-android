package chan.android.app.pocketnote.util;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtility {

  private HashUtility() {
    throw new AssertionError(HashUtility.class.getSimpleName() + " can't be instantiated.");
  }

  public static String md5(String s) {
    return hash(s, "MD5");
  }

  public static String sha1(String s) {
    return hash(s, "SHA1");
  }

  private static String hash(String s, String algorithm) {
    String result = s;
    try {
      MessageDigest digest = MessageDigest.getInstance(algorithm);
      byte[] bytes = digest.digest(s.getBytes());
      BigInteger biggie = new BigInteger(1, bytes);
      result = String.format("%0" + (bytes.length << 1) + "x", biggie);
    } catch (NoSuchAlgorithmException e) {
      // No way...
    }
    return result;
  }
}
