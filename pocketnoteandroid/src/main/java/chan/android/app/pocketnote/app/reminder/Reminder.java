package chan.android.app.pocketnote.app.reminder;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

public class Reminder {

  /**
   * Share across multiple reminder
   */
  private static final Gson GSON = new Gson();
  private Type type;
  private Repetition repetition = Repetition.ONE_TIME;
  private int whenIndex;
  private long begin;
  private long end;
  public Reminder(Type type, Repetition repetition, long begin, long end, int index) {
    this.type = type;
    this.repetition = repetition;
    this.begin = begin;
    this.end = end;
    this.whenIndex = index;

    if (type == Type.PIN_TO_STATUS_BAR) {
      this.repetition = Repetition.ONE_TIME;
      this.begin = 0;
      this.end = 0;
      this.whenIndex = 0;
    }
  }
  private Reminder() {
    // Internal use only
  }

  public static Reminder fromJson(String json) {
    return GSON.fromJson(json, Reminder.class);
  }

  public static String toJson(Reminder reminder) {
    return GSON.toJson(reminder);
  }

  public int getWhenIndex() {
    return whenIndex;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Repetition getRepetition() {
    return repetition;
  }

  public void setRepetition(Repetition repetition) {
    this.repetition = repetition;
  }

  public long getBegin() {
    return begin;
  }

  public void setBegin(long begin) {
    this.begin = begin;
  }

  public long getEnd() {
    return end;
  }

  public void setEnd(long end) {
    this.end = end;
  }

  @Override
  public String toString() {
    return "Reminder{" +
      "type=" + type +
      ", repetition=" + repetition +
      ", begin='" + begin + '\'' +
      ", end='" + end + '\'' +
      '}';
  }

  public boolean isExpired() {
    // Pin to status bar never expired
    if (type == Type.PIN_TO_STATUS_BAR) {
      return false;
    }

    if (end == 0 && repetition != Repetition.ONE_TIME) {
      return false;
    }

    long now = System.currentTimeMillis();
    if (repetition == Repetition.ONE_TIME) {
      return (now > begin);
    } else {
      return (now > end);
    }
  }

  public enum Repetition {
    ONE_TIME("One time event", 0),
    HOURLY("Hourly", TimeUnit.HOURS.toMillis(1)),
    DAILY("Daily", TimeUnit.DAYS.toMillis(1)),
    WEEKLY("Weekly", TimeUnit.DAYS.toMillis(7)),
    MONTHLY("Monthly", TimeUnit.DAYS.toMillis(30)),
    YEARLY("Yearly", TimeUnit.DAYS.toMillis(365));

    final String description;
    final long milliseconds;

    Repetition(String description, long milliseconds) {
      this.description = description;
      this.milliseconds = milliseconds;
    }

    @Override
    public String toString() {
      return description;
    }

    public long getInterval() {
      return milliseconds;
    }
  }

  public enum Type {
    ALL_DAY("All day"),
    TIME_ALARM("Time alarm"),
    PIN_TO_STATUS_BAR("Pin to status bar");

    final String description;

    Type(String description) {
      this.description = description;
    }

    @Override
    public String toString() {
      return description;
    }
  }
}
