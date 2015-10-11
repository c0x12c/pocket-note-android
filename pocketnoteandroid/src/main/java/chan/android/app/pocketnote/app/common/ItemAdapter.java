package chan.android.app.pocketnote.app.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import chan.android.app.pocketnote.R;

import java.util.List;

public class ItemAdapter extends BaseAdapter {

  private List<Item> items;

  private LayoutInflater inflater;

  public ItemAdapter(Context context, List<Item> items) {
    this.inflater = LayoutInflater.from(context);
    this.items = items;
  }

  @Override
  public int getCount() {
    return items.size();
  }

  @Override
  public Item getItem(int position) {
    return items.get(position);
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder vh;
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.row_action, parent, false);
      vh = new ViewHolder(convertView);
      convertView.setTag(vh);
    } else {
      vh = (ViewHolder) convertView.getTag();
    }
    final Item item = items.get(position);
    vh.item.setText(item.getName());
    vh.item.setCompoundDrawablesWithIntrinsicBounds(item.getIconId(), 0, 0, 0);
    return convertView;
  }

  static class ViewHolder {
    final TextView item;

    public ViewHolder(View v) {
      item = (TextView) v.findViewById(
        R.id.row_action___textview);
    }
  }
}
