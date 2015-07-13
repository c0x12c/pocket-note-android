package chan.android.app.pocketnote.app;


import android.content.Context;
import chan.android.app.pocketnote.R;

import java.util.HashMap;

public class AppResources {

  private static final HashMap<Integer, Integer> COLOR_TO_DRAWABLE = new HashMap<Integer, Integer>();
  private static final HashMap<Integer, Integer> DRAWABLE_TO_COLOR = new HashMap<Integer, Integer>();

  private AppResources() {
    throw new AssertionError("Don't even dare");
  }

  public static void initialize(Context context) {
    COLOR_TO_DRAWABLE.put(context.getResources().getColor(R.color.c00), R.drawable.c00);
    COLOR_TO_DRAWABLE.put(context.getResources().getColor(R.color.c01), R.drawable.c01);
    COLOR_TO_DRAWABLE.put(context.getResources().getColor(R.color.c02), R.drawable.c02);

    COLOR_TO_DRAWABLE.put(context.getResources().getColor(R.color.c10), R.drawable.c10);
    COLOR_TO_DRAWABLE.put(context.getResources().getColor(R.color.c11), R.drawable.c11);
    COLOR_TO_DRAWABLE.put(context.getResources().getColor(R.color.c12), R.drawable.c12);

    COLOR_TO_DRAWABLE.put(context.getResources().getColor(R.color.c20), R.drawable.c20);
    COLOR_TO_DRAWABLE.put(context.getResources().getColor(R.color.c21), R.drawable.c21);
    COLOR_TO_DRAWABLE.put(context.getResources().getColor(R.color.c22), R.drawable.c22);


    DRAWABLE_TO_COLOR.put(R.drawable.c00, context.getResources().getColor(R.color.c00));
    DRAWABLE_TO_COLOR.put(R.drawable.c01, context.getResources().getColor(R.color.c01));
    DRAWABLE_TO_COLOR.put(R.drawable.c02, context.getResources().getColor(R.color.c02));

    DRAWABLE_TO_COLOR.put(R.drawable.c10, context.getResources().getColor(R.color.c10));
    DRAWABLE_TO_COLOR.put(R.drawable.c11, context.getResources().getColor(R.color.c11));
    DRAWABLE_TO_COLOR.put(R.drawable.c12, context.getResources().getColor(R.color.c12));

    DRAWABLE_TO_COLOR.put(R.drawable.c20, context.getResources().getColor(R.color.c20));
    DRAWABLE_TO_COLOR.put(R.drawable.c21, context.getResources().getColor(R.color.c21));
    DRAWABLE_TO_COLOR.put(R.drawable.c22, context.getResources().getColor(R.color.c22));
  }

  public static int getDrawable(int color) {
    return COLOR_TO_DRAWABLE.get(color);
  }

  public static int getColor(int drawableId) {
    return DRAWABLE_TO_COLOR.get(drawableId);
  }
}
