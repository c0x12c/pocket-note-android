package chan.android.app.pocketnote.app.notes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

class NoteEditor extends EditText {

  private Rect rect;
  private Paint paint;

  // This constructor is used by LayoutInflater
  public NoteEditor(Context context, AttributeSet attrs) {
    super(context, attrs);

    rect = new Rect();
    paint = new Paint();
    paint.setStyle(Paint.Style.STROKE);
    paint.setColor(Color.parseColor("#e5e500"));
  }

  public void setLineColor(int color) {
    paint.setColor(color);
  }

  /**
   * This is called to draw the LinedEditText object
   *
   * @param canvas The canvas on which the background is drawn.
   */
  @Override
  protected void onDraw(Canvas canvas) {
    int height = getHeight();
    int lineHeight = getLineHeight();
    int count = height / lineHeight;

    // For long text with scrolling
    if (getLineCount() > count) {
      count = getLineCount();
    }

    // Draw first line
    int baseline = getLineBounds(0, rect);
    for (int i = 0; i < count; i++) {
      canvas.drawLine(rect.left, baseline + 1, rect.right, baseline + 1, paint);
      // Draw next line
      baseline += getLineHeight();
    }
    super.onDraw(canvas);
  }
}
