package chan.android.app.pocketnote.app.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

public class NoteContentProvider extends ContentProvider {

  public static final String CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/notes";
  public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/note";
  public static final String[] COLUMNS = {
    NoteDbTable.COLUMN_ID,
    NoteDbTable.COLUMN_TITLE,
    NoteDbTable.COLUMN_CONTENT,
    NoteDbTable.COLUMN_MODIFIED_TIME,
    NoteDbTable.COLUMN_COLOR,
    NoteDbTable.COLUMN_REMINDER,
    NoteDbTable.COLUMN_TRASHED,
    NoteDbTable.COLUMN_LOCKED,
    NoteDbTable.COLUMN_CHECKED,
    NoteDbTable.COLUMN_DELETED_TIME,
    NoteDbTable.COLUMN_CALENDAR_DAY,
    NoteDbTable.COLUMN_CALENDAR_MONTH,
    NoteDbTable.COLUMN_CALENDAR_YEAR
  };
  private static final int TYPE_SINGLE = 1;
  private static final int TYPE_PLURAL = 2;
  private static final String AUTHORITY = "chan.android.app.pocketnote.app.db.note";
  private static final String BASE_PATH = "notes";
  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
  private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

  static {
    URI_MATCHER.addURI(AUTHORITY, BASE_PATH, TYPE_PLURAL);
    URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/#", TYPE_SINGLE);
  }

  private NoteDbHelper dbHelper;

  private void checkColumns(String[] projection) {
    if (projection != null) {
      HashSet<String> request = new HashSet<String>(Arrays.asList(projection));
      HashSet<String> available = new HashSet<String>(Arrays.asList(COLUMNS));
      if (!available.containsAll(request)) {
        throw new IllegalArgumentException("Unknown columns in projection");
      }
    }
  }

  @Override
  public boolean onCreate() {
    dbHelper = new NoteDbHelper(getContext());
    return true;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    checkColumns(projection);
    queryBuilder.setTables(NoteDbTable.TABLE_NAME);
    int type = URI_MATCHER.match(uri);
    switch (type) {
      case TYPE_PLURAL:
        break;

      case TYPE_SINGLE:
        queryBuilder.appendWhere(NoteDbTable.COLUMN_ID + "=" + uri.getLastPathSegment());
        break;

      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    cursor.setNotificationUri(getContext().getContentResolver(), uri);
    return cursor;
  }

  @Override
  public String getType(Uri uri) {
    return null;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    int type = URI_MATCHER.match(uri);
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    long id;
    switch (type) {
      case TYPE_PLURAL:
        id = db.insert(NoteDbTable.TABLE_NAME, null, values);
        break;

      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return Uri.parse(BASE_PATH + "/" + id);
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    int type = URI_MATCHER.match(uri);
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    int affectedRows;
    switch (type) {
      case TYPE_PLURAL:
        affectedRows = db.delete(NoteDbTable.TABLE_NAME, selection, selectionArgs);
        break;

      case TYPE_SINGLE:
        String id = uri.getLastPathSegment();
        if (TextUtils.isEmpty(selection)) {
          affectedRows = db.delete(NoteDbTable.TABLE_NAME, NoteDbTable.COLUMN_ID + "=" + id, null);
        } else {
          affectedRows = db.delete(NoteDbTable.TABLE_NAME, NoteDbTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
        }
        break;

      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return affectedRows;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    int type = URI_MATCHER.match(uri);
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    int affectedRows;
    switch (type) {
      case TYPE_PLURAL:
        affectedRows = db.update(NoteDbTable.TABLE_NAME, values, selection, selectionArgs);
        break;

      case TYPE_SINGLE:
        String id = uri.getLastPathSegment();
        if (TextUtils.isEmpty(selection)) {
          affectedRows = db.update(NoteDbTable.TABLE_NAME, values, NoteDbTable.COLUMN_ID + "=" + id, null);
        } else {
          affectedRows = db.update(NoteDbTable.TABLE_NAME, values, NoteDbTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
        }
        break;

      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return affectedRows;
  }
}
