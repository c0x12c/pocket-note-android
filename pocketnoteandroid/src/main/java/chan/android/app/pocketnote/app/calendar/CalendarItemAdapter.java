package chan.android.app.pocketnote.app.calendar;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import chan.android.app.pocketnote.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class CalendarItemAdapter extends BaseAdapter {

  private static final int COLOR_BACKGROUND_AVAILABLE = Color.parseColor("#FFFFFF");
  private static final int COLOR_TEXT_AVAILABLE = Color.parseColor("#000000");
  private static final int COLOR_BACKGROUND_UNAVAILABLE = Color.parseColor("#d3d3d3");
  private static final int COLOR_TEXT_UNAVAILABLE = Color.parseColor("#a8a8a8");
  private static final int COLOR_TEXT_SATURDAY = Color.parseColor("#006887");
  private static final int COLOR_TEXT_SUNDAY = Color.parseColor("#ad2e11");
  private static final Calendar TODAY = Calendar.getInstance();
  private static Calendar calendar;
  private Context context;
  private List<CalendarItem> items;

  public CalendarItemAdapter(Context context, List<CalendarItem> items) {
    this.context = context;
    this.items = items;
    this.calendar = Calendar.getInstance();
  }

  public CalendarItemAdapter(Context context) {
    this.context = context;
    this.items = new ArrayList<CalendarItem>();
  }

  public void addItem(CalendarItem item) {
    items.add(item);
  }

  @Override
  public int getCount() {
    return items.size();
  }

  @Override
  public CalendarItem getItem(int position) {
    return items.get(position);
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder vh;
    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.calendar_grid_item, parent, false);
      vh = new ViewHolder(convertView);
      convertView.setTag(vh);
    } else {
      vh = (ViewHolder) convertView.getTag();
    }

    final CalendarItem item = items.get(position);
    calendar.set(Calendar.DAY_OF_MONTH, item.getDay());
    calendar.set(Calendar.MONTH, item.getMonth());
    calendar.set(Calendar.YEAR, item.getYear());

    // Mark Sunday as red
    int weekday = calendar.get(Calendar.DAY_OF_WEEK);

    if (item.isIgnored()) {
      vh.day.setTextColor(COLOR_TEXT_UNAVAILABLE);
      setViewBackground(context, vh.parent, R.drawable.calendar_item_greyout);
    } else {
      if (isSunday(weekday)) {
        vh.day.setTextColor(COLOR_TEXT_SUNDAY);
      } else if (isSaturday(weekday)) {
        vh.day.setTextColor(COLOR_TEXT_SATURDAY);
      } else {
        vh.day.setTextColor(COLOR_TEXT_AVAILABLE);
      }
      setViewBackground(context, vh.parent, R.drawable.calendar_item);
    }

    if (isToday(item.getDay(), item.getMonth(), item.getYear())) {
      setViewBackground(context, vh.parent, R.drawable.calendar_item_today);
    }

    vh.day.setText(Integer.toString(item.getDay()));
    vh.container.setNoteColors(item.getNotes());
    return convertView;
  }

  private boolean isSunday(int weekDay) {
    return weekDay == 1;
  }

  private boolean isSaturday(int weekDay) {
    return weekDay == 7;
  }

  private boolean isToday(int day, int month, int year) {
    int d = TODAY.get(Calendar.DAY_OF_MONTH);
    int m = TODAY.get(Calendar.MONTH);
    int y = TODAY.get(Calendar.YEAR);
    return day == d && month == m && year == y;
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  private void setViewBackground(Context context, View parent, int drawableId) {
    int sdk = Build.VERSION.SDK_INT;
    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
      parent.setBackgroundDrawable(context.getResources().getDrawable(drawableId));
    } else {
      parent.setBackground(context.getResources().getDrawable(drawableId));
    }
  }

  static class ViewHolder {
    BucketNoteView container;
    TextView day;
    LinearLayout parent;

    public ViewHolder(View v) {
      parent = (LinearLayout) v.findViewById(R.id.calendar_grid_item_$_linearlayout_parent);
      container = (BucketNoteView) v.findViewById(R.id.calendar_grid_item_$_bucket_note_view);
      day = (TextView) v.findViewById(R.id.calendar_grid_item_$_textview_day);
    }
  }
}
