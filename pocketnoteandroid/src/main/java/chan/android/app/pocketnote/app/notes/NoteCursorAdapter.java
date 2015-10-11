package chan.android.app.pocketnote.app.notes;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.Note;
import chan.android.app.pocketnote.app.db.NoteResourceManager;
import chan.android.app.pocketnote.util.DateTimeUtility;
import chan.android.app.pocketnote.util.TextUtility;
import org.joda.time.DateTime;

class NoteCursorAdapter extends CursorAdapter {

  private static final int COLOR_GREY_OUT = Color.parseColor("#777777");

  private LayoutInflater inflater;

  private int layoutId;

  public NoteCursorAdapter(Context context, int layoutId) {
    super(context, null, false);
    this.inflater = LayoutInflater.from(context);
    this.layoutId = layoutId;
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = inflater.inflate(layoutId, parent, false);
    ViewHolder vh = new ViewHolder(view);
    view.setTag(vh);
    return view;
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    final ViewHolder vh = (ViewHolder) view.getTag();
    final Note note = NoteResourceManager.fromCursor(cursor);
    vh.title.setText(note.getTitle());
    vh.content.setText(note.getContent());

    // Make date time more readable
    DateTime dt = new DateTime(note.getModifiedTime());
    vh.date.setText(
      DateTimeUtility.getReminderReadableDate(dt) + " @ " +
      DateTimeUtility.getReminderReadableTime(dt.getHourOfDay(), dt.getMinuteOfHour())
    );
    vh.color.setBackgroundColor(note.getColor());
    vh.lock.setVisibility(note.isLocked() ? View.VISIBLE : View.INVISIBLE);
    vh.reminder.setVisibility(TextUtility.isNullOrEmpty(note.getReminder()) ? View.INVISIBLE : View.VISIBLE);
    if (note.isChecked()) {
      setParentBackground(context, vh.parent, R.drawable.shadow_note_row_greyout_select);
    } else {
      setParentBackground(context, vh.parent, R.drawable.shadow_note_row_select);
    }
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
      parent = (View) v.findViewById(
        R.id.note_item_$_parent);
      color = (View) v.findViewById(
        R.id.note_item_$_color_view);
      title = (TextView) v.findViewById(
        R.id.note_item_$_textview_title);
      content = (TextView) v.findViewById(
        R.id.note_item_$_textview_content);
      date = (TextView) v.findViewById(
        R.id.note_item_$_textview_date);
      lock = (ImageView) v.findViewById(
        R.id.note_item_$_imageview_lock);
      reminder = (ImageView) v.findViewById(
        R.id.note_item_$_imageview_reminder);
    }
  }
}
