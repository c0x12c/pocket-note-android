package chan.android.app.pocketnote.app.preferences;


public interface PreferenceResource {

  void saveDefaultAlphabetSortColumn(String column);

  String getDefaultAlphabetSortColumn();

  void saveDefaultCollectionView(int index);

  int getDefaultCollectionView();

  void saveDefaultSortBy(int index);

  int getDefaultSortBy();

  void saveDefaultColor(int color);

  int getDefaultColor();

  void savePassword(String password);

  String getPassword();

  boolean hasCorrectPassword(String password);

  void saveUserPhotoFilePath(String imagePath);

  void saveUserTempPhotoFilePath(String imagePath);

  String getUserTempPhotoFilePath();

  String getUserPhotoFilePath();

  void saveUserName(String name);

  String getUserName();
}
