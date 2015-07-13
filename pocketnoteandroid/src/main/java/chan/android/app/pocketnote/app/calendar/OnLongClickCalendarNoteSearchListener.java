package chan.android.app.pocketnote.app.calendar;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import chan.android.app.pocketnote.app.Note;
import chan.android.app.pocketnote.app.notes.ActionListDialogFragment;

import java.util.List;

class OnLongClickCalendarNoteSearchListener extends OnLongClickCalendarNoteListener {

  public OnLongClickCalendarNoteSearchListener(Fragment fragment) {
    super(fragment);
  }

  @Override
  public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
    final CalendarFragment f = (CalendarFragment) fragment;
    final Note note = (Note) f.adapterSearch.getItem(position);
    final List<Option> options = getAvailableOptions(note);
    ActionListDialogFragment d = ActionListDialogFragment.newInstance(note.getTitle(), getOptionItems(options));
    d.setPickItemListener(new ActionListDialogFragment.OnPickItemListener() {
      @Override
      public void onPick(int index) {
        Option opt = options.get(index);
        opt.choose(f, note);
        f.adapterSearch.notifyDataSetChanged();
      }
    });
    d.show(fragment.getFragmentManager(), "dialog");
    return true;
  }
}
