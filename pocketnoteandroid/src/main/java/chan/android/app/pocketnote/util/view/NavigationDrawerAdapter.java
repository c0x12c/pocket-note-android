package chan.android.app.pocketnote.util.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import chan.android.app.pocketnote.R;

public class NavigationDrawerAdapter extends ArrayAdapter<NavigationDrawerItem> {

  LayoutInflater inflater;

  public NavigationDrawerAdapter(Context context, int textViewResourceId, NavigationDrawerItem[] objects) {
    super(context, textViewResourceId, objects);
    this.inflater = LayoutInflater.from(context);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view;
    NavigationDrawerItem item = this.getItem(position);
    if (item.getType() == NavigationMenuItem.ITEM_TYPE) {
      view = getItemView(convertView, parent, item);
    } else {
      view = getSectionView(convertView, parent, item);
    }
    return view;
  }

  public View getItemView(View convertView, ViewGroup parentView, NavigationDrawerItem drawerItem) {
    NavigationMenuItem item = (NavigationMenuItem) drawerItem;
    MenuItemHolder holder;
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.navdrawer_item, parentView, false);
      holder = new MenuItemHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (MenuItemHolder) convertView.getTag();
    }
    holder.label.setText(item.getLabel());
    holder.icon.setImageResource(item.getIcon());
    return convertView;
  }

  public View getSectionView(View convertView, ViewGroup parentView, NavigationDrawerItem drawerItem) {
    NavigationMenuSection item = (NavigationMenuSection) drawerItem;
    MenuSectionHolder holder;
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.navdrawer_section, parentView, false);
      holder = new MenuSectionHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (MenuSectionHolder) convertView.getTag();
    }
    holder.section.setText(item.getLabel());
    return convertView;
  }

  @Override
  public int getViewTypeCount() {
    return 2;
  }

  @Override
  public int getItemViewType(int position) {
    return this.getItem(position).getType();
  }

  @Override
  public boolean isEnabled(int position) {
    return getItem(position).isEnabled();
  }

  static class MenuItemHolder {
    TextView label;
    ImageView icon;
    TextView notification;

    public MenuItemHolder(View v) {
      label = (TextView) v.findViewById(R.id.navmenuitem_label);
      icon = (ImageView) v.findViewById(R.id.navmenuitem_icon);
      notification = (TextView) v.findViewById(R.id.navmenuitem_extra);
    }
  }

  class MenuSectionHolder {
    TextView section;

    public MenuSectionHolder(View v) {
      section = (TextView) v.findViewById(R.id.navmenusection_label);
    }
  }
}
