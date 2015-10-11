package chan.android.app.pocketnote.app.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;
import chan.android.app.pocketnote.app.db.NoteDbTable;
import chan.android.app.pocketnote.util.Hasher;
import rx.Observable;

import java.util.HashSet;
import java.util.Set;

public class PreferenceResourceManager implements PreferenceResource {

  /**
   * This color should be in one of the color picker dialog
   */
  private static final int DEFAULT_COLOR = Color.parseColor("#f4da70");

  private static final int VIEW_AS_LIST = 0;

  private static final int VIEW_AS_GRID = 1;

  @IntDef({VIEW_AS_LIST, VIEW_AS_GRID})
  public @interface ViewType {
  }

  @StringDef({NoteDbTable.COLUMN_CONTENT, NoteDbTable.COLUMN_TITLE})
  public @interface SortType {
  }

  private static final Set<String> AVAILABLE_COLUMNS = new HashSet<>();

  private final SharedPreferences prefs;

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

  static {
    AVAILABLE_COLUMNS.add(NoteDbTable.COLUMN_CONTENT);
    AVAILABLE_COLUMNS.add(NoteDbTable.COLUMN_TITLE);
  }

  public PreferenceResourceManager(Context context) {
    prefs = context.getSharedPreferences("chan.android.app.pocketnote.prefs_", Context.MODE_PRIVATE);
  }

  @Override
  public Observable<String> getDefaultAlphabetSortColumn() {
    return Observable.just(prefs.getString(Key.ALPHABET_SORT_COLUMN.name(), NoteDbTable.COLUMN_TITLE));
  }

  @Override
  public Observable<Integer> getDefaultSortBy() {
    return Observable.just(prefs.getInt(Key.SORT_BY.name(), 0));
  }

  @Override
  public Observable<Integer> getDefaultColor() {
    return Observable.just(prefs.getInt(Key.COLOR.name(), DEFAULT_COLOR));
  }

  @Override
  public Observable<Integer> getDefaultCollectionView() {
    return Observable.just(prefs.getInt(Key.COLLECTION_VIEW.name(), VIEW_AS_LIST));
  }

  @Override
  public Observable<String> getPassword() {
    return Observable.just(prefs.getString(Key.PASSWORD.name(), ""));
  }

  @Override
  public Observable<Boolean> hasCorrectPassword(String password) {
    return Observable.just(getPassword().equals(Hasher.md5(password)));
  }

  @Override
  public Observable<String> getUserTempPhotoFilePath() {
    return Observable.just(prefs.getString(Key.USER_PHOTO_TEMP.name(), null));
  }

  @Override
  public Observable<String> getUserPhotoFilePath() {
    return Observable.just(prefs.getString(Key.USER_PHOTO.name(), null));
  }

  @Override
  public Observable<String> getUserName() {
    return Observable.just(prefs.getString(Key.USER_NAME.name(), "Anonymous"));
  }

  @Override
  public void saveDefaultCollectionView(@ViewType int index) {

  }

  @Override
  public void saveDefaultAlphabetSortColumn(@SortType String column) {
    prefs.edit().putString(Key.ALPHABET_SORT_COLUMN.name(), column).apply();
  }

  @Override
  public void saveDefaultColor(int color) {
    prefs.edit().putInt(Key.COLOR.name(), color).apply();
  }

  @Override
  public void savePassword(String password) {
    prefs.edit().putString(Key.PASSWORD.name(), Hasher.md5(password)).apply();
  }

  @Override
  public void saveUserPhotoFilePath(String imagePath) {
    prefs.edit().putString(Key.USER_PHOTO.name(), imagePath).apply();
  }

  @Override
  public void saveUserTempPhotoFilePath(String imagePath) {
    prefs.edit().putString(Key.USER_PHOTO_TEMP.name(), imagePath).apply();
  }

  @Override
  public void saveUserName(String name) {
    prefs.edit().putString(Key.USER_NAME.name(), name).apply();
  }
}
