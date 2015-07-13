package chan.android.app.pocketnote.app.notes.colors;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import chan.android.app.pocketnote.R;

public class ColorPickerDialogFragment extends DialogFragment {

  public static final String TAG = ColorPickerDialogFragment.class.getSimpleName();

  private OnPickColorListener listener;

  public ColorPickerDialogFragment() {
    // ...
  }

  public void setOnPickColorListener(OnPickColorListener listener) {
    this.listener = listener;
  }

  public void removePickColorListener() {
    this.listener = null;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.color_dialog, container, false);
    final GridView gridView = (GridView) root.findViewById(R.id.color_dialog_$_gridview_colors);
    final ColorItemAdapter adapter = new ColorItemAdapter(getActivity(), ColorItem.DEFAULT_ITEMS);
    gridView.setAdapter(adapter);
    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
          listener.onPick(adapter.getItem(position).getColor());
          dismiss();
        }
      }
    });
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    return root;
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
        convertView = inflater.inflate(R.layout.color_dialog_item, parent, false);
        vh = new ViewHolder(convertView);
        convertView.setTag(vh);
      } else {
        vh = (ViewHolder) convertView.getTag();
      }
      final ColorItem item = items[position];
      vh.imageView.setImageDrawable(context.getResources().getDrawable(item.getDrawableId()));
      vh.name.setText(item.getName());
      return convertView;
    }

    static class ViewHolder {
      ImageView imageView;
      TextView name;

      public ViewHolder(View v) {
        imageView = (ImageView) v.findViewById(R.id.color_dialog_item_$_drawable);
        name = (TextView) v.findViewById(R.id.color_dialog_item_$_textview_name);
      }
    }
  }
}
