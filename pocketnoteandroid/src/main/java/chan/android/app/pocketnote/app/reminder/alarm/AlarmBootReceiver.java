package chan.android.app.pocketnote.app.reminder.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmBootReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0);
    // Use inexact repeating which is easier on battery (system can phase events and not wake at exact times)
    am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmConstants.ALARM_TRIGGER_AT_TIME, AlarmConstants.ALARM_INTERVAL, pendingIntent);
  }
}
