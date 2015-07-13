package chan.android.app.pocketnote.util.view;

import android.content.Context;

public class NavigationMenuItem implements NavigationDrawerItem {

  public static final int ITEM_TYPE = 1;

  private int id;

  private String label;

  private int icon;

  private boolean updateActionBarTitle;

  private boolean checkable;

  private NavigationMenuItem() {
    // empty
  }

  public static NavigationMenuItem create(int id, String label, String icon, boolean updateActionBarTitle, boolean checkable, Context context) {
    NavigationMenuItem item = new NavigationMenuItem();
    item.setId(id);
    item.setLabel(label);
    int drawableResource = context.getResources().getIdentifier(icon, "drawable", context.getPackageName());
    item.setIcon(drawableResource);
    item.setUpdateActionBarTitle(updateActionBarTitle);
    item.setCheckable(checkable);
    return item;
  }

  @Override
  public int getType() {
    return ITEM_TYPE;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public int getIcon() {
    return icon;
  }

  public void setIcon(int icon) {
    this.icon = icon;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean updateActionBarTitle() {
    return this.updateActionBarTitle;
  }

  public void setUpdateActionBarTitle(boolean updateActionBarTitle) {
    this.updateActionBarTitle = updateActionBarTitle;
  }

  public boolean isCheckable() {
    return checkable;
  }

  public void setCheckable(boolean checkable) {
    this.checkable = checkable;
  }
}

