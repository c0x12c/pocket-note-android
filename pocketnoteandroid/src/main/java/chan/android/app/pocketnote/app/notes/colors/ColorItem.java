package chan.android.app.pocketnote.app.notes.colors;

import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.AppResources;

public class ColorItem {

  public static final ColorItem[] DEFAULT_ITEMS = new ColorItem[]{
    new ColorItem("Red", AppResources.getColor(R.drawable.c00), R.drawable.c00),
    new ColorItem("Yellow", AppResources.getColor(R.drawable.c01), R.drawable.c01),
    new ColorItem("Orange", AppResources.getColor(R.drawable.c02), R.drawable.c02),

    new ColorItem("Grey", AppResources.getColor(R.drawable.c10), R.drawable.c10),
    new ColorItem("Green", AppResources.getColor(R.drawable.c11), R.drawable.c11),
    new ColorItem("Brown", AppResources.getColor(R.drawable.c12), R.drawable.c12),

    new ColorItem("Cyan", AppResources.getColor(R.drawable.c20), R.drawable.c20),
    new ColorItem("Purple", AppResources.getColor(R.drawable.c21), R.drawable.c21),
    new ColorItem("Blue", AppResources.getColor(R.drawable.c22), R.drawable.c22),
  };

  private final int drawableId;

  private final int color;

  private final String name;

  public ColorItem(String name, int color, int drawableId) {
    this.name = name;
    this.color = color;
    this.drawableId = drawableId;
  }

  public int getColor() {
    return color;
  }

  public String getName() {
    return name;
  }

  public int getDrawableId() {
    return drawableId;
  }
}


