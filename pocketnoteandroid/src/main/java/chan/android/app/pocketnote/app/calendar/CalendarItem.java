package chan.android.app.pocketnote.app.calendar;


import chan.android.app.pocketnote.app.Note;

import java.util.ArrayList;
import java.util.List;

class CalendarItem {

  private final int day;

  private final int month;

  private final int year;

  private List<Note> notes;

  private boolean ignored;

  public CalendarItem(int day, int month, int year, boolean ignored) {
    this.day = day;
    this.month = month;
    this.year = year;
    this.ignored = ignored;
    notes = new ArrayList<Note>();
  }

  public void addNote(Note note) {
    notes.add(note);
  }

  public boolean isIgnored() {
    return ignored;
  }

  public void setIgnored(boolean ignored) {
    this.ignored = ignored;
  }

  public List<Note> getNotes() {
    return notes;
  }

  public void setNotes(List<Note> notes) {
    this.notes = notes;
  }

  public int getDay() {
    return day;
  }

  public int getMonth() {
    return month;
  }

  public int getYear() {
    return year;
  }

}
