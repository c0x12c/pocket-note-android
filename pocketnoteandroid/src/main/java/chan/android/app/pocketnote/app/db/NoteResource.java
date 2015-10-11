package chan.android.app.pocketnote.app.db;


import chan.android.app.pocketnote.app.Note;
import rx.Observable;

import java.util.List;

 public interface NoteResource {

   Observable<Note> add(Note note);

   Observable<Boolean> remove(Note note);

   Observable<Note> trash(Note note);

   Observable<Note> restore(Note note);

   Observable<Note> edit(Note note);

   Observable<Note> lock(Note note);

   Observable<Note> unlock(Note note);

   Observable<Note> check(Note note);

   Observable<Note> uncheck(Note note);

   Observable<Integer> removeAll();

   Observable<Integer> getId(Note note);

   Observable<Note> addReminder(Note note, String reminder);

   Observable<Note> removeReminder(Note note);

   Observable<List<Note>> getNotes(int month, int year);

   Observable<Note> changeColor(Note note, int color);

   Observable<List<Note>> searchInCalendar(String query);
}
