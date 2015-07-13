package chan.android.app.pocketnote.app.reminder.alarm;

import android.app.AlarmManager;
import android.os.SystemClock;

public class AlarmConstants {

  public static final String LOG_TAG = "AlertAlarm";
  public static final String FORCE_RELOAD = "FORCE_RELOAD";

  // In real life, use AlarmManager.INTERVALs with longer periods of time
  // for dev you can shorten this to 10000 or such, but deals don't change
  // often anyway
  // (better yet, allow user to set and use PreferenceActivity)
  // /public static final long ALARM_INTERVAL = 10000;

  // public static final long ALARM_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

  // 1/2 day
  public static final long ALARM_INTERVAL = AlarmManager.INTERVAL_HALF_DAY;
  public static final long ALARM_TRIGGER_AT_TIME = SystemClock.elapsedRealtime() + 1000;
}
