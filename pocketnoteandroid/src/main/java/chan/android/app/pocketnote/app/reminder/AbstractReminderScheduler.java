package chan.android.app.pocketnote.app.reminder;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import chan.android.app.pocketnote.app.Note;

/**
 * Any note event scheduler must implement this interface
 */
public abstract class AbstractReminderScheduler {

  protected Context context;

  protected AlarmManager alarmManager;

  protected PendingIntent alarmIntent;

  public AbstractReminderScheduler(Context context) {
    this.context = context;
    this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
  }

  public abstract void schedule(final Note note);

  public abstract void cancel(final Note note);
}
