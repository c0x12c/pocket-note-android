package chan.android.app.pocketnote.app;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteDateFormatter {

  private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public static String toString(Date date) {
    return DATE_FORMATTER.format(date);
  }

  public static String toString(long time) {
    return toString(new Date(time));
  }

  public static Date toDate(String dateString) {
    try {
      return DATE_FORMATTER.parse(dateString);
    } catch (ParseException e) {
      return null;
    }
  }

  public static Date toDate(long time) {
    return new Date(time);
  }

  public static long toTime(Date date) {
    return date.getTime();
  }

  public static long toTime(String dateString) {
    return toDate(dateString).getTime();
  }
}
