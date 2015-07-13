package chan.android.app.pocketnote.util;

public class TextUtility {

  public static String removeWhiteSpaces(String s) {
    return s.replaceAll("\\s+", "");
  }

  public static boolean isNullOrEmpty(String s) {
    return s == null || s.isEmpty();
  }
}
