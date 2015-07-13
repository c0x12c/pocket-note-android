package chan.android.app.pocketnote.util;

import org.joda.time.DateTime;

public class DateTimeUtility {

  private static final String[] WEEKDAY = new String[]{
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday",
    "Sunday",
  };
  private static final String[] MONTH = new String[]{
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"
  };

  private DateTimeUtility() {
    throw new AssertionError("Oh come on");
  }

  public static String getReadableWeekDay(DateTime date) {
    return WEEKDAY[date.getDayOfWeek() - 1];
  }

  public static String getReadableWeekDay(int day) {
    return WEEKDAY[day - 1];
  }

  public static String getReadableMonth(int month) {
    return MONTH[month - 1];
  }

  public static int getNextDay(int day) {
    return (day % 7) + 1;
  }

  public static String[] getAllDaysCycleFrom(DateTime date) {
    int today = date.getDayOfWeek();
    int tomorrow = DateTimeUtility.getNextDay(today);
    String[] result = new String[8];
    result[0] = "Today - " + getReminderReadableDate(date);
    result[1] = "Tomorrow - " + getReminderReadableDate(date.plusDays(1));
    for (int i = 2; i < 7; ++i) {
      tomorrow = DateTimeUtility.getNextDay(tomorrow);
      result[i] = DateTimeUtility.getReadableWeekDay(tomorrow) + " - " + getReminderReadableDate(date.plusDays(i));
    }
    result[7] = "Specific date";
    return result;
  }

  public static String getReminderReadableDate(DateTime date) {
    StringBuilder sb = new StringBuilder();
    sb.append(getReadableWeekDay(date));
    sb.append(", ");
    sb.append(getReadableMonth(date.getMonthOfYear()));
    sb.append(" ");
    sb.append(date.getDayOfMonth());
    sb.append(" ");
    sb.append(date.getYear());
    return sb.toString();
  }

  public static String getReminderReadableTime(int hour, int minute) {
    StringBuilder sb = new StringBuilder();
    String period = " AM";
    if (hour == 0) {
      hour = 12;
      period = " AM";
    } else if (hour == 12) {
      period = " PM";
    } else if (13 <= hour && hour <= 23) {
      period = " PM";
      hour = hour % 12;
    }

    if (hour < 10) {
      sb.append("0");
    }

    sb.append(hour);
    sb.append(":");

    if (minute < 10) {
      sb.append("0");
    }

    sb.append(minute);
    sb.append(period);
    return sb.toString();
  }
}
