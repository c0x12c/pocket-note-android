package chan.android.app.pocketnote.app.notes.colors;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import chan.android.app.pocketnote.R;

public class ColorDropdownDialogFragment extends DialogFragment implements View.OnClickListener {

  public static final String TAG = ColorDropdownDialogFragment.class.getSimpleName();


  private OnPickColorListener listener;

  public ColorDropdownDialogFragment() {
    // ...
  }

  public void setOnPickColorListener(OnPickColorListener listener) {
    this.listener = listener;
  }

  public void removeOnPickColorListener() {
    this.listener = null;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    View root = inflater.inflate(R.layout.color_dropdown_dialog, container, false);
    ListView listView = (ListView) root.findViewById(R.id.color_dropdown_dialog_$_listview);
    final ColorItemAdapter adapter = new ColorItemAdapter(getActivity(), ColorItem.DEFAULT_ITEMS);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
          listener.onPick(adapter.getItem(position).getColor());
          dismiss();
        }
      }
    });
    return root;
  }

  @Override
  public void onClick(View v) {
    if (listener != null) {
      final Button b = (Button) v;
      listener.onPick(getButtonColor(b));
      b.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }
    dismiss();
  }

  public int getButtonColor(Button button) {
    ColorDrawable cd = (ColorDrawable) button.getBackground();
    return cd.getColor();
  }

  private String hexColor(int color) {
    return String.format("#%06X", (0xFFFFFF & color));
  }

  public static class ColorItemAdapter extends BaseAdapter {

    private ColorItem[] items;

    private Context context;

    private ColorItemAdapter(Context context, ColorItem[] items) {
      this.context = context;
      this.items = items;
    }

    @Override
    public int getCount() {
      return items.length;
    }

    @Override
    public ColorItem getItem(int position) {
      return items[position];
    }

    @Override
    public long getItemId(int position) {
      return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder vh;
      if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.color_dropdown_item, parent, false);
        vh = new ViewHolder(convertView);
        convertView.setTag(vh);
      } else {
        vh = (ViewHolder) convertView.getTag();
      }
      final ColorItem item = items[position];
      vh.name.setText(item.getName());
      vh.drawable.setImageDrawable(context.getResources().getDrawable(item.getDrawableId()));
      return convertView;
    }

    static class ViewHolder {
      ImageView drawable;
      TextView name;

      public ViewHolder(View v) {
        drawable = (ImageView) v.findViewById(R.id.color_dropdown_item_$_drawable);
        name = (TextView) v.findViewById(R.id.color_dropdown_item_$_checkedtextview_name);
      }
    }
  }
}

