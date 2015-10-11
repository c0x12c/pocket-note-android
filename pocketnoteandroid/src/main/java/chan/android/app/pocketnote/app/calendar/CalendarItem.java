package chan.android.app.pocketnote.app.calendar;


import chan.android.app.pocketnote.app.Note;

import java.util.ArrayList;

class CalendarItem {

  private final int day;

  private final int month;

  private final int year;

  private ArrayList<Note> notes;

  private boolean ignored;

  public CalendarItem(int day, int month, int year, boolean ignored) {
    this.day = day;
    this.month = month;
    this.year = year;
    this.ignored = ignored;
    notes = new ArrayList<>();
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

  public ArrayList<Note> getNotes() {
    return notes;
  }

  public void setNotes(ArrayList<Note> notes) {
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
