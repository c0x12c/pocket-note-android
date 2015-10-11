package chan.android.app.pocketnote.app;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable, Comparable<Note> {

  /**
   * Use this key to send note to any other activity/fragment
   */
  public static final String BUNDLE_KEY = "note_bundle_key";

  public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {

    @Override
    public Note createFromParcel(Parcel in) {
      return new Note(in);
    }

    @Override
    public Note[] newArray(int size) {
      return new Note[size];
    }
  };
  /**
   * Title of the note
   */
  private String title;
  /**
   * The body of the note
   */
  private String content;
  /**
   * The time the note is being modified in milliseconds.
   * Anytime user makes a change to either: 'title', 'content' or 'color', we update this modified time.
   * We also use this time to update/query/delete note because it's unique. Using _id field would be
   * annoying every time we create a new note, we have to somehow update the id but id should be final
   * because it's unique to the current instance of note. Thus, using modifiedTime would save us
   * a lot of headache :)
   */
  private long modifiedTime;
  /**
   * The current color of note
   */
  private int color;
  /**
   * The time when the note is removed (go to trash) in milliseconds
   */
  private long deletedTime;
  /**
   * Indicate the note is being removed temporarily.
   * Instead of actual deletion the note, we flag it on or off.
   */
  private boolean trashed;
  /**
   * Indicate note is password protected
   */
  private boolean locked;
  /**
   * For mark a note as read
   */
  private boolean checked;
  /**
   * We store reminder as JSON string to make note structure simple to parse
   * and it matches with Sqlite DB
   */
  private String reminder;
  private int day;
  private int month;
  private int year;
  private int id;

  public Note(int id, String title, String content, long modifiedTime, int color) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.modifiedTime = modifiedTime;
    this.color = color;
    this.deletedTime = 0;
    this.trashed = false;
    this.locked = false;
    this.checked = false;
    this.reminder = null;
    this.year = -1;
    this.month = -1;
    this.year = -1;
  }

  public Note(String title, String content, long modifiedTime, int color) {
    this.title = title;
    this.content = content;
    this.modifiedTime = modifiedTime;
    this.color = color;
    this.deletedTime = 0;
    this.trashed = false;
    this.locked = false;
    this.checked = false;
    this.reminder = null;
    this.year = -1;
    this.month = -1;
    this.year = -1;
  }

  public Note(Parcel in) {
    id = in.readInt();
    title = in.readString();
    content = in.readString();
    modifiedTime = in.readLong();
    color = in.readInt();
    deletedTime = in.readLong();
    trashed = (in.readByte() != 0);
    locked = (in.readByte() != 0);
    checked = (in.readByte() != 0);
    reminder = in.readString();
    year = in.readInt();
    month = in.readInt();
    day = in.readInt();
  }

  public long getDeletedTime() {
    return deletedTime;
  }

  public void setDeletedTime(long milliseconds) {
    deletedTime = milliseconds;
  }

  public boolean isChecked() {
    return checked;
  }

  public void setChecked(boolean yesOrNo) {
    checked = yesOrNo;
  }

  public boolean isTrashed() {
    return trashed;
  }

  public void setTrashed(boolean yesOrNo) {
    trashed = yesOrNo;
  }

  public long getModifiedTime() {
    return modifiedTime;
  }

  public void setModifiedTime(long modifiedTime) {
    this.modifiedTime = modifiedTime;
  }

  public boolean isLocked() {
    return locked;
  }

  public void setLocked(boolean yesOrNo) {
    this.locked = yesOrNo;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public String getReminder() {
    return reminder;
  }

  public void setReminder(String reminder) {
    this.reminder = reminder;
  }

  public int getDay() {
    return day;
  }

  public void setDay(int day) {
    this.day = day;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Note)) return false;

    Note note = (Note) o;

    if (color != note.color) return false;
    if (modifiedTime != note.modifiedTime) return false;
    if (content != null ? !content.equals(note.content) : note.content != null) return false;
    if (title != null ? !title.equals(note.title) : note.title != null) return false;

    return true;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(title);
    dest.writeString(content);
    dest.writeLong(modifiedTime);
    dest.writeInt(color);
    dest.writeLong(deletedTime);
    dest.writeByte((byte) (trashed ? 1 : 0));
    dest.writeByte((byte) (locked ? 1 : 0));
    dest.writeByte((byte) (checked ? 1 : 0));
    if (reminder != null) {
      dest.writeString(reminder);
    }

    dest.writeInt(year);
    dest.writeInt(month);
    dest.writeInt(day);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public int hashCode() {
    int result = title != null ? title.hashCode() : 0;
    result = 31 * result + (content != null ? content.hashCode() : 0);
    result = 31 * result + (int) (modifiedTime ^ (modifiedTime >>> 32));
    result = 31 * result + color;
    result = 31 * result + (int) (deletedTime ^ (deletedTime >>> 32));
    result = 31 * result + (trashed ? 1 : 0);
    result = 31 * result + (locked ? 1 : 0);
    result = 31 * result + (checked ? 1 : 0);
    result = 31 * result + (reminder != null ? reminder.hashCode() : 0);
    return result;
  }

  @Override
  public int compareTo(Note that) {
    if (this.getModifiedTime() > that.getModifiedTime()) {
      return 1;
    } else if (this.getModifiedTime() < that.getModifiedTime()) {
      return -1;
    }
    return 0;
  }
}
