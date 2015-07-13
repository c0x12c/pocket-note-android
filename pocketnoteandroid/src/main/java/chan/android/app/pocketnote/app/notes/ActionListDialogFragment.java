package chan.android.app.pocketnote.app.notes;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import chan.android.app.pocketnote.R;

import java.util.List;

public class ActionListDialogFragment extends DialogFragment {

  private String title;
  private ItemAdapter adapter;
  private List<Item> items;
  private OnPickItemListener listener;

  private ActionListDialogFragment(String title, List<Item> items) {
    this.title = title;
    this.items = items;
  }

  public static ActionListDialogFragment newInstance(String title, List<Item> items) {
    return new ActionListDialogFragment(title, items);
  }

  public void setPickItemListener(OnPickItemListener listener) {
    this.listener = listener;
  }

  public void removePickListener() {
    this.listener = null;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.purple_dialog, container);
    final ListView listView = (ListView) root.findViewById(R.id.purple_dialog_$_listview_items);
    final TextView textView = (TextView) root.findViewById(R.id.purple_dialog_$_textview_title);
    textView.setText(title);
    ItemAdapter adapter = new ItemAdapter(getActivity(), items);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
          listener.onPick(position);
        }
        dismiss();
      }
    });
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    return root;
  }

  public interface OnPickItemListener {

    public void onPick(int index);
  }

  public static class Item {

    final int iconId;
    final String name;

    public Item(int iconId, String name) {
      this.iconId = iconId;
      this.name = name;
    }

    public int getIconId() {
      return iconId;
    }

    public String getName() {
      return name;
    }
  }

  static class ItemAdapter extends BaseAdapter {

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
        convertView = inflater.inflate(R.layout.purple_dialog_row, parent, false);
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
      TextView item;

      public ViewHolder(View v) {
        item = (TextView) v.findViewById(R.id.purple_dialog_row_$_textview_item);
      }
    }
  }
}
