package chan.android.app.pocketnote.app.calendar;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.AppPreferences;
import chan.android.app.pocketnote.app.Note;
import chan.android.app.pocketnote.app.db.NoteManager;
import chan.android.app.pocketnote.app.db.PocketNoteManager;
import chan.android.app.pocketnote.app.notes.EditNoteActivity;
import chan.android.app.pocketnote.app.settings.PasswordDialogFragment;
import chan.android.app.pocketnote.app.trash.ConfirmDialogFragment;
import chan.android.app.pocketnote.util.Logger;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import java.util.*;

public class CalendarFragment extends SherlockFragment implements View.OnTouchListener, NoteAdapterNotifier {

  public static final String TAG = "Calendar";
  private static final int FLIPPER_CALENDAR = 0;
  private static final int FLIPPER_SEARCH = 1;
  private static final int FLIPPER_NOT_FOUND = 2;
  private static final String[] MONTH_DESCRIPTION = new String[]{
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"
  };
  private static final int REQUEST_CODE = 4;
  private static final int MAX_DAY_SPOTS = 42;
  public NoteItemAdapter adapterSearch;
  private int month;
  private int year;
  private List<CalendarItem> calendarItems;
  private Map<Integer, List<Note>> noteMap;
  private GridView gridViewDays;
  private TextView textViewMonth;
  private RelativeLayout relativeLayoutRoot;
  private OnSwipeListener swipeListener;
  private CalendarItemAdapter adapter;
  private ViewFlipper viewFlipper;
  private ListView listViewSearch;
  private String savedSearchQuery;

  public static CalendarFragment newInstance() {
    return new CalendarFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    calendarItems = new ArrayList<>();
    noteMap = new HashMap<>();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    menu.clear();
    inflater.inflate(R.menu.calendar, menu);
    SearchView searchView = (SearchView) menu.findItem(R.id.calendar_menu_$_search).getActionView();
    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        if (!query.isEmpty()) {
          savedSearchQuery = query;
          boolean notEmpty = refreshSearchList(query);
          if (notEmpty) {
            viewFlipper.setDisplayedChild(FLIPPER_SEARCH);
          } else {
            viewFlipper.setDisplayedChild(FLIPPER_NOT_FOUND);
          }
        }
        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
          viewFlipper.setDisplayedChild(FLIPPER_CALENDAR);
          refresh(month, year);
        }
        return true;
      }
    };
    searchView.setOnQueryTextListener(listener);
    super.onCreateOptionsMenu(menu, inflater);
  }

  private boolean refreshSearchList(String query) {
    NoteManager manager = PocketNoteManager.getPocketNoteManager();
    List<Note> notes = manager.searchInCalendar(buildSearchQuery(query));
    adapterSearch.setNotes(notes);
    adapterSearch.notifyDataSetChanged();
    return !notes.isEmpty();
  }

  private String buildSearchQuery(String query) {
    StringBuilder sb = new StringBuilder();
    sb.append(AppPreferences.getDefaultAlphabetSortColumn());
    sb.append(" LIKE '%");
    sb.append(query);
    sb.append("%'");
    return sb.toString();
  }

  @Override
  public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onResume() {
    Logger.e("CalendarFragment.onResume()");
    refresh(month, year);
    refreshSearchList(savedSearchQuery);
    super.onResume();
  }

  @Override
  public void onStop() {
    Logger.e("CalendarFragment.onStop()");
    super.onStop();
  }

  @Override
  public void onDestroyView() {
    Logger.e("CalendarFragment.onDestroyView()");
    super.onDestroyView();
  }

  @Override
  public void onDestroy() {
    Logger.e("CalendarFragment.onDestroy()");
    super.onDestroy();
  }

  @Override
  public void onPause() {
    Logger.e("CalendarFragment.onPause()");
    super.onPause();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Logger.e("CalendarFragment.onCreateView()");
    final View root = inflater.inflate(R.layout.calendar, container, false);
    viewFlipper = (ViewFlipper) root.findViewById(R.id.calendar_$_viewflipper);
    listViewSearch = (ListView) root.findViewById(R.id.calendar_$_listview_notes);
    adapterSearch = new NoteItemAdapter(getActivity(), new ArrayList<Note>());
    listViewSearch.setAdapter(adapterSearch);
    listViewSearch.setOnItemLongClickListener(new OnLongClickCalendarNoteSearchListener(this));
    listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Note note = adapterSearch.getItem(position);
        if (note.isLocked()) {
          final PasswordDialogFragment d = new PasswordDialogFragment();
          d.show(getFragmentManager(), "password_dialog");
          d.setOnPasswordEnterListener(new PasswordDialogFragment.OnPasswordEnterListener() {
            @Override
            public void onEnter(String password) {
              if (AppPreferences.hasCorrectPassword(password)) {
                d.dismiss();
                launchEditNoteActivity(note.getDay(), month, year, note);
              } else {
                d.showErrorMessage("Your password is incorrect!");
              }
            }
          });
        } else {
          launchEditNoteActivity(note.getDay(), month, year, note);
        }
      }
    });

    gridViewDays = (GridView) root.findViewById(R.id.calendar_$_gridview);
    textViewMonth = (TextView) root.findViewById(R.id.calendar_$_textview_month);
    relativeLayoutRoot = (RelativeLayout) root.findViewById(R.id.calendar_$_linearlayout_parent);
    swipeListener = new OnSwipeListener(getSherlockActivity(), new Swiper() {
      @Override
      public void onSwipeLeft() {
        Logger.e("Swipe left");
      }

      @Override
      public void onSwipeRight() {
        Logger.e("Swipe right");
      }

      @Override
      public void onSwipeUp() {
        Logger.e("Swipe up");
      }

      @Override
      public void onSwipeDown() {
        Logger.e("Swipe down");
      }
    });
    relativeLayoutRoot.setOnTouchListener(this);

    ImageView nextIcon = (ImageView) root.findViewById(R.id.calendar_$_imageview_right);
    nextIcon.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        goToNextMonthYear();
        refresh(month, year);
      }
    });
    ImageView prevIcon = (ImageView) root.findViewById(R.id.calendar_$_imageview_left);
    prevIcon.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        goToPreviousMonthYear();
        refresh(month, year);
      }
    });
    Button next = (Button) root.findViewById(R.id.calendar_$_button_next);
    next.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        goToNextMonthYear();
        refresh(month, year);
      }
    });
    Button prev = (Button) root.findViewById(R.id.calendar_$_button_prev);
    prev.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        goToPreviousMonthYear();
        refresh(month, year);
      }
    });
    Button jump = (Button) root.findViewById(R.id.calendar_$_button_jump);
    jump.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
          getSherlockActivity(),
          new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
              refresh(monthOfYear, year);
            }
          },
          calendar.get(Calendar.YEAR),
          calendar.get(Calendar.MONTH),
          calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
      }
    });

    Calendar calendar = Calendar.getInstance();
    month = calendar.get(Calendar.MONTH);
    year = calendar.get(Calendar.YEAR);
    refresh(month, year);
    return root;
  }

  private void goToNextMonthYear() {
    int nextMonth = nextMonth(month);
    if (nextMonth < month) {
      year++;
    }
    month = nextMonth;
  }

  private void goToPreviousMonthYear() {
    int prevMonth = previousMonth(month);
    if (prevMonth > month) {
      year--;
    }
    month = prevMonth;
  }

  private void refresh(int month, int year) {
    this.month = month;
    this.year = year;
    textViewMonth.setText(MONTH_DESCRIPTION[month] + " " + year);
    loadNotes(month, year);
    buildMonthView(month, year);
    display(month, year);
  }

  private void loadNotes(int month, int year) {
    NoteManager manager = PocketNoteManager.getPocketNoteManager();
    List<Note> notes = manager.getNotes(month, year);
    noteMap.clear();
    for (Note note : notes) {
      if (!noteMap.containsKey(note.getDay())) {
        noteMap.put(note.getDay(), new ArrayList<Note>());
      }
      noteMap.get(note.getDay()).add(note);
    }
  }

  private void buildMonthView(final int month, final int year) {
    // Reset
    calendarItems.clear();

    // Get the current calendar of month and year with day 1
    Calendar calendar = new GregorianCalendar(year, month, 1);
    int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
    int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

    // For previous month
    int previousMonth = previousMonth(month);
    if (month == 0) {
      calendar.set(Calendar.YEAR, year - 1);
    }
    calendar.set(Calendar.MONTH, previousMonth);

    // Save the last day of previous month
    int lastDayInPreviousMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

    // For next month
    int nextMonth = nextMonth(month);
    if (month == 11) {
      calendar.set(Calendar.YEAR, year + 1);
    }
    calendar.set(Calendar.MONTH, nextMonth);

    // Save the first day of next month
    int firstDayInNextMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);

    int totalSpots = MAX_DAY_SPOTS - daysInMonth;
    int previousMonthSpots = weekDay - 1;
    int nextMonthSpots = totalSpots - previousMonthSpots;

    // Add days for previous month
    for (int i = lastDayInPreviousMonth - previousMonthSpots + 1; i <= lastDayInPreviousMonth; ++i) {
      calendarItems.add(new CalendarItem(i, previousMonth, year, true));
    }

    // Add days for current month
    for (int i = 0; i < daysInMonth; ++i) {
      CalendarItem item = new CalendarItem(i + 1, month, year, false);
      item.setNotes(noteMap.get(item.getDay()));
      calendarItems.add(item);
    }

    // Add days for next month
    for (int i = 0; i < nextMonthSpots; ++i) {
      calendarItems.add(new CalendarItem(firstDayInNextMonth++, nextMonth, year, true));
    }
  }

  private int nextMonth(int month) {
    return (month + 1 == 12) ? 0 : (month + 1);
  }

  private int previousMonth(int month) {
    return (month - 1 == -1) ? 11 : (month - 1);
  }

  public void display(final int month, final int year) {
    adapter = new CalendarItemAdapter(getSherlockActivity(), calendarItems);
    gridViewDays.setAdapter(adapter);
    gridViewDays.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final CalendarItem item = calendarItems.get(position);
        if (!item.isIgnored()) {
          final List<Note> notes = item.getNotes();
          if (notes == null || notes.isEmpty()) {
            showConfirmDialog(item.getDay());
          } else {
            final int day = item.getDay();
            displayListDialog(day, month, year, item.getNotes());
          }
        }
      }
    });
  }

  private void displayListDialog(final int day, final int month, final int year, final List<Note> notes) {
    final NoteListDialogFragment d = new NoteListDialogFragment(this, getDayDescription(day, month, year), notes);
    d.setOnDialogClickListener(
      new NoteListDialogFragment.OnDialogClickListener() {
        @Override
        public void onCancel() {
          // Do nothing
        }

        @Override
        public void onAdd() {
          launchEditNoteActivity(day, month, year, null);
        }

        @Override
        public void onEditNote(final Note note) {
          if (note.isLocked()) {
            final PasswordDialogFragment d = new PasswordDialogFragment();
            d.show(getFragmentManager(), "password");
            d.setOnPasswordEnterListener(new PasswordDialogFragment.OnPasswordEnterListener() {
              @Override
              public void onEnter(String password) {
                if (AppPreferences.hasCorrectPassword(password)) {
                  d.dismiss();
                  launchEditNoteActivity(day, month, year, note);
                } else {
                  d.showErrorMessage("Your password is incorrect!");
                }
              }
            });
          } else {
            d.dismiss();
            launchEditNoteActivity(day, month, year, note);
          }
        }

        @Override
        public void onNotesChanged() {
          refresh(month, year);
        }
      }
    );
    d.show(getFragmentManager(), "note_list_dialog");
  }

  private String getDayDescription(int day, int month, int year) {
    return MONTH_DESCRIPTION[month] + " " + day + ", " + year;
  }

  private void showConfirmDialog(final int day) {
    ConfirmDialogFragment d = new ConfirmDialogFragment(
      "Add new note on " + getDayDescription(day, month, year),
      "Cancel",
      "Add");
    d.setOnConfirmListener(new ConfirmDialogFragment.OnConfirmListener() {
      @Override
      public void onEnter(boolean ok) {
        if (ok) {
          launchEditNoteActivity(day, month, year, null);
        }
      }
    });
    d.show(getFragmentManager(), "add_dialog");
  }

  private void launchEditNoteActivity(int day, int month, int year, Note note) {
    Intent intent = new Intent(getSherlockActivity(), EditNoteActivity.class);
    if (note != null) {
      intent.putExtra(Note.BUNDLE_KEY, note);
    } else {
      intent.putExtra("year", year);
      intent.putExtra("month", month);
      intent.putExtra("day", day);
    }
    startActivityForResult(intent, REQUEST_CODE);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    refresh(month, year);
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    return swipeListener.getGestureDetector().onTouchEvent(event);
  }

  @Override
  public void notifyAdapter() {
    adapterSearch.notifyDataSetChanged();
  }

  @Override
  public List<Note> getNotes() {
    return adapterSearch.getNotes();
  }

  @Override
  public void setNotes(List<Note> notes) {
    adapterSearch.setNotes(notes);
    adapterSearch.notifyDataSetChanged();
  }
}
