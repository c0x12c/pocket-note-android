package chan.android.app.pocketnote.app.calendar;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.Note;
import com.actionbarsherlock.app.SherlockFragment;

import java.util.List;

class NoteListDialogFragment extends DialogFragment implements NoteAdapterNotifier {

  public OnDialogClickListener listener;
  public NoteItemAdapter adapter;
  private List<Note> notes;
  private String title;
  private SherlockFragment fragment;

  public NoteListDialogFragment(SherlockFragment fragment, String title, List<Note> notes) {
    this.fragment = fragment;
    this.title = title;
    this.notes = notes;
  }

  @Override
  public void notifyAdapter() {
    adapter.notifyDataSetChanged();
  }

  @Override
  public List<Note> getNotes() {
    return adapter.getNotes();
  }

  @Override
  public void setNotes(List<Note> notes) {
    adapter.setNotes(notes);
    adapter.notifyDataSetChanged();
  }

  public void setOnDialogClickListener(OnDialogClickListener listener) {
    this.listener = listener;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Parse all views
    View root = inflater.inflate(R.layout.note_list_dialog, container, false);
    Button add = (Button) root.findViewById(R.id.note_list_$_button_add);
    Button cancel = (Button) root.findViewById(R.id.note_list_$_button_cancel);
    TextView textViewTitle = (TextView) root.findViewById(R.id.note_list_$_textview_title);
    final ListView listView = (ListView) root.findViewById(R.id.note_list_$_listview);

    // Do stuffs
    textViewTitle.setText(title);
    adapter = new NoteItemAdapter(getActivity(), notes);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
          listener.onEditNote(notes.get(position));
        }
      }
    });
    listView.setOnItemLongClickListener(new OnLongClickCalendarNoteListener(this));
    add.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (listener != null) {
          listener.onAdd();
        }
        dismiss();
      }
    });
    cancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (listener != null) {
          listener.onCancel();
        }
        dismiss();
      }
    });
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    return root;
  }

  public interface OnDialogClickListener {

    public void onCancel();

    public void onAdd();

    public void onEditNote(final Note note);

    public void onNotesChanged();
  }
}
