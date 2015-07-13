package chan.android.app.pocketnote.app.calendar;

import chan.android.app.pocketnote.app.Note;

import java.util.List;

interface NoteAdapterNotifier {

  public void notifyAdapter();

  public List<Note> getNotes();

  public void setNotes(List<Note> notes);
}
