package chan.android.app.pocketnote.app.reminder;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import chan.android.app.pocketnote.app.AppResources;
import chan.android.app.pocketnote.app.Note;
import chan.android.app.pocketnote.app.db.PocketNoteManager;
import chan.android.app.pocketnote.app.notes.EditNoteActivity;

public class NotificationCenter {

  private static final String NOTIFICATION_TITLE = "PocketNote reminder";

  private static NotificationCenter center;

  public NotificationCenter() {
    // ???
  }

  public void notifySticky(Context context, Note note) {
    final Notification n = buildNotification(context, note);
    n.flags |= Notification.FLAG_NO_CLEAR;
    final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    manager.notify(PocketNoteManager.getPocketNoteManager().getId(note), n);
  }

  public void notify(Context context, Note note) {
    final Notification n = buildNotification(context, note);
    final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    manager.notify(PocketNoteManager.getPocketNoteManager().getId(note), n);
  }

  private Notification buildNotification(Context context, Note note) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
    builder.setContentTitle(note.getTitle());
    builder.setContentText(note.getContent());
    builder.setTicker(NOTIFICATION_TITLE);
    builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
    builder.setSmallIcon(AppResources.getDrawable(note.getColor()));

    Intent intent = new Intent(context, EditNoteActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    intent.putExtra(Note.BUNDLE_KEY, note);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
    stackBuilder.addParentStack(EditNoteActivity.class);
    stackBuilder.addNextIntent(intent);

    PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(pendingIntent);
    return builder.build();
  }
}
