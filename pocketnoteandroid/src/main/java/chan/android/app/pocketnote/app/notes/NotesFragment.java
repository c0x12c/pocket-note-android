package chan.android.app.pocketnote.app.notes;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.*;
import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.AppPreferences;
import chan.android.app.pocketnote.app.Note;
import chan.android.app.pocketnote.app.db.NoteContentProvider;
import chan.android.app.pocketnote.app.db.NoteDbTable;
import chan.android.app.pocketnote.app.settings.PasswordDialogFragment;
import chan.android.app.pocketnote.util.Logger;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class NotesFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor> {

  public static final String TAG = "notes";

  private static final int FLIPPER_LIST_VIEW = 0;

  private static final int FLIPPER_GRID_VIEW = 1;

  private static final int FLIPPER_EMPTY = 2;

  private static final int FLIPPER_NOT_FOUND = 3;

  private ViewFlipper viewFlipper;

  private NoteCursorAdapter listAdapter;

  private NoteCursorAdapter gridAdapter;

  private ListView listView;

  private GridView gridView;

  private ImageView optionButton;

  private String currentSortByColumn = NoteDbTable.COLUMN_MODIFIED_TIME;

  private String currentSortOrder = "DESC";

  private String currentSearchQuery = "";

  private LayoutAnimationController layoutAnimationController;

  private LinearLayout linearLayoutEmpty;

  private int currentCollectionViewIndex = 0;

  public static NotesFragment newInstance() {
    return new NotesFragment();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    menu.clear();
    inflater.inflate(R.menu.notes, menu);
    SearchView searchView = (SearchView) menu.findItem(R.id.notes_menu_$_search).getActionView();
    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        if (query.isEmpty()) {
          restartLoader(null);
        } else {
          Bundle bundle = new Bundle();
          bundle.putString("query", buildSearchQuery(query));
          bundle.putInt("collection_view_index", currentCollectionViewIndex);
          restartLoader(bundle);
        }
        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
          restartLoader(null);
        }
        return true;
      }

      private String buildSearchQuery(String query) {
        StringBuilder sb = new StringBuilder();
        sb.append(" AND ");
        sb.append(AppPreferences.getDefaultAlphabetSortColumn());
        sb.append(" LIKE '%");
        sb.append(query);
        sb.append("%'");
        return sb.toString();
      }
    };
    searchView.setOnQueryTextListener(listener);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    switch (item.getItemId()) {
      case R.id.notes_menu_$_add:
        startActivity(new Intent(getSherlockActivity(), EditNoteActivity.class));
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getLoaderManager().initLoader(0, null, this);
    gridAdapter = new NoteCursorAdapter(getActivity(), R.layout.note_grid_item);
    listAdapter = new NoteCursorAdapter(getActivity(), R.layout.note_list_item);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Logger.e("NoteFragment.onCreateView()");
    View root = inflater.inflate(R.layout.notes, container, false);

    // View flipper to flip between list view and grid view
    viewFlipper = (ViewFlipper) root.findViewById(R.id.notes_$_viewflipper);

    // Empty layout click add note
    linearLayoutEmpty = (LinearLayout) root.findViewById(R.id.notes_$_linearlayout_empty);
    linearLayoutEmpty.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(getSherlockActivity(), EditNoteActivity.class));
      }
    });

    // Default view
    viewFlipper.setDisplayedChild(FLIPPER_LIST_VIEW);

    // Prepare animation
    layoutAnimationController = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.notes_layout_anim);

    // Prepare grid view
    gridView = (GridView) root.findViewById(R.id.notes_$_gridview);
    gridView.setAdapter(gridAdapter);
    gridView.setOnItemLongClickListener(new OnLongClickNoteListener(this, gridAdapter));
    gridView.setOnItemClickListener(new OnClickNoteListener(this, gridAdapter));
    gridView.setLayoutAnimation(layoutAnimationController);

    // Prepare list view
    listView = (ListView) root.findViewById(R.id.notes_$_listview);
    listView.setAdapter(listAdapter);
    listView.setOnItemLongClickListener(new OnLongClickNoteListener(this, listAdapter));
    listView.setOnItemClickListener(new OnClickNoteListener(this, listAdapter));
    listView.setLayoutAnimation(layoutAnimationController);

    // To change view or sort item
    optionButton = (ImageView) root.findViewById(R.id.notes_$_imageview_sticky);
    optionButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        OptionsTabDialogFragment d = new OptionsTabDialogFragment();
        d.show(fm, OptionsTabDialogFragment.TAG);
        d.addPickOptionListener(new OptionsTabDialogFragment.PickOptionListener() {
          @Override
          public void onPick(OptionsTabDialogFragment.Option option) {
            if ("As list view".equals(option.getTitle())) {
              currentCollectionViewIndex = 0;
              viewFlipper.setDisplayedChild(currentCollectionViewIndex);
            } else if ("As grid view".equals(option.getTitle())) {
              currentCollectionViewIndex = 1;
              viewFlipper.setDisplayedChild(currentCollectionViewIndex);
            } else if ("By modified time".equals(option.getTitle())) {
              sortBy(currentSortOrder, currentSortByColumn, NoteDbTable.COLUMN_MODIFIED_TIME, currentCollectionViewIndex);
            } else if ("By alphabet".equals(option.getTitle())) {
              sortBy(currentSortOrder, currentSortByColumn, NoteDbTable.COLUMN_TITLE, currentCollectionViewIndex);
            } else if ("By color".equals(option.getTitle())) {
              sortBy(currentSortOrder, currentSortByColumn, NoteDbTable.COLUMN_COLOR, currentCollectionViewIndex);
            }
          }
        });
      }
    });

    // Make sure we update from preferences
    checkPreferences();

    return root;
  }

  public void onResume() {
    checkPreferences();
    restartLoader(null);
    super.onResume();
  }

  private void checkPreferences() {
    // Update sort order
    final int sortBy = AppPreferences.getDefaultSortBy();
    if (sortBy == 0) {
      currentSortByColumn = NoteDbTable.COLUMN_MODIFIED_TIME;
    } else if (sortBy == 1) {
      currentSortByColumn = NoteDbTable.COLUMN_CONTENT;
    } else if (sortBy == 2) {
      currentSortByColumn = NoteDbTable.COLUMN_COLOR;
    }

    currentCollectionViewIndex = AppPreferences.getDefaultCollectionView();
  }

  private void restartLoader(Bundle bundle) {
    getLoaderManager().restartLoader(0, bundle, this);
    if (bundle != null) {
      listAdapter.notifyDataSetChanged();
      listView.startLayoutAnimation();
      gridAdapter.notifyDataSetChanged();
      gridView.startLayoutAnimation();
    }
  }

  private void sortBy(String oldSortOrder, String oldColumn, String newColumn, int viewIndex) {
    Bundle bundle = new Bundle();
    bundle.putString("old_column", oldColumn);
    bundle.putString("new_column", newColumn);
    bundle.putString("old_sort_order", oldSortOrder);
    bundle.putInt("collection_view_index", viewIndex);
    restartLoader(bundle);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
    Logger.e("onCreateLoader()");
    if (bundle != null) {
      // Handle sorting
      String oldColumn = bundle.getString("old_column");
      String newColumn = bundle.getString("new_column");
      if (oldColumn != null && newColumn != null) {
        if (oldColumn.equals(newColumn)) {
          currentSortOrder = currentSortOrder.equals("DESC") ? "ASC" : "DESC";
        } else {
          currentSortOrder = "DESC";
        }
        currentSortByColumn = newColumn;
      }

      // Handle searching
      if (bundle.getString("query") != null) {
        currentSearchQuery = bundle.getString("query");
      }

      // Previous collection view
      currentCollectionViewIndex = bundle.getInt("collection_view_index");
    } else {
      currentSearchQuery = "";
    }
    return new CursorLoader(getActivity(),
      NoteContentProvider.CONTENT_URI,
      NoteContentProvider.COLUMNS,
      NoteDbTable.COLUMN_TRASHED + "=0 AND " +
        // Either day or month or year equals to 0 is good enough
        NoteDbTable.COLUMN_CALENDAR_YEAR + "=-1 "
        + currentSearchQuery,
      null,
      currentSortByColumn + " " + currentSortOrder);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    Logger.e("onLoadFinished()");
    listAdapter.swapCursor(cursor);
    gridAdapter.swapCursor(cursor);
    if (listAdapter.getCount() == 0 && gridAdapter.getCount() == 0) {
      Logger.e("onLoadFinished() = both empty?");
      if (currentSearchQuery.equals("")) {
        viewFlipper.setDisplayedChild(FLIPPER_EMPTY);
      } else {
        viewFlipper.setDisplayedChild(FLIPPER_NOT_FOUND);
      }
    } else {
      viewFlipper.setDisplayedChild(currentCollectionViewIndex);
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> cursorLoader) {
    Logger.e("onLoaderReset()");
    listAdapter.swapCursor(null);
    gridAdapter.swapCursor(null);
  }

  private static class OnClickNoteListener implements AdapterView.OnItemClickListener {

    private CursorAdapter cursorAdapter;

    private Fragment fragment;

    public OnClickNoteListener(Fragment fragment, CursorAdapter adapter) {
      this.cursorAdapter = adapter;
      this.fragment = fragment;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      final Cursor cursor = (Cursor) cursorAdapter.getItem(position);
      final Note note = Note.fromCursor(cursor);
      if (note.isLocked()) {
        final PasswordDialogFragment d = new PasswordDialogFragment();
        d.show(fragment.getFragmentManager(), "password");
        d.setOnPasswordEnterListener(new PasswordDialogFragment.OnPasswordEnterListener() {
          @Override
          public void onEnter(String password) {
            if (AppPreferences.hasCorrectPassword(password)) {
              d.dismiss();
              launchEditNoteActivity(note);
            } else {
              d.showErrorMessage("Your password is incorrect!");
            }
          }
        });
      } else {
        launchEditNoteActivity(note);
      }
    }

    private void launchEditNoteActivity(Note note) {
      Intent intent = new Intent(fragment.getActivity(), EditNoteActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.putExtra(Note.BUNDLE_KEY, note);
      fragment.startActivity(intent);
    }
  }
}
