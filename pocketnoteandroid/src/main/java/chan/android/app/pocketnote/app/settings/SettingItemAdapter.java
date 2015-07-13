package chan.android.app.pocketnote.app.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import chan.android.app.pocketnote.R;

import java.util.List;

public class SettingItemAdapter extends BaseAdapter {

  final List<String> items;
  final Context context;

  public SettingItemAdapter(Context context, List<String> items) {
    this.context = context;
    this.items = items;
  }

  @Override
  public int getCount() {
    return items.size();
  }

  @Override
  public String getItem(int position) {
    return items.get(position);
  }

  @Override
  public long getItemId(int i) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;
    if (convertView == null) {
      LayoutInflater inflater = LayoutInflater.from(context);
      convertView = inflater.inflate(R.layout.option_row, parent, false);
      viewHolder = new ViewHolder(convertView);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    viewHolder.text.setText(items.get(position));
    return convertView;
  }

  static class ViewHolder {
    final TextView text;

    public ViewHolder(View v) {
      text = (TextView) v.findViewById(R.id.option_row_$_textview_item);
    }
  }
}
