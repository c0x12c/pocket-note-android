package chan.android.app.pocketnote.util.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ListView;

public class RoundedRectListView extends ListView {

  private static final float RADIUS = 7;
  private Path clip;

  public RoundedRectListView(Context context) {
    super(context);
    init();
  }

  public RoundedRectListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {
    GradientDrawable gd = new GradientDrawable();
    gd.setCornerRadius(RADIUS);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    clip = new Path();
    RectF rect = new RectF(0, 0, w, h);
    clip.addRoundRect(rect, RADIUS, RADIUS, Path.Direction.CW);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    canvas.save();
    canvas.clipPath(clip);
    super.dispatchDraw(canvas);
    canvas.restore();
  }
}
