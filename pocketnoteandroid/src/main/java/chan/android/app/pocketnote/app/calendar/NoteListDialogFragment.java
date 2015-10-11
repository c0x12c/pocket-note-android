package chan.android.app.pocketnote.app.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.BaseDialogFragment;
import chan.android.app.pocketnote.app.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteListDialogFragment extends BaseDialogFragment implements NoteAdapterNotifier {

  public static final String TAG = NoteListDialogFragment.class.getSimpleName();

  interface Args {

    String TITLE = TAG + ".title";

    String NOTES = TAG + ".notes";
  }

  public OnDialogClickListener listener;

  public NoteItemAdapter adapter;

  private ArrayList<Note> notes;

  private String title;

  public static NoteListDialogFragment fragment(String title, ArrayList<Note> notes) {
    Bundle args = new Bundle();
    args.putString(Args.TITLE, title);
    args.putParcelableArrayList(Args.NOTES, notes);
    NoteListDialogFragment d = new NoteListDialogFragment();
    d.setArguments(args);
    return d;
  }

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    final Bundle args = getArguments();
    title = args.getString(Args.TITLE);
    notes = args.getParcelableArrayList(Args.NOTES);
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
    // TODO:
    // listView.setOnItemLongClickListener(new OnLongClickCalendarNoteListener(this));
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

    void onCancel();

    void onAdd();

    void onEditNote(final Note note);

    void onNotesChanged();
  }
}
