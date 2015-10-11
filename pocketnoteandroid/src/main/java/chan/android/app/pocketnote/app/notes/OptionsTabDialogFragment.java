package chan.android.app.pocketnote.app.notes;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import chan.android.app.pocketnote.R;

import java.util.Arrays;
import java.util.List;

public class OptionsTabDialogFragment extends DialogFragment {

  public static final String TAG = OptionsTabDialogFragment.class.getSimpleName();
  private static final Option[] VIEW_OPTIONS = new Option[]{
    new Option(R.drawable.ic_action_collections_view_as_list, "As list view"),
    new Option(R.drawable.ic_action_collections_view_as_grid, "As grid view")
  };
  private static final Option[] SORT_OPTIONS = new Option[]{
    new Option(R.drawable.ic_action_clock, "By modified time"),
    new Option(R.drawable.ic_action_alphabetical_sorting, "By alphabet"),
    new Option(R.drawable.ic_action_color_pencil, "By color")
  };
  private PickOptionListener pickListener;

  public OptionsTabDialogFragment() {
    // Empty constructor required for DialogFragment
  }

  public void addPickOptionListener(PickOptionListener listener) {
    pickListener = listener;
  }

  public void removePickOptionListener() {
    pickListener = null;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.options_tabhost, container);
    TabHost tabs = (TabHost) root.findViewById(R.id.tabhost);
    tabs.setup();
    TabHost.TabSpec spec = tabs.newTabSpec("tag1");
    spec.setContent(R.id.tab_host_$_listview_sort_by);
    populateListView(root, R.id.tab_host_$_listview_sort_by, Arrays.asList(SORT_OPTIONS));
    spec.setIndicator("Sort by");
    tabs.addTab(spec);

    spec = tabs.newTabSpec("tag2");
    spec.setContent(R.id.tab_host_$_listview_view);
    populateListView(root, R.id.tab_host_$_listview_view, Arrays.asList(VIEW_OPTIONS));
    spec.setIndicator("View");
    tabs.addTab(spec);

    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    return root;
  }

  private void populateListView(View root, int listId, final List<Option> options) {
    ListView listView = (ListView) root.findViewById(listId);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (pickListener != null) {
          pickListener.onPick(options.get(position));
        }
        dismiss();
      }
    });
    OptionAdapter adapter = new OptionAdapter(getActivity(), options);
    listView.setAdapter(adapter);
  }

  public interface PickOptionListener {

    void onPick(Option option);
  }

  public static class OptionAdapter extends BaseAdapter {

    private final Context context;
    private List<Option> options;

    public OptionAdapter(Context context, List<Option> options) {
      this.context = context;
      this.options = options;
    }

    @Override
    public int getCount() {
      return options.size();
    }

    @Override
    public Option getItem(int position) {
      return options.get(position);
    }

    @Override
    public long getItemId(int position) {
      return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder viewHolder;
      if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row_option, null);
        viewHolder = new ViewHolder(convertView);
        convertView.setTag(viewHolder);
      } else {
        viewHolder = (ViewHolder) convertView.getTag();
      }

      final Option opt = options.get(position);
      viewHolder.icon.setImageResource(opt.getResourceId());
      viewHolder.title.setText(opt.getTitle());
      return convertView;
    }

    static class ViewHolder {
      final ImageView icon;
      final TextView title;

      public ViewHolder(View v) {
        icon = (ImageView) v.findViewById(
          R.id.row_option___imageview_icon);
        title = (TextView) v.findViewById(
          R.id.row_option___textview_title);
      }
    }
  }

  public static class Option {

    private final int resourceId;

    private final String title;

    public Option(int resourceId, String title) {
      this.resourceId = resourceId;
      this.title = title;
    }

    public int getResourceId() {
      return resourceId;
    }

    public String getTitle() {
      return title;
    }
  }
}
