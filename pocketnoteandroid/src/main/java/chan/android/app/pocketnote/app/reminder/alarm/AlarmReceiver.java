package chan.android.app.pocketnote.app.reminder.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import chan.android.app.pocketnote.app.Note;
import chan.android.app.pocketnote.app.NoteDateFormatter;
import chan.android.app.pocketnote.app.db.PocketNoteManager;
import chan.android.app.pocketnote.app.reminder.AbstractReminderScheduler;
import chan.android.app.pocketnote.app.reminder.NoteReminderScheduler;
import chan.android.app.pocketnote.app.reminder.NotificationCenter;
import chan.android.app.pocketnote.app.reminder.Reminder;
import chan.android.app.pocketnote.util.Logger;

public class AlarmReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    final Note note = intent.getParcelableExtra(Note.BUNDLE_KEY);
    Reminder reminder = Reminder.fromJson(note.getReminder());
    long end = reminder.getEnd();
    if (end != 0) {
      long now = System.currentTimeMillis();
      Logger.e("onReceive() now = " + NoteDateFormatter.toString(now));
      Logger.e("onRecieve() end = " + NoteDateFormatter.toString(end));
      if (now > end) {
        Logger.e("onRecieve() remove reminder");
        // Remove reminder from alarm manager
        AbstractReminderScheduler scheduler = NoteReminderScheduler.getScheduler(context);
        scheduler.cancel(note);

        // Remove reminder from database
        PocketNoteManager manager = PocketNoteManager.getPocketNoteManager();
        manager.removeReminder(note);

      } else {
        sendNotification(context, note, reminder);
      }
    } else {
      sendNotification(context, note, reminder);
    }
  }

  private void sendNotification(Context context, Note note, Reminder reminder) {
    NotificationCenter notificationCenter = new NotificationCenter();
    if (reminder.getRepetition() == Reminder.Repetition.ONE_TIME) {
      notificationCenter.notifySticky(context, note);
    } else {
      notificationCenter.notify(context, note);
    }
  }
}
