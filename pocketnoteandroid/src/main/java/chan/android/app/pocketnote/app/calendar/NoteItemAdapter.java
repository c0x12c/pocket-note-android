package chan.android.app.pocketnote.app.calendar;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.Note;
import chan.android.app.pocketnote.util.DateTimeUtility;
import chan.android.app.pocketnote.util.TextUtility;
import org.joda.time.DateTime;

import java.util.List;

class NoteItemAdapter extends BaseAdapter {

  private static final int COLOR_GREY_OUT = Color.parseColor("#777777");

  private Context context;

  private List<Note> notes;

  public NoteItemAdapter(Context context, List<Note> notes) {
    this.context = context;
    this.notes = notes;
  }

  @Override
  public int getCount() {
    return notes.size();
  }

  @Override
  public Note getItem(int position) {
    return notes.get(position);
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  public List<Note> getNotes() {
    return notes;
  }

  public void setNotes(List<Note> notes) {
    this.notes = notes;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder vh;
    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.note_list_item, null);
      vh = new ViewHolder(convertView);
      convertView.setTag(vh);
    } else {
      vh = (ViewHolder) convertView.getTag();
    }
    final Note note = notes.get(position);
    vh.title.setText(note.getTitle());
    vh.content.setText(note.getContent());
    // Make date time more readable
    DateTime dt = new DateTime(note.getModifiedTime());
    vh.date.setText(DateTimeUtility.getReminderReadableDate(dt) + " @ " + DateTimeUtility.getReminderReadableTime(dt.getHourOfDay(), dt.getMinuteOfHour()));
    vh.color.setBackgroundColor(note.getColor());
    vh.lock.setVisibility(note.isLocked() ? View.VISIBLE : View.INVISIBLE);
    vh.reminder.setVisibility(TextUtility.isNullOrEmpty(note.getReminder()) ? View.INVISIBLE : View.VISIBLE);
    if (note.isChecked()) {
      setParentBackground(context, vh.parent, R.drawable.shadow_note_row_greyout_select);
    } else {
      setParentBackground(context, vh.parent, R.drawable.shadow_note_row_select);
    }
    return convertView;
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  private void setParentBackground(Context context, View parent, int drawableId) {
    int sdk = Build.VERSION.SDK_INT;
    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
      parent.setBackgroundDrawable(context.getResources().getDrawable(drawableId));
    } else {
      parent.setBackground(context.getResources().getDrawable(drawableId));
    }
  }

  private static class ViewHolder {
    View parent;
    View color;
    TextView title;
    TextView content;
    TextView date;
    ImageView lock;
    ImageView reminder;

    public ViewHolder(View v) {
      parent = (View) v.findViewById(R.id.note_item_$_parent);
      color = (View) v.findViewById(R.id.note_item_$_color_view);
      title = (TextView) v.findViewById(R.id.note_item_$_textview_title);
      content = (TextView) v.findViewById(R.id.note_item_$_textview_content);
      date = (TextView) v.findViewById(R.id.note_item_$_textview_date);
      lock = (ImageView) v.findViewById(R.id.note_item_$_imageview_lock);
      reminder = (ImageView) v.findViewById(R.id.note_item_$_imageview_reminder);
    }
  }
}
