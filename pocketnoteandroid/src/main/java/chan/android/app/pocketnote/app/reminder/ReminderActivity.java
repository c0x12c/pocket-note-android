package chan.android.app.pocketnote.app.reminder;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.Note;
import chan.android.app.pocketnote.app.db.PocketNoteManager;
import chan.android.app.pocketnote.app.settings.SettingItemAdapter;
import chan.android.app.pocketnote.app.trash.ConfirmDialogFragment;
import chan.android.app.pocketnote.util.DateTimeUtility;
import chan.android.app.pocketnote.util.TextUtility;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ReminderActivity extends AppCompatActivity {

  static final String[] TYPES = new String[]{
    Reminder.Type.ALL_DAY.description,
    Reminder.Type.TIME_ALARM.description,
    Reminder.Type.PIN_TO_STATUS_BAR.description,
  };
  static final String[] REPETITIONS = new String[]{
    Reminder.Repetition.ONE_TIME.description,
    Reminder.Repetition.HOURLY.description,
    Reminder.Repetition.DAILY.description,
    Reminder.Repetition.WEEKLY.description,
    Reminder.Repetition.MONTHLY.description,
    Reminder.Repetition.YEARLY.description
  };
  static final String[] TIMES = new String[]{
    TimeAlarmOption.MINUTE_5.description,
    TimeAlarmOption.MINUTE_10.description,
    TimeAlarmOption.MINUTE_15.description,
    TimeAlarmOption.MINUTE_20.description,
    TimeAlarmOption.MINUTE_25.description,
    TimeAlarmOption.MINUTE_30.description,
    TimeAlarmOption.MINUTE_45.description,
    TimeAlarmOption.HOUR_1.description,
    TimeAlarmOption.HOUR_2.description,
    TimeAlarmOption.HOUR_3.description,
    TimeAlarmOption.HOUR_6.description,
    TimeAlarmOption.HOUR_12.description,
    TimeAlarmOption.HOUR_24.description,
    TimeAlarmOption.SPECIFIC.description,
  };
  /**
   * Cached all enums since values() is expensive operation
   */
  static final TimeAlarmOption[] TIMES_VALUES = TimeAlarmOption.values();
  static final Reminder.Repetition[] REPETITIONS_VALUES = Reminder.Repetition.values();
  private static final int FLIPPER_ALL_DAY = 0;
  private static final int FLIPPER_TIME_ALARM = 1;
  private static final int FLIPPER_PIN_STATUS = 2;
  private final View.OnClickListener[] saveClickHandlers = new View.OnClickListener[]{
    new AllDayOnSaveClick(),
    new TimeAlarmOnSaveClick(),
    new PinToStatusBarOnSaveClick(),
  };
  /**
   * All day event
   */
  private Spinner allDaySpinnerWhen;

  private Spinner allDaySpinnerRepetition;

  private Button allDayButtonDatePicker;

  private TextView allDayTextViewEnd;

  private Button allDayButtonEnd;

  /**
   * Time alarm event
   */
  private Spinner timeAlarmSpinnerWhen;

  private Spinner timeAlarmSpinnerRepetition;

  private Button timeAlarmButtonDatePicker;

  private Button timeAlarmButtonTimePicker;

  private TextView timeAlarmTextViewEnd;

  private Button timeAlarmButtonEnd;

  /**
   * Common UI to all events
   */
  private Button buttonCancel;

  private Button buttonReset;

  private Button buttonSave;

  private Note editingNote;

  private ViewFlipper viewFlipper;

  private Spinner spinnerType;

  private NotificationCenter notificationCenter;

  private Calendar calendarWhen = Calendar.getInstance();

  private Calendar calendarEnd = Calendar.getInstance();

  private TextView textViewType;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    final MenuInflater inflater = getMenuInflater();
    if (hasReminder()) {
      inflater.inflate(R.menu.reminder, menu);
    } else {
      inflater.inflate(R.menu.blank, menu);
    }
    return super.onCreateOptionsMenu(menu);
  }

  private boolean hasReminder() {
    return !TextUtility.isNullOrEmpty(editingNote.getReminder());
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.reminder_menu_$_dismiss: {
        ConfirmDialogFragment d = new ConfirmDialogFragment("Dismiss reminder for this event", "Cancel", "Yes");
        d.setOnConfirmListener(new ConfirmDialogFragment.OnConfirmListener() {
          @Override
          public void onEnter(boolean ok) {
            if (ok) {
              Reminder r = Reminder.fromJson(editingNote.getReminder());
              // Remove from alarm manager
              if (r.getType() != Reminder.Type.PIN_TO_STATUS_BAR) {
                AbstractReminderScheduler scheduler = NoteReminderScheduler.getScheduler(ReminderActivity.this);
                scheduler.cancel(editingNote);
              }

              // Remove notification
              NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
              PocketNoteManager noteManager = PocketNoteManager.getPocketNoteManager();
              manager.cancel(noteManager.getId(editingNote));

              // Remove from DB
              PocketNoteManager.getPocketNoteManager().removeReminder(editingNote);

              // OK, go back
              finish();
            }
          }
        });
        d.show(getSupportFragmentManager(), "dismiss_dialog");
        break;
      }

      // TODO: Should we display more detail message?
      case R.id.reminder_menu_$_info:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.reminder);

    notificationCenter = new NotificationCenter();

    // Common for all event type
    viewFlipper = (ViewFlipper) findViewById(R.id.reminder_$_spinner_god_father);
    spinnerType = (Spinner) findViewById(R.id.reminder_$_spinner_type);
    buttonCancel = (Button) findViewById(R.id.reminder_$_button_cancel);
    buttonReset = (Button) findViewById(R.id.reminder_$_button_reset);
    buttonSave = (Button) findViewById(R.id.reminder_$_button_save);
    textViewType = (TextView) findViewById(R.id.reminder_$_textview_type);

    // If reminder has expired, show this icon otherwise hide it
    textViewType.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(ReminderActivity.this, "Event has expired", Toast.LENGTH_LONG).show();
      }
    });

    spinnerType.setAdapter(new SettingItemAdapter(this, Arrays.asList(TYPES)));
    spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        viewFlipper.setDisplayedChild(position);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        // Ignored
      }
    });

    buttonCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    buttonReset.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
      }
    });

    buttonSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        saveClickHandlers[viewFlipper.getDisplayedChild()].onClick(v);
      }
    });

    setUpAllDayUi();
    setUpTimeAlarmUi();

    // Parse note from bundle
    if (getIntent().getExtras() != null) {
      editingNote = getIntent().getExtras().getParcelable(Note.BUNDLE_KEY);
      if (!TextUtility.isNullOrEmpty(editingNote.getReminder())) {
        Reminder r = Reminder.fromJson(editingNote.getReminder());
        // Delegate this option to spinner type select event
        if (r.getType() == Reminder.Type.PIN_TO_STATUS_BAR) {
          spinnerType.setSelection(FLIPPER_PIN_STATUS);
        } else if (r.getType() == Reminder.Type.ALL_DAY) {
          spinnerType.setSelection(FLIPPER_ALL_DAY);
          populateAllDay(r);
        } else if (r.getType() == Reminder.Type.TIME_ALARM) {
          spinnerType.setSelection(FLIPPER_TIME_ALARM);
          populateTimeAlarm(r);
        }

        if (r.isExpired()) {
          textViewType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_icon_tiny_alert, 0);
          textViewType.setClickable(true);
        } else {
          textViewType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
          textViewType.setClickable(false);
        }
      } else {
        textViewType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        textViewType.setClickable(false);
      }
    }
  }

  private void populateAllDay(Reminder reminder) {
    allDaySpinnerRepetition.setSelection(reminder.getRepetition().ordinal());
    allDaySpinnerWhen.setSelection(reminder.getWhenIndex());
    if (allDaySpinnerWhen.getSelectedItem().toString().equals(TimeAlarmOption.SPECIFIC.description)) {
      DateTime beginDate = new DateTime(reminder.getBegin());
      calendarWhen.setTime(beginDate.toDate());
      allDayButtonDatePicker.setText(DateTimeUtility.getReminderReadableDate(beginDate));
    }
    if (reminder.getEnd() == 0) {
      allDayButtonEnd.setText("Never");
    } else {
      DateTime endDate = new DateTime(reminder.getEnd());
      calendarEnd.setTime(endDate.toDate());
      allDayButtonEnd.setText(DateTimeUtility.getReminderReadableDate(endDate));
    }
  }

  private void populateTimeAlarm(Reminder reminder) {
    timeAlarmSpinnerRepetition.setSelection(reminder.getRepetition().ordinal());
    timeAlarmSpinnerWhen.setSelection(reminder.getWhenIndex());
    if (timeAlarmSpinnerWhen.getSelectedItem().toString().equals(TIMES[TIMES.length - 1])) {
      DateTime beginDate = new DateTime(reminder.getBegin());
      calendarWhen.setTime(beginDate.toDate());
      timeAlarmButtonDatePicker.setText(DateTimeUtility.getReminderReadableDate(beginDate));
      timeAlarmButtonTimePicker.setText(DateTimeUtility.getReminderReadableTime(beginDate.getHourOfDay(), beginDate.getMinuteOfHour()));
    }
    if (reminder.getEnd() == 0) {
      timeAlarmButtonEnd.setText("Never");
    } else {
      DateTime endDate = new DateTime(reminder.getEnd());
      calendarEnd.setTime(endDate.toDate());
      timeAlarmButtonEnd.setText(DateTimeUtility.getReminderReadableDate(endDate));
    }
  }

  private void toggleEndDateView(View text, View button, int position) {
    if (REPETITIONS_VALUES[position] == Reminder.Repetition.ONE_TIME) {
      text.setVisibility(View.GONE);
      button.setVisibility(View.GONE);
    } else {
      text.setVisibility(View.VISIBLE);
      button.setVisibility(View.VISIBLE);
    }
  }

  private void setUpAllDayUi() {
    allDaySpinnerWhen = (Spinner) findViewById(R.id.reminder_$_spinner_all_day_when);
    allDayTextViewEnd = (TextView) findViewById(R.id.reminder_$_textview_all_day_pick_end_date);
    allDayButtonEnd = (Button) findViewById(R.id.reminder_$_button_all_day_pick_end_date);
    allDaySpinnerRepetition = (Spinner) findViewById(R.id.reminder_$_spinner_all_day_repetition);
    allDayButtonDatePicker = (Button) findViewById(R.id.reminder_$_button_pick_a_date);

    allDayButtonDatePicker.setVisibility(View.GONE);

    final DateTime now = new DateTime();
    final String[] days = DateTimeUtility.getAllDaysCycleFrom(now);
    allDaySpinnerWhen.setAdapter(new SettingItemAdapter(this, Arrays.asList(days)));
    allDaySpinnerWhen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (days[position].equals("Specific date")) {
          allDayButtonDatePicker.setVisibility(View.VISIBLE);
        } else {
          allDayButtonDatePicker.setVisibility(View.GONE);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    allDayTextViewEnd.setVisibility(View.GONE);
    allDayButtonEnd.setVisibility(View.GONE);

    allDaySpinnerRepetition.setAdapter(new SettingItemAdapter(this, Arrays.asList(REPETITIONS)));
    allDaySpinnerRepetition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        toggleEndDateView(allDayTextViewEnd, allDayButtonEnd, position);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    allDayButtonDatePicker.setText(DateTimeUtility.getReminderReadableDate(now));
    allDayButtonDatePicker.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(ReminderActivity.this,
          new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
              c.set(year, monthOfYear, dayOfMonth);
              DateTime newDate = new DateTime(c.getTime());
              allDayButtonDatePicker.setText(DateTimeUtility.getReminderReadableDate(newDate));

              // Update this calendar for scheduler
              calendarWhen.set(year, monthOfYear, dayOfMonth);
            }
          },
          c.get(Calendar.YEAR),
          c.get(Calendar.MONTH),
          c.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
      }
    });

    allDayButtonEnd.setText("Never");
    allDayButtonEnd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(ReminderActivity.this,
          new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
              c.set(year, monthOfYear, dayOfMonth);
              DateTime newDate = new DateTime(c.getTime());
              allDayButtonEnd.setText(DateTimeUtility.getReminderReadableDate(newDate));
              calendarEnd.set(year, monthOfYear, dayOfMonth);
            }
          },
          c.get(Calendar.YEAR),
          c.get(Calendar.MONTH),
          c.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
      }
    });
  }

  private void setUpTimeAlarmUi() {
    timeAlarmSpinnerWhen = (Spinner) findViewById(R.id.reminder_$_spinner_time_alarm_when);
    timeAlarmButtonDatePicker = (Button) findViewById(R.id.reminder_$_button_time_alarm_pick_date);
    timeAlarmButtonTimePicker = (Button) findViewById(R.id.reminder_$_button_time_alarm_pick_time);
    timeAlarmSpinnerRepetition = (Spinner) findViewById(R.id.reminder_$_spinner_time_alarm_repetition);
    timeAlarmTextViewEnd = (TextView) findViewById(R.id.reminder_$_textview_time_alarm_pick_end_date);
    timeAlarmButtonEnd = (Button) findViewById(R.id.reminder_$_button_time_alarm_pick_end_date);

    timeAlarmTextViewEnd.setVisibility(View.GONE);
    timeAlarmButtonEnd.setVisibility(View.GONE);
    timeAlarmButtonDatePicker.setVisibility(View.GONE);
    timeAlarmButtonTimePicker.setVisibility(View.GONE);

    timeAlarmSpinnerWhen.setAdapter(new SettingItemAdapter(this, Arrays.asList(TIMES)));
    timeAlarmSpinnerWhen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (TIMES_VALUES[position] == TimeAlarmOption.SPECIFIC) {
          timeAlarmButtonDatePicker.setVisibility(View.VISIBLE);
          timeAlarmButtonTimePicker.setVisibility(View.VISIBLE);
        } else {
          timeAlarmButtonDatePicker.setVisibility(View.GONE);
          timeAlarmButtonTimePicker.setVisibility(View.GONE);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    timeAlarmSpinnerRepetition.setAdapter(new SettingItemAdapter(this, Arrays.asList(REPETITIONS)));
    timeAlarmSpinnerRepetition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        toggleEndDateView(timeAlarmTextViewEnd, timeAlarmButtonEnd, position);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });


    // For both time and date
    DateTime now = new DateTime();
    timeAlarmButtonTimePicker.setText(DateTimeUtility.getReminderReadableTime(now.getHourOfDay(), now.getMinuteOfHour()));
    timeAlarmButtonTimePicker.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(ReminderActivity.this,
          new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
              c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
              calendarWhen.set(
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE));
              timeAlarmButtonTimePicker.setText(DateTimeUtility.getReminderReadableTime(hourOfDay, minute));
            }
          },
          c.get(Calendar.HOUR_OF_DAY),
          c.get(Calendar.MINUTE),
          true
        );
        dialog.show();
      }
    });

    timeAlarmButtonDatePicker.setText(DateTimeUtility.getReminderReadableDate(now));
    timeAlarmButtonDatePicker.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(ReminderActivity.this,
          new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
              c.set(year, monthOfYear, dayOfMonth, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
              DateTime newDate = new DateTime(c.getTime());
              timeAlarmButtonDatePicker.setText(DateTimeUtility.getReminderReadableDate(newDate));
              calendarWhen.set(
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE));
            }
          },
          c.get(Calendar.YEAR),
          c.get(Calendar.MONTH),
          c.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
      }
    });

    timeAlarmButtonEnd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(ReminderActivity.this,
          new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
              c.set(Calendar.YEAR, year);
              c.set(Calendar.MONTH, monthOfYear);
              c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
              DateTime newDate = new DateTime(c.getTime());
              timeAlarmButtonEnd.setText(DateTimeUtility.getReminderReadableDate(newDate));
              calendarEnd.set(year, monthOfYear, dayOfMonth);
            }
          },
          calendarEnd.get(Calendar.YEAR),
          calendarEnd.get(Calendar.MONTH),
          calendarEnd.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
      }
    });

  }

  private void quit() {
    toast("Reminder is set");
    finish();
  }

  private void toast(String text) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show();
  }

  private void schedule(Reminder r) {
    PocketNoteManager manager = PocketNoteManager.getPocketNoteManager();
    AbstractReminderScheduler scheduler = NoteReminderScheduler.getScheduler(ReminderActivity.this);
    manager.addReminder(editingNote, Reminder.toJson(r));
    scheduler.schedule(editingNote);
  }

  private enum TimeAlarmOption {
    MINUTE_5("5 minutes", TimeUnit.MINUTES.toMillis(5)),
    MINUTE_10("10 minutes", TimeUnit.MINUTES.toMillis(10)),
    MINUTE_15("15 minutes", TimeUnit.MINUTES.toMillis(15)),
    MINUTE_20("20 minutes", TimeUnit.MINUTES.toMillis(20)),
    MINUTE_25("25 minutes", TimeUnit.MINUTES.toMillis(25)),
    MINUTE_30("30 minutes", TimeUnit.MINUTES.toMillis(30)),
    MINUTE_45("45 minutes", TimeUnit.MINUTES.toMillis(45)),
    HOUR_1("1 hour", TimeUnit.HOURS.toMillis(1)),
    HOUR_2("2 hour", TimeUnit.HOURS.toMillis(2)),
    HOUR_3("3 hour", TimeUnit.HOURS.toMillis(3)),
    HOUR_6("6 hour", TimeUnit.HOURS.toMillis(6)),
    HOUR_12("12 hour", TimeUnit.HOURS.toMillis(12)),
    HOUR_24("24 hour", TimeUnit.HOURS.toMillis(24)),
    SPECIFIC("Specific date time", 0);

    final String description;
    final long milliseconds;

    TimeAlarmOption(String description, long milliseconds) {
      this.description = description;
      this.milliseconds = milliseconds;
    }

    public String getDescription() {
      return description;
    }

    public long getMilliseconds() {
      return milliseconds;
    }
  }

  private class PinToStatusBarOnSaveClick implements View.OnClickListener {

    @Override
    public void onClick(View v) {
      notificationCenter.notifySticky(ReminderActivity.this, editingNote);
      Reminder reminder = new Reminder(Reminder.Type.PIN_TO_STATUS_BAR, Reminder.Repetition.ONE_TIME, 0, 0, 0);
      String json = Reminder.toJson(reminder);
      editingNote.setReminder(json);
      PocketNoteManager.getPocketNoteManager().addReminder(editingNote, json);
      quit();
    }
  }

  private class TimeAlarmOnSaveClick implements View.OnClickListener {

    @Override
    public void onClick(View v) {
      Reminder.Repetition repetition = REPETITIONS_VALUES[timeAlarmSpinnerRepetition.getSelectedItemPosition()];
      int whenIndex = timeAlarmSpinnerWhen.getSelectedItemPosition();
      TimeAlarmOption option = TIMES_VALUES[whenIndex];

      // Parse begin
      long begin = 0L;
      if (option == TimeAlarmOption.SPECIFIC) {
        begin = calendarWhen.getTimeInMillis();
      } else {
        begin = System.currentTimeMillis() + option.getMilliseconds();
      }

      // Parse end
      long end = 0L;
      if (!timeAlarmButtonEnd.getText().toString().equals("Never")) {
        end = calendarEnd.getTimeInMillis();
      }

      schedule(new Reminder(Reminder.Type.TIME_ALARM, repetition, begin, end, whenIndex));
      quit();
    }
  }

  private class AllDayOnSaveClick implements View.OnClickListener {

    /**
     * Add an extra time buffer of 1 second
     */
    private static final long TIME_BUFFER = 1000;

    private static final long SPECIFIC_DATE_INDEX = 7;

    @Override
    public void onClick(View v) {
      Reminder.Repetition repetition = REPETITIONS_VALUES[allDaySpinnerRepetition.getSelectedItemPosition()];
      int whenIndex = allDaySpinnerWhen.getSelectedItemPosition();

      // Parse begin
      long begin = 0L;
      if (whenIndex == SPECIFIC_DATE_INDEX) {
        begin = calendarWhen.getTimeInMillis();
      } else {
        begin = System.currentTimeMillis() + TIME_BUFFER + TimeUnit.DAYS.toMillis(whenIndex);
      }

      // Parse end
      long end = 0L;
      if (!allDayButtonEnd.getText().toString().equals("Never")) {
        end = calendarEnd.getTimeInMillis();
      }

      schedule(new Reminder(Reminder.Type.ALL_DAY, repetition, begin, end, whenIndex));
      quit();
    }
  }
}
