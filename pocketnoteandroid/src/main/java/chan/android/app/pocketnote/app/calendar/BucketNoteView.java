package chan.android.app.pocketnote.app.calendar;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.Note;

import java.util.List;

/**
 * BucketNoteView is a child view of a calendar grid item.
 * It's used to display the number of notes within a day with
 * limited on fix width and height.
 * For example, if we only allow a total of 10 notes to be displayed,
 * the grid should look like this:
 * xxxxx
 * xxxxx
 * <p/>
 * If we have less than 10 notes, the rest will still occupy the space
 * but the background will be transparent
 * xxxoo
 * ooooo
 * <p/>
 * So that each day item will have the same width & height relatively
 * to its parent.
 */
class BucketNoteView extends View {

  private int[][] colorMatrix;
  private int row;
  private int column;
  private int size;
  private Paint paint;
  private Paint borderPaint;

  public BucketNoteView(Context context) {
    this(context, null, 0);
  }

  public BucketNoteView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BucketNoteView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initialize(context, attrs);
  }

  private void initialize(Context context, AttributeSet attrs) {
    TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BucketNoteView, 0, 0);
    try {
      row = a.getInt(R.styleable.BucketNoteView_row, 0);
      column = a.getInt(R.styleable.BucketNoteView_column, 0);
      size = a.getDimensionPixelSize(R.styleable.BucketNoteView_size, 20);

      colorMatrix = new int[row][column];

      // For drawing square
      paint = new Paint();
      paint.setStyle(Paint.Style.FILL);

      borderPaint = new Paint();
      borderPaint.setStyle(Paint.Style.STROKE);
      borderPaint.setStrokeWidth(1.0f);
      borderPaint.setColor(Color.BLACK);

    } finally {
      a.recycle();
    }
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
  }

  private int measureHeight(int measureSpec) {
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);
    int result = 0;
    if (specMode == MeasureSpec.EXACTLY) {
      result = specSize;
    } else {
      result = (row * size) + getPaddingBottom() + getPaddingTop();
      if (specMode == MeasureSpec.AT_MOST) {
        result = Math.min(result, specSize);
      }
    }
    return result;
  }

  private int measureWidth(int measureSpec) {
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);
    int result = 0;
    if (specMode == MeasureSpec.EXACTLY) {
      result = specSize;
    } else {
      result = (column * size) + getPaddingLeft() + getPaddingRight();
      if (specMode == MeasureSpec.AT_MOST) {
        result = Math.min(result, specSize);
      }
    }
    return result;
  }

  public void setNoteColors(List<Note> notes) {
    if (notes == null || notes.isEmpty()) {
      return;
    }

    final int max = row * column;
    final int n = notes.size();
    int i = 0;
    for (int r = 0; r < row; ++r) {
      for (int c = 0; c < column; ++c) {
        colorMatrix[r][c] = notes.get(i).getColor();
        i++;
        if (i == max || i == n) {
          return;
        }
      }
    }
    invalidate();
  }

  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    int left = 0;
    int top = 0;
    for (int r = 0; r < row; ++r) {
      left = 0;
      for (int c = 0; c < column; ++c) {
        if (colorMatrix[r][c] != 0) {
          paint.setColor(colorMatrix[r][c]);
          canvas.drawRect(left, top, left + size, top + size, paint);
          canvas.drawRect(left, top, left + size, top + size, borderPaint);
        }
        left += size;
      }
      top += size;
    }
  }

  private float dpFromPx(float px) {
    return px / getContext().getResources().getDisplayMetrics().density;
  }

  private float pxFromDp(float dp) {
    return dp * getContext().getResources().getDisplayMetrics().density;
  }
}
