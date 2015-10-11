package chan.android.app.pocketnote.app.notes.adapter;

import android.os.Parcel;
import android.os.Parcelable;
import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.Note;

import java.util.ArrayList;
import java.util.List;

public enum Action implements Parcelable {

  CHECK("Check",
    R.drawable.ic_action_check),

  UNCHECK("Uncheck",
    R.drawable.ic_action_uncheck),

  LOCK("Lock",
    R.drawable.ic_lock),

  UNLOCK("Unlock",
    R.drawable.ic_unlock),

  TRASH("Delete",
    R.drawable.ic_drawer_trash),

  REMINDER("Reminder",
    R.drawable.ic_action_clock),

  EMAIL("Email",
    R.drawable.ic_email),

  TAKE_PHOTO("Take photo",
    R.drawable.ic_action_device_access_camera),

  CHOOSE_PHOTO("Choose photo",
    R.drawable.ic_action_content_picture);


  final String name;
  final int iconId;

  Action(String name, int iconId) {
    this.name = name;
    this.iconId = iconId;
  }

  public int getIconId() {
    return iconId;
  }

  public String getName() {
    return name;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(final Parcel dest, final int flags) {
    dest.writeInt(ordinal());
  }

  public static final Creator<Action> CREATOR = new Creator<Action>() {
    @Override
    public Action createFromParcel(final Parcel source) {
      return Action.values()[source.readInt()];
    }

    @Override
    public Action[] newArray(final int size) {
      return new Action[size];
    }
  };

  public static List<Action> getAvailableActions(Note note) {
    List<Action> options = new ArrayList<>();
    options.add(note.isChecked() ? Action.UNCHECK : Action.CHECK);
    options.add(note.isLocked() ? Action.UNLOCK : Action.LOCK);
    options.add(Action.TRASH);
    options.add(Action.REMINDER);
    options.add(Action.EMAIL);
    return options;
  }
}
