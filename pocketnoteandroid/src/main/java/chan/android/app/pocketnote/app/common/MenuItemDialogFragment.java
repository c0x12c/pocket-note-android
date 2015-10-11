package chan.android.app.pocketnote.app.common;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.BaseDialogFragment;

import java.util.ArrayList;

public class MenuItemDialogFragment extends BaseDialogFragment {

  public static final String TAG = MenuItemDialogFragment.class.getSimpleName();

  public interface OnPickItemListener {

    void onPick(View v, int index, Item item);
  }

  interface Args {

    String TITLE = TAG + ".title";

    String ITEMS = TAG + ".items";
  }

  private OnPickItemListener listener;

  private ArrayList<Item> items;

  private String title;

  private ListView listView;

  private TextView titleView;

  private ItemAdapter adapter;

  public static MenuItemDialogFragment fragment(String title, ArrayList<Item> items) {
    Bundle args = new Bundle();
    args.putString(Args.TITLE, title);
    args.putParcelableArrayList(Args.ITEMS, items);
    MenuItemDialogFragment d = new MenuItemDialogFragment();
    d.setArguments(args);
    return d;
  }

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    final Bundle args = getArguments();
    title = args.getString(Args.TITLE);
    items = args.getParcelableArrayList(Args.ITEMS);
  }

  public void setPickItemListener(OnPickItemListener listener) {
    this.listener = listener;
  }

  public void removePickListener() {
    this.listener = null;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(
      R.layout.purple_dialog, container);
    listView = (ListView) root.findViewById(
      R.id.purple_dialog___listview_items);
    titleView = (TextView) root.findViewById(
      R.id.purple_dialog___textview_title);

    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    return root;
  }

  @Override
  public void onViewCreated(View view, Bundle bundle) {
    titleView.setText(title);
    adapter = new ItemAdapter(getActivity(), items);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
          listener.onPick(view, position, adapter.getItem(position));
        }
        dismiss();
      }
    });
  }
}
