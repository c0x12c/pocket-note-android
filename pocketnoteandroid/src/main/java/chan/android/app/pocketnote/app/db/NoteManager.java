package chan.android.app.pocketnote.app.db;


import chan.android.app.pocketnote.app.Note;

import java.util.List;

 public interface NoteManager {

   void add(Note note);

   void remove(Note note);

   void trash(Note note);

   void restore(Note note);

   void edit(Note note);

   void lock(Note note);

   void unlock(Note note);

   void check(Note note);

   void uncheck(Note note);

   void removeAll();

   int getId(Note note);

   void addReminder(Note note, String reminder);

   void removeReminder(Note note);

   List<Note> getNotes(int month, int year);

   void changeColor(Note note, int color);

   List<Note> searchInCalendar(String query);
}
