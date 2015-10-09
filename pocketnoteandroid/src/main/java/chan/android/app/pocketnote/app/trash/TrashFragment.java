package chan.android.app.pocketnote.app.trash;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.*;
import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.Note;
import chan.android.app.pocketnote.app.db.NoteContentProvider;
import chan.android.app.pocketnote.app.db.NoteDbTable;
import chan.android.app.pocketnote.app.db.PocketNoteManager;
import chan.android.app.pocketnote.app.notes.ActionListDialogFragment;
import chan.android.app.pocketnote.util.DateTimeUtility;
import chan.android.app.pocketnote.util.Logger;
import chan.android.app.pocketnote.util.view.RoundedRectListView;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class TrashFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

  public static final String TAG = "Trash";

  private TrashNoteCursorAdapter adapter;

  private RoundedRectListView listView;

  private ViewFlipper viewFlipper;

  public static TrashFragment newInstance() {
    return new TrashFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getLoaderManager().initLoader(0, null, this);
    adapter = new TrashNoteCursorAdapter(getActivity(), R.layout.trash_list_item);
    setHasOptionsMenu(true);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    menu.clear();
    inflater.inflate(R.menu.trash, menu);
    SearchView searchView = (SearchView) menu.findItem(R.id.trash_menu_$_action_search).getActionView();
    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        if (query.isEmpty()) {
          restartLoader(null);
          return true;
        }

        query = " AND " + NoteDbTable.COLUMN_TITLE + " LIKE '%" + query + "%'";
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        restartLoader(bundle);
        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
          restartLoader(null);
        }
        return true;
      }
    };
    searchView.setOnQueryTextListener(listener);
    super.onCreateOptionsMenu(menu, inflater);
  }

  private void restartLoader(Bundle bundle) {
    getLoaderManager().restartLoader(0, bundle, this);
    adapter.notifyDataSetChanged();
    listView.invalidate();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.trash_menu_$_action_delete_all:
        final ConfirmDialogFragment d = new ConfirmDialogFragment();
        d.setOnConfirmListener(new ConfirmDialogFragment.OnConfirmListener() {
          @Override
          public void onEnter(boolean ok) {
            if (ok) {
              PocketNoteManager.getPocketNoteManager().removeAll();
            }
          }
        });
        d.show(getFragmentManager(), "confirm_dialog");
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Logger.e("TrashFragment.onCreateView()");
    View root = inflater.inflate(R.layout.trash, container, false);
    viewFlipper = (ViewFlipper) root.findViewById(R.id.trash_$_viewflipper);
    viewFlipper.setDisplayedChild(0);

    listView = (RoundedRectListView) root.findViewById(R.id.trash_$_listview);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(new TrashItemLongClickListener(this, adapter));
    return root;
  }

  @Override
  public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    String query = "";
    if (bundle != null && bundle.getString("query") != null) {
      query = bundle.getString("query");
    }

    return new CursorLoader(getActivity(),
      NoteContentProvider.CONTENT_URI,
      NoteContentProvider.COLUMNS,
      NoteDbTable.COLUMN_TRASHED + "=1" + query, null, NoteDbTable.COLUMN_MODIFIED_TIME + " DESC");
  }

  @Override
  public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    adapter.swapCursor(cursor);
    if (adapter.getCount() == 0) {
      viewFlipper.setDisplayedChild(1);
    } else {
      viewFlipper.setDisplayedChild(0);
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> cursorLoader) {
    adapter.swapCursor(null);
  }

  public void invalidate() {
    // Do nothing
  }

  private static class ViewHolder {
    TextView title;
    TextView date;
    LinearLayout parent;

    public ViewHolder(View v) {
      parent = (LinearLayout) v.findViewById(R.id.trash_list_item_$_linearlayout_parent);
      title = (TextView) v.findViewById(R.id.trash_list_item_$_textview_title);
      date = (TextView) v.findViewById(R.id.trash_list_item_$_textview_removed_date);
    }
  }

  private static class TrashItemLongClickListener implements AdapterView.OnItemClickListener {

    private CursorAdapter cursorAdapter;

    private Fragment fragment;

    public TrashItemLongClickListener(Fragment fragment, CursorAdapter adapter) {
      this.cursorAdapter = adapter;
      this.fragment = fragment;
    }

    private List<Option> getAvailableOptions(Note note) {
      List<Option> options = new ArrayList<Option>();
      options.add(Option.REMOVE);
      options.add(Option.RESTORE);
      return options;
    }

    private List<ActionListDialogFragment.Item> getOptionItems(List<Option> options) {
      List<ActionListDialogFragment.Item> result = new ArrayList<>();
      for (int i = 0, n = options.size(); i < n; ++i) {
        result.add(new ActionListDialogFragment.Item(options.get(i).iconId, options.get(i).name));
      }
      return result;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      final Cursor cursor = (Cursor) cursorAdapter.getItem(position);
      final Note note = Note.fromCursor(cursor);
      final List<Option> options = getAvailableOptions(note);
      ActionListDialogFragment d = ActionListDialogFragment.newInstance(note.getTitle(), getOptionItems(options));
      d.setPickItemListener(new ActionListDialogFragment.OnPickItemListener() {
        @Override
        public void onPick(int index) {
          Option opt = options.get(index);
          opt.choose(fragment.getActivity(), note);
          cursorAdapter.notifyDataSetChanged();
        }
      });
      d.show(fragment.getFragmentManager(), "dialog");
    }

    private enum Option {

      REMOVE("Delete permanently", R.drawable.ic_drawer_trash) {
        @Override
        public void choose(Context context, Note note) {
          PocketNoteManager.getPocketNoteManager().remove(note);
        }
      },

      RESTORE("Restore", R.drawable.ic_action_restore) {
        @Override
        public void choose(Context context, Note note) {
          PocketNoteManager.getPocketNoteManager().restore(note);
        }
      };

      final String name;
      final int iconId;

      Option(String name, int iconId) {
        this.name = name;
        this.iconId = iconId;
      }

      public abstract void choose(Context context, Note note);
    }
  }

  private class TrashNoteCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;

    private int layoutId;

    public TrashNoteCursorAdapter(Context context, int layoutId) {
      super(context, null, false);
      this.inflater = LayoutInflater.from(context);
      this.layoutId = layoutId;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
      View view = inflater.inflate(layoutId, parent, false);
      ViewHolder vh = new ViewHolder(view);
      view.setTag(vh);
      return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
      final ViewHolder vh = (ViewHolder) view.getTag();
      Note note = Note.fromCursor(cursor);
      if (!note.isTrashed()) {
        vh.parent.setVisibility(View.GONE);
        vh.parent.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
      }
      vh.title.setText(note.getTitle());
      // Make date time more readable
      DateTime dt = new DateTime(note.getDeletedTime());
      vh.date.setText(DateTimeUtility.getReminderReadableDate(dt) + " @ " + DateTimeUtility.getReminderReadableTime(dt.getHourOfDay(), dt.getMinuteOfHour()));
    }
  }
}
