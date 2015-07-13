package chan.android.app.pocketnote.util.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;


public class SquareGridView extends GridView {

  public SquareGridView(Context context) {
    super(context);
  }

  public SquareGridView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SquareGridView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
  }
}

