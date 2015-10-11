package chan.android.app.pocketnote.app.db;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import chan.android.app.pocketnote.app.Note;
import chan.android.app.pocketnote.app.reminder.Reminder;
import chan.android.app.pocketnote.util.TextUtility;
import chan.android.app.pocketnote.util.TimeProvider;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class NoteResourceManager implements NoteResource {

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

  private ContentResolver resolver;

  private Context context;

  private TimeProvider timeProvider;

  public NoteResourceManager(Context context, TimeProvider timeProvider) {
    this.context = context.getApplicationContext();
    this.resolver = context.getContentResolver();
    this.timeProvider = timeProvider;
  }

  @Override
  public Observable<Note> add(final Note note) {
    return Observable.create(new Observable.OnSubscribe<Note>() {
      @Override
      public void call(Subscriber<? super Note> subscriber) {
        try {
          ContentValues cv = new ContentValues();
          cv.put(NoteDbTable.COLUMN_TITLE, note.getTitle());
          cv.put(NoteDbTable.COLUMN_CONTENT, note.getContent());
          cv.put(NoteDbTable.COLUMN_MODIFIED_TIME, note.getModifiedTime());
          cv.put(NoteDbTable.COLUMN_COLOR, note.getColor());
          cv.put(NoteDbTable.COLUMN_CALENDAR_DAY, note.getDay());
          cv.put(NoteDbTable.COLUMN_CALENDAR_MONTH, note.getMonth());
          cv.put(NoteDbTable.COLUMN_CALENDAR_YEAR, note.getYear());
          Uri uri = resolver.insert(NoteContentProvider.CONTENT_URI, cv);
          Cursor cursor = resolver.query(uri, NoteContentProvider.COLUMNS, null, null, null);
          subscriber.onNext(fromCursor(cursor));
          subscriber.onCompleted();
        } catch (Exception e) {
          subscriber.onError(e);
        }
      }
    })
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread());
  }

  @Override
  public Observable<Note> changeColor(Note note, int color) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_COLOR, color);
    note.setColor(color);
    return update(note, cv);
  }


  @Override
  public Observable<Note> trash(final Note note) {
    final long now = timeProvider.provideCurrentMilliseconds();
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_TRASHED, YES);
    cv.put(NoteDbTable.COLUMN_DELETED_TIME, now);
    note.setTrashed(true);
    note.setDeletedTime(now);
    return update(note, cv);
  }

  @Override
  public Observable<Note> restore(final Note note) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_TRASHED, NO);
    cv.put(NoteDbTable.COLUMN_DELETED_TIME, 0);
    note.setTrashed(false);
    note.setDeletedTime(0);
    return update(note, cv);
  }

  @Override
  public Observable<Boolean> remove(final Note note) {
    return Observable.create(new Observable.OnSubscribe<Boolean>() {
      @Override
      public void call(Subscriber<? super Boolean> subscriber) {
        // We also need to remove reminder if it was a pin to status bar
        try {
          if (!TextUtility.isNullOrEmpty(note.getReminder())) {
            Reminder r = Reminder.fromJson(note.getReminder());
            if (r.getType() == Reminder.Type.PIN_TO_STATUS_BAR) {
              NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
              manager.cancel(getId(note).toBlocking().first());
            }
          }
          final int r = resolver.delete(NoteContentProvider.CONTENT_URI, NoteDbTable.COLUMN_MODIFIED_TIME + "=" + note.getModifiedTime(), null);
          subscriber.onNext(r == 1);
          subscriber.onCompleted();
        } catch (Exception e) {
          subscriber.onError(e);
        }
      }
    })
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread());
  }

  @Override
  public Observable<Note> edit(final Note note) {
    long now = timeProvider.provideCurrentMilliseconds();
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_TITLE, note.getTitle());
    cv.put(NoteDbTable.COLUMN_CONTENT, note.getContent());
    cv.put(NoteDbTable.COLUMN_MODIFIED_TIME, now);
    cv.put(NoteDbTable.COLUMN_COLOR, note.getColor());
    cv.put(NoteDbTable.COLUMN_TRASHED, note.isTrashed());
    cv.put(NoteDbTable.COLUMN_DELETED_TIME, note.getDeletedTime());
    cv.put(NoteDbTable.COLUMN_CHECKED, note.isChecked());
    cv.put(NoteDbTable.COLUMN_LOCKED, note.isLocked());
    return update(note, cv);
  }

  @Override
  public Observable<Note> lock(final Note note) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_LOCKED, YES);
    note.setLocked(true);
    return update(note, cv);
  }

  @Override
  public Observable<Note> unlock(final Note note) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_LOCKED, NO);
    note.setLocked(false);
    return update(note, cv);
  }

  @Override
  public Observable<Note> check(final Note note) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_CHECKED, YES);
    note.setChecked(true);
    return update(note, cv);
  }

  @Override
  public Observable<Note> uncheck(final Note note) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_CHECKED, NO);
    note.setChecked(false);
    return update(note, cv);
  }

  @Override
  public Observable<Integer> removeAll() {
    return Observable.create(new Observable.OnSubscribe<Integer>() {
      @Override
      public void call(Subscriber<? super Integer> subscriber) {
        try {
          int count = resolver.delete(NoteContentProvider.CONTENT_URI, NoteDbTable.COLUMN_TRASHED + "=1", null);
          subscriber.onNext(count);
          subscriber.onCompleted();
        } catch (Exception e) {
          subscriber.onError(e);
        }
      }
    })
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread());
  }

  @Override
  public Observable<Integer> getId(final Note note) {
    return Observable.create(new Observable.OnSubscribe<Integer>() {
      @Override
      public void call(Subscriber<? super Integer> subscriber) {
        try {
          final Cursor cursor = resolver.query(
            NoteContentProvider.CONTENT_URI,
            new String[]{NoteDbTable.COLUMN_ID},
            NoteDbTable.COLUMN_MODIFIED_TIME + "=" + note.getModifiedTime(),
            null,
            null
          );
          if (cursor == null) {
            subscriber.onNext(-1);
            subscriber.onCompleted();
          }
          cursor.moveToFirst();
          int id = cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbTable.COLUMN_ID));
          cursor.close();
          subscriber.onNext(id);
          subscriber.onCompleted();
        } catch (Exception e) {
          subscriber.onError(e);
        }
      }
    })
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread());
  }

  @Override
  public Observable<Note> addReminder(Note note, String reminder) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_REMINDER, reminder);
    note.setReminder(reminder);
    return update(note, cv);
  }

  @Override
  public Observable<Note> removeReminder(Note note) {
    ContentValues cv = new ContentValues();
    cv.put(NoteDbTable.COLUMN_REMINDER, "");
    return update(note, cv);
  }

  @Override
  public Observable<List<Note>> getNotes(final int month, final int year) {
    return Observable.create(new Observable.OnSubscribe<List<Note>>() {
      @Override
      public void call(Subscriber<? super List<Note>> subscriber) {
        try {
          final Cursor cursor = resolver.query(
            NoteContentProvider.CONTENT_URI, ALL_COLUMNS,
            NoteDbTable.COLUMN_TRASHED + "=0 AND " +
              NoteDbTable.COLUMN_CALENDAR_MONTH + "=" + month + " AND " +
              NoteDbTable.COLUMN_CALENDAR_YEAR + "=" + year,
            null,
            null
          );
          List<Note> notes = new ArrayList<>();
          if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
              notes.add(fromCursor(cursor));
              cursor.moveToNext();
            }
            cursor.close();
          }
          subscriber.onNext(notes);
          subscriber.onCompleted();
        } catch (Exception e) {
          subscriber.onError(e);
        }
      }
    })
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread());
  }

  @Override
  public Observable<List<Note>> searchInCalendar(final String query) {
    return Observable.create(new Observable.OnSubscribe<List<Note>>() {
      @Override
      public void call(Subscriber<? super List<Note>> subscriber) {
        try {
          final Cursor cursor = resolver.query(
            NoteContentProvider.CONTENT_URI, ALL_COLUMNS,
            NoteDbTable.COLUMN_TRASHED + "=0 AND " +
              NoteDbTable.COLUMN_CALENDAR_YEAR + "!=-1" + " AND " +
              NoteDbTable.COLUMN_CALENDAR_MONTH + "!=-1" + " AND " +
              NoteDbTable.COLUMN_CALENDAR_DAY + "!=-1 " + " AND " +
              query,
            null,
            null
          );
          List<Note> notes = new ArrayList<>();
          if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
              notes.add(fromCursor(cursor));
              cursor.moveToNext();
            }
            cursor.close();
          }
          subscriber.onNext(notes);
          subscriber.onCompleted();
        } catch (Exception e) {
          subscriber.onError(e);
        }
      }
    })
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread());
  }

  private Observable<List<Note>> getAllNotes(final Cursor cursor) {
    return Observable.create(new Observable.OnSubscribe<List<Note>>() {
      @Override
      public void call(Subscriber<? super List<Note>> subscriber) {
        List<Note> notes = new ArrayList<>();
        if (cursor != null) {
          cursor.moveToFirst();
          while (!cursor.isAfterLast()) {
            notes.add(fromCursor(cursor));
            cursor.moveToNext();
          }
          cursor.close();
        }
      }
    });
  }

  private Observable<Note> update(final Note note, final ContentValues values) {
    return Observable.create(new Observable.OnSubscribe<Note>() {
      @Override
      public void call(Subscriber<? super Note> subscriber) {
        try {
          int result = resolver.update(
            NoteContentProvider.CONTENT_URI,
            values,
            NoteDbTable.COLUMN_MODIFIED_TIME + "=" + note.getModifiedTime(),
            null
          );
          if (result == 1) {
            subscriber.onNext(note);
            subscriber.onCompleted();
          } else {
            subscriber.onError(new RuntimeException("Failed to update note: " + values));
          }
        } catch (Exception e) {
          subscriber.onError(e);
        }
      }
    })
      .subscribeOn(Schedulers.io())
      .subscribeOn(AndroidSchedulers.mainThread());
  }

  /**
   * Convenient method to constructor a note from cursor
   *
   * @param cursor Cursor to note data
   * @return
   */
  public static Note fromCursor(Cursor cursor) {
    Note note = new Note(
      cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbTable.COLUMN_ID)),
      cursor.getString(cursor.getColumnIndexOrThrow(NoteDbTable.COLUMN_TITLE)),
      cursor.getString(cursor.getColumnIndexOrThrow(NoteDbTable.COLUMN_CONTENT)),
      cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbTable.COLUMN_MODIFIED_TIME)),
      cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbTable.COLUMN_COLOR))
    );

    boolean locked = cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbTable.COLUMN_LOCKED)) == 1;
    note.setLocked(locked);

    boolean trashed = cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbTable.COLUMN_TRASHED)) == 1;
    note.setTrashed(trashed);

    boolean checked = cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbTable.COLUMN_CHECKED)) == 1;
    note.setChecked(checked);

    if (trashed) {
      note.setDeletedTime(cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbTable.COLUMN_DELETED_TIME)));
    }

    String reminder = cursor.getString(cursor.getColumnIndexOrThrow(NoteDbTable.COLUMN_REMINDER));
    if (!TextUtility.isNullOrEmpty(reminder)) {
      note.setReminder(reminder);
    }

    // For calendar note
    int day = cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbTable.COLUMN_CALENDAR_DAY));
    note.setDay(day);

    int month = cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbTable.COLUMN_CALENDAR_MONTH));
    note.setMonth(month);

    int year = cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbTable.COLUMN_CALENDAR_YEAR));
    note.setYear(year);

    return note;
  }
}
