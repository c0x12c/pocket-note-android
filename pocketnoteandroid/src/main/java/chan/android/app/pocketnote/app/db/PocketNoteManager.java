package chan.android.app.pocketnote.app.db;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import chan.android.app.pocketnote.app.Note;
import chan.android.app.pocketnote.app.reminder.Reminder;
import chan.android.app.pocketnote.util.Logger;
import chan.android.app.pocketnote.util.TextUtility;

import java.util.ArrayList;
import java.util.List;

public class PocketNoteManager implements NoteManager {

  static private final String[] ALL_COLUMNS = new String[]{
    NoteDbTable.COLUMN_ID,
    NoteDbTable.COLUMN_TITLE,
    NoteDbTable.COLUMN_CONTENT,
    NoteDbTable.COLUMN_MODIFIED_TIME,
    NoteDbTable.COLUMN_COLOR,
    NoteDbTable.COLUMN_REMINDER,
    NoteDbTable.COLUMN_LOCKED,
    NoteDbTable.COLUMN_CHECKED,
    NoteDbTable.COLUMN_TRASHED,
    NoteDbTable.COLUMN_DELETED_TIME,
    NoteDbTable.COLUMN_CALENDAR_DAY,
    NoteDbTable.COLUMN_CALENDAR_MONTH,
    NoteDbTable.COLUMN_CALENDAR_YEAR
  };


  private static final int NO = 0;

  private static final int YES = 1;
  private static PocketNoteManager instance;
  private Context context;

  private PocketNoteManager(Context context) {
    this.context = context.getApplicationContext();
  }

  public static void initialize(Context context) {
    instance = new PocketNoteManager(context);
  }

  public static PocketNoteManager getPocketNoteManager() {
    return instance;
  }

  public void add(Note note) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_TITLE, note.getTitle());
    cv.put(NoteDbTable.COLUMN_CONTENT, note.getContent());
    cv.put(NoteDbTable.COLUMN_MODIFIED_TIME, note.getModifiedTime());
    cv.put(NoteDbTable.COLUMN_COLOR, note.getColor());
    cv.put(NoteDbTable.COLUMN_CALENDAR_DAY, note.getDay());
    cv.put(NoteDbTable.COLUMN_CALENDAR_MONTH, note.getMonth());
    cv.put(NoteDbTable.COLUMN_CALENDAR_YEAR, note.getYear());
    context.getContentResolver().insert(NoteContentProvider.CONTENT_URI, cv);
  }

  @Override
  public void changeColor(Note note, int color) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_COLOR, color);
    note.setColor(color);
    context.getContentResolver().update(NoteContentProvider.CONTENT_URI, cv, NoteDbTable.COLUMN_MODIFIED_TIME + "=" + note.getModifiedTime(), null);
  }


  @Override
  public void trash(Note note) {
    long now = System.currentTimeMillis();
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_TRASHED, YES);
    cv.put(NoteDbTable.COLUMN_DELETED_TIME, now);
    note.setTrashed(true);
    note.setDeletedTime(now);
    context.getContentResolver().update(NoteContentProvider.CONTENT_URI, cv, NoteDbTable.COLUMN_MODIFIED_TIME + "=" + note.getModifiedTime(), null);
  }

  @Override
  public void restore(Note note) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_TRASHED, NO);
    cv.put(NoteDbTable.COLUMN_DELETED_TIME, 0);
    note.setTrashed(false);
    note.setDeletedTime(0);
    context.getContentResolver().update(NoteContentProvider.CONTENT_URI, cv, NoteDbTable.COLUMN_MODIFIED_TIME + "=" + note.getModifiedTime(), null);
  }

  @Override
  public void remove(Note note) {
    // We also need to remove reminder if it was a pin to status bar
    if (!TextUtility.isNullOrEmpty(note.getReminder())) {
      Reminder r = Reminder.fromJson(note.getReminder());
      if (r.getType() == Reminder.Type.PIN_TO_STATUS_BAR) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PocketNoteManager noteManager = PocketNoteManager.getPocketNoteManager();
        manager.cancel(noteManager.getId(note));
      }
    }
    context.getContentResolver().delete(NoteContentProvider.CONTENT_URI, NoteDbTable.COLUMN_MODIFIED_TIME + "=" + note.getModifiedTime(), null);
  }

  @Override
  public void edit(Note note) {
    long before = note.getModifiedTime();
    long now = System.currentTimeMillis();
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_TITLE, note.getTitle());
    cv.put(NoteDbTable.COLUMN_CONTENT, note.getContent());
    cv.put(NoteDbTable.COLUMN_MODIFIED_TIME, now);
    cv.put(NoteDbTable.COLUMN_COLOR, note.getColor());
    cv.put(NoteDbTable.COLUMN_TRASHED, note.isTrashed());
    cv.put(NoteDbTable.COLUMN_DELETED_TIME, note.getDeletedTime());
    cv.put(NoteDbTable.COLUMN_CHECKED, note.isChecked());
    cv.put(NoteDbTable.COLUMN_LOCKED, note.isLocked());
    context.getContentResolver().update(NoteContentProvider.CONTENT_URI, cv, NoteDbTable.COLUMN_MODIFIED_TIME + "=" + before, null);
  }

  @Override
  public void lock(Note note) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_LOCKED, YES);
    note.setLocked(true);
    context.getContentResolver().update(NoteContentProvider.CONTENT_URI, cv, NoteDbTable.COLUMN_MODIFIED_TIME + "=" + note.getModifiedTime(), null);
  }

  @Override
  public void unlock(Note note) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_LOCKED, NO);
    note.setLocked(false);
    context.getContentResolver().update(NoteContentProvider.CONTENT_URI, cv, NoteDbTable.COLUMN_MODIFIED_TIME + "=" + note.getModifiedTime(), null);
  }

  @Override
  public void check(Note note) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_CHECKED, YES);
    note.setChecked(true);
    context.getContentResolver().update(NoteContentProvider.CONTENT_URI, cv, NoteDbTable.COLUMN_MODIFIED_TIME + "=" + note.getModifiedTime(), null);
  }

  @Override
  public void uncheck(Note note) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_CHECKED, NO);
    note.setChecked(false);
    context.getContentResolver().update(NoteContentProvider.CONTENT_URI, cv, NoteDbTable.COLUMN_MODIFIED_TIME + "=" + note.getModifiedTime(), null);
  }

  @Override
  public void removeAll() {
    context.getContentResolver().delete(NoteContentProvider.CONTENT_URI, NoteDbTable.COLUMN_TRASHED + "=1", null);
  }

  @Override
  public int getId(Note note) {
    Cursor cursor = context.getContentResolver().query(NoteContentProvider.CONTENT_URI, new String[]{NoteDbTable.COLUMN_ID}, NoteDbTable.COLUMN_MODIFIED_TIME + "=" + note.getModifiedTime(), null, null);
    if (cursor != null) {
      cursor.moveToFirst();
    }
    int id = cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbTable.COLUMN_ID));
    cursor.close();
    return id;
  }

  @Override
  public void addReminder(Note note, String reminder) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_REMINDER, reminder);
    note.setReminder(reminder);
    context.getContentResolver().update(NoteContentProvider.CONTENT_URI, cv, NoteDbTable.COLUMN_MODIFIED_TIME + "=" + note.getModifiedTime(), null);
  }

  @Override
  public void removeReminder(Note note) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_REMINDER, "");
    context.getContentResolver().update(NoteContentProvider.CONTENT_URI, cv, NoteDbTable.COLUMN_MODIFIED_TIME + "=" + note.getModifiedTime(), null);
  }

  @Override
  public List<Note> getNotes(int month, int year) {
    Cursor cursor = context.getContentResolver().query(NoteContentProvider.CONTENT_URI, ALL_COLUMNS,
      NoteDbTable.COLUMN_TRASHED + "=0 AND " +
        NoteDbTable.COLUMN_CALENDAR_MONTH + "=" + month + " AND " +
        NoteDbTable.COLUMN_CALENDAR_YEAR + "=" + year,
      null, null
    );
    return getAllNotes(cursor);
  }

  @Override
  public List<Note> searchInCalendar(String query) {
    Logger.e("query = " + query);
    Cursor cursor = context.getContentResolver().query(NoteContentProvider.CONTENT_URI, ALL_COLUMNS,
      NoteDbTable.COLUMN_TRASHED + "=0 AND " +
        NoteDbTable.COLUMN_CALENDAR_YEAR + "!=-1" + " AND " +
        NoteDbTable.COLUMN_CALENDAR_MONTH + "!=-1" + " AND " +
        NoteDbTable.COLUMN_CALENDAR_DAY + "!=-1 " + " AND " +
        query,
      null, null);
    return getAllNotes(cursor);
  }

  private List<Note> getAllNotes(Cursor cursor) {
    List<Note> notes = new ArrayList<Note>();
    if (cursor != null) {
      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
        notes.add(Note.fromCursor(cursor));
        cursor.moveToNext();
      }
      cursor.close();
    }
    return notes;
  }
}
