package chan.android.app.pocketnote.app;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import chan.android.app.pocketnote.app.db.NoteDbTable;
import chan.android.app.pocketnote.util.HashUtility;

import java.util.HashSet;
import java.util.Set;

public class AppPreferences {

  /**
   * This color should be in one of the color picker dialog
   */
  private static final int DEFAULT_COLOR = Color.parseColor("#f4da70");

  private static final int VIEW_AS_LIST = 0;
  private static final int VIEW_AS_GRID = 1;
  private static final Set<String> AVAILABLE_COLUMNS = new HashSet<String>();
  private static SharedPreferences prefs;

  static {
    AVAILABLE_COLUMNS.add(NoteDbTable.COLUMN_CONTENT);
    AVAILABLE_COLUMNS.add(NoteDbTable.COLUMN_TITLE);
  }

  ;

  public static void initialize(Context context) {
    if (prefs == null) {
      prefs = context.getSharedPreferences("chan.android.app.pocketnote.prefs_", Context.MODE_PRIVATE);
    }
  }

  public static void saveDefaultAlphabetSortColumn(String column) {
    if (!AVAILABLE_COLUMNS.contains(column)) {
      throw new RuntimeException("Invalid alphabet column: " + column);
    }
    prefs.edit().putString(Key.ALPHABET_SORT_COLUMN.name(), column).commit();
  }

  public static String getDefaultAlphabetSortColumn() {
    return prefs.getString(Key.ALPHABET_SORT_COLUMN.name(), NoteDbTable.COLUMN_TITLE);
  }

  public static void saveDefaultCollectionView(int index) {
    if (index == VIEW_AS_LIST || index == VIEW_AS_GRID) {
      prefs.edit().putInt(Key.COLLECTION_VIEW.name(), index).commit();
    }
  }

  public static int getDefaultCollectionView() {
    return prefs.getInt(Key.COLLECTION_VIEW.name(), VIEW_AS_LIST);
  }

  public static void saveDefaultSortBy(int index) {
    prefs.edit().putInt(Key.SORT_BY.name(), index).commit();
  }

  public static int getDefaultSortBy() {
    return prefs.getInt(Key.SORT_BY.name(), 0);
  }

  public static void saveDefaultColor(int color) {
    prefs.edit().putInt(Key.COLOR.name(), color).commit();
  }

  public static int getDefaultColor() {
    return prefs.getInt(Key.COLOR.name(), DEFAULT_COLOR);
  }

  public static void savePassword(String password) {
    prefs.edit().putString(Key.PASSWORD.name(), HashUtility.md5(password)).commit();
  }

  public static String getPassword() {
    return prefs.getString(Key.PASSWORD.name(), "");
  }

  public static boolean hasCorrectPassword(String password) {
    String actual = getPassword();
    return actual.equals(HashUtility.md5(password));
  }

  public static void saveUserPhotoFilePath(String imagePath) {
    prefs.edit().putString(Key.USER_PHOTO.name(), imagePath).commit();
  }

  public static void saveUserTempPhotoFilePath(String imagePath) {
    prefs.edit().putString(Key.USER_PHOTO_TEMP.name(), imagePath).commit();
  }

  public static String getUserTempPhotoFilePath() {
    return prefs.getString(Key.USER_PHOTO_TEMP.name(), null);
  }

  public static String getUserPhotoFilePath() {
    return prefs.getString(Key.USER_PHOTO.name(), null);
  }

  public static void saveUserName(String name) {
    prefs.edit().putString(Key.USER_NAME.name(), name).commit();
  }

  public static String getUserName() {
    return prefs.getString(Key.USER_NAME.name(), "Anonymous");
  }

  private enum Key {
    PASSWORD,
    COLOR,
    SORT_BY,
    COLLECTION_VIEW,
    ALPHABET_SORT_COLUMN,
    USER_PHOTO_TEMP,
    USER_PHOTO,
    USER_NAME
  }
}
