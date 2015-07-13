package chan.android.app.pocketnote.app.db;

import android.database.sqlite.SQLiteDatabase;

public class NoteDbTable {

  public static final String TABLE_NAME = "notes";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_TITLE = "title";
  public static final String COLUMN_CONTENT = "content";
  public static final String COLUMN_MODIFIED_TIME = "modified_time";
  public static final String COLUMN_COLOR = "color";
  public static final String COLUMN_REMINDER = "reminder_json";
  public static final String COLUMN_LOCKED = "locked";
  public static final String COLUMN_CHECKED = "checked";
  public static final String COLUMN_TRASHED = "trashed";
  public static final String COLUMN_DELETED_TIME = "deleted_time";
  public static final String COLUMN_CALENDAR_DAY = "calendar_day";
  public static final String COLUMN_CALENDAR_MONTH = "calendar_month";
  public static final String COLUMN_CALENDAR_YEAR = "calendar_year";

  private static final String QUERY_CREATE = "create table "
    + TABLE_NAME
    + "("
    + COLUMN_ID + " integer primary key autoincrement, "
    + COLUMN_TITLE + " text not null, "
    + COLUMN_CONTENT + " text not null, "
    + COLUMN_COLOR + " integer not null, "
    + COLUMN_MODIFIED_TIME + " integer not null, "
    + COLUMN_REMINDER + " text, "
    + COLUMN_LOCKED + " integer default 0, "
    + COLUMN_CHECKED + " integer default 0, "
    + COLUMN_TRASHED + " integer default 0, "
    + COLUMN_DELETED_TIME + " integer default 0, "
    + COLUMN_CALENDAR_DAY + " integer default -1, "
    + COLUMN_CALENDAR_MONTH + " integer default -1, "
    + COLUMN_CALENDAR_YEAR + " integer default -1 "
    + ");";

  private static final String QUERY_DROP = "drop table if exists " + TABLE_NAME;

  public static void onCreate(SQLiteDatabase database) {
    database.execSQL(QUERY_CREATE);
  }

  public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    database.execSQL(QUERY_DROP);
    onCreate(database);
  }
}
