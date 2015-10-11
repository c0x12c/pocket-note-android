package chan.android.app.pocketnote.app.common;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {

  final int iconId;

  final String name;

  public Item(int iconId, String name) {
    this.iconId = iconId;
    this.name = name;
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
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.iconId);
    dest.writeString(this.name);
  }

  protected Item(Parcel in) {
    this.iconId = in.readInt();
    this.name = in.readString();
  }

  public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
    public Item createFromParcel(Parcel source) {
      return new Item(source);
    }

    public Item[] newArray(int size) {
      return new Item[size];
    }
  };
}
