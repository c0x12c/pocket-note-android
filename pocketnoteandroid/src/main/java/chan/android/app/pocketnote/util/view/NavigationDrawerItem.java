package chan.android.app.pocketnote.util.view;

public interface NavigationDrawerItem {

  int getId();

  String getLabel();

  int getType();

  boolean isEnabled();

  boolean updateActionBarTitle();

  boolean isCheckable();
}
