package chan.android.app.pocketnote.util.view;

public class NavigationMenuSection implements NavigationDrawerItem {

  public static final int SECTION_TYPE = 0;

  private int id;

  private String label;

  private NavigationMenuSection() {
    // don't call me
  }

  public static NavigationMenuSection create(int id, String label) {
    NavigationMenuSection section = new NavigationMenuSection();
    section.setLabel(label);
    return section;
  }

  @Override
  public int getType() {
    return SECTION_TYPE;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public boolean updateActionBarTitle() {
    return false;
  }

  @Override
  public boolean isCheckable() {
    return false;
  }
}
