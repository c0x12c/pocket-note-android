package chan.android.app.pocketnote.app.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class NoteDbHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "notes.db";

  private static final int DATABASE_VERSION = 1;

  public NoteDbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    NoteDbTable.onCreate(db);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    NoteDbTable.onUpgrade(db, oldVersion, newVersion);
  }
}
