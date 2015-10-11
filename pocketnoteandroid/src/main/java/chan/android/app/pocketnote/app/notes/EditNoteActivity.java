package chan.android.app.pocketnote.app.notes;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.AppPreferences;
import chan.android.app.pocketnote.app.BaseActivity;
import chan.android.app.pocketnote.app.Note;
import chan.android.app.pocketnote.app.notes.colors.ColorDropdownDialogFragment;
import chan.android.app.pocketnote.app.notes.colors.ColorPickerDialogFragment;
import chan.android.app.pocketnote.util.Logger;
import chan.android.app.pocketnote.util.TextUtility;
import chan.android.app.pocketnote.util.rx.SimpleSubscriber;
import chan.android.app.pocketnote.util.view.SquareButton;

public class EditNoteActivity extends BaseActivity {

  private static final int INVALID = -1;

  private EditText editTextTitle;

  private SquareButton buttonPickColor;

  private LinearLayout layoutBorder;

  private NoteEditor noteEditor;

  private int chosenColor = 0;

  private boolean oldNote = false;

  private Note editingNote;

  private Note originalNote;

  private int month = INVALID;

  private int day = INVALID;

  private int year = INVALID;

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.editor);

    layoutBorder = (LinearLayout) findViewById(R.id.editor_$_linearlayout_border);

    editTextTitle = (EditText) findViewById(R.id.editor_$_edittext_title);
    noteEditor = (NoteEditor) findViewById(R.id.editor_$_note_editor);

    buttonPickColor = (SquareButton) findViewById(R.id.editor_$_button_pick_color);
    buttonPickColor.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        FragmentManager fm = getSupportFragmentManager();
        ColorDropdownDialogFragment colorPicker = new ColorDropdownDialogFragment();
        colorPicker.setOnPickColorListener(new ColorPickerDialogFragment.OnPickColorListener() {
          @Override
          public void onPick(int color) {
            chosenColor = color;
            layoutBorder.setBackgroundColor(color);
            noteEditor.setLineColor(Color.BLACK);
            noteEditor.setBackgroundColor(color);
          }
        });
        colorPicker.show(fm, ColorDropdownDialogFragment.TAG);
      }
    });

    // Check if activity is started from clicking on existing note
    Bundle extra = getIntent().getExtras();
    if (extra != null) {
      editingNote = extra.getParcelable(Note.BUNDLE_KEY);
      if (editingNote != null) {
        originalNote = new Note(editingNote.getId(), editingNote.getTitle(), editingNote.getContent(), editingNote.getModifiedTime(), editingNote.getColor());
        noteEditor.setText(editingNote.getContent());
        editTextTitle.setText(editingNote.getTitle());
        chosenColor = editingNote.getColor();
        setColor(editingNote.getColor());
        oldNote = true;
      }

      if (extra.containsKey("year") && extra.containsKey("month") && extra.containsKey("day")) {
        year = extra.getInt("year");
        month = extra.getInt("month");
        day = extra.getInt("day");
        setColor(AppPreferences.getDefaultColor());
      }
    } else {
      checkPreferences();
    }
  }

  private void setColor(int color) {
    noteEditor.setTextColor(Color.BLACK);
    noteEditor.setLineColor(Color.BLACK);
    noteEditor.setBackgroundColor(color);
    layoutBorder.setBackgroundColor(color);
    chosenColor = color;
  }

  private void checkPreferences() {
    setColor(AppPreferences.getDefaultColor());
  }

  public Note getNote() {
    String title = editTextTitle.getText().toString();
    String content = noteEditor.getText().toString();
    if (TextUtility.removeWhiteSpaces(title).isEmpty() && TextUtility.removeWhiteSpaces(content).isEmpty()) {
      return null;
    }

    if (title.isEmpty()) {
      title = content;
    }

    if (content.isEmpty()) {
      content = title;
    }

    Note note = new Note(title, content, System.currentTimeMillis(), chosenColor);
    if (year != INVALID && month != INVALID && day != INVALID) {
      note.setYear(year);
      note.setMonth(month);
      note.setDay(day);
    }
    return note;
  }

  @Override
  public void onBackPressed() {
    final Note note = getNote();
    if (note != null && !oldNote) {
      subscribe(noteResource.add(note).subscribe(new SimpleSubscriber<Note>() {
          @Override
          public void onNext(Note note) {
            note.setColor(chosenColor);
            Toast.makeText(EditNoteActivity.this, "Saved", Toast.LENGTH_LONG).show();
          }
        })
      );
    } else if (oldNote) {
      editingNote.setTitle(note.getTitle());
      editingNote.setContent(note.getContent());
      editingNote.setColor(note.getColor());
      if (different(originalNote, editingNote)) {
        subscribe(noteResource.edit(editingNote).subscribe(new SimpleSubscriber<Note>() {
            @Override
            public void onNext(Note note) {
              Toast.makeText(EditNoteActivity.this, "Updated", Toast.LENGTH_LONG).show();
              Toast.makeText(EditNoteActivity.this, "Saved", Toast.LENGTH_LONG).show();
            }
          })
        );
      }
    }

    if (note != null) {
      Logger.e("Note mm-dd-yyyy: " + note.getYear() + ", " + note.getMonth() + ", " + note.getDay());
    }

    // Just go back
    super.onBackPressed();
  }

  private boolean different(Note original, Note editing) {
    return !(original.getContent().equals(editing.getContent())) ||
      !(original.getTitle().equals(editing.getTitle())) ||
      (original.getColor() != editing.getColor());
  }
}
