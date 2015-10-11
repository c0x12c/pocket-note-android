package chan.android.app.pocketnote.app.preferences;


import rx.Observable;

public interface PreferenceResource {

  Observable<String> getDefaultAlphabetSortColumn();

  Observable<Integer> getDefaultSortBy();

  Observable<Integer> getDefaultColor();

  Observable<Integer> getDefaultCollectionView();

  Observable<String> getPassword();

  Observable<Boolean> hasCorrectPassword(String password);

  Observable<String> getUserTempPhotoFilePath();

  Observable<String> getUserPhotoFilePath();

  Observable<String> getUserName();

  void saveDefaultCollectionView(int index);

  void saveDefaultAlphabetSortColumn(String column);

  void saveDefaultColor(int color);

  void savePassword(String password);

  void saveUserPhotoFilePath(String imagePath);

  void saveUserTempPhotoFilePath(String imagePath);

  void saveUserName(String name);

}
