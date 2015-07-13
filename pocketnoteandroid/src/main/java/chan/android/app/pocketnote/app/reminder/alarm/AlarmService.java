package chan.android.app.pocketnote.app.reminder.alarm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import chan.android.app.pocketnote.app.Note;
import chan.android.app.pocketnote.app.reminder.NotificationCenter;

public class AlarmService extends IntentService {

  public AlarmService() {
    super("Alert Service");
  }

  @Override
  public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
  }

  @Override
  public void onHandleIntent(Intent intent) {
  }

  private void sendNotification(Context context, Note note) {
    NotificationCenter center = new NotificationCenter();
    center.notifySticky(context, note);
  }
}
