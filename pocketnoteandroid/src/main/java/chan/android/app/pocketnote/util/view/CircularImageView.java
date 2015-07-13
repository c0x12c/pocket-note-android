package chan.android.app.pocketnote.util.view;

import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircularImageView extends ImageView {

  public CircularImageView(Context context) {
    this(context, null, 0);
  }

  public CircularImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CircularImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
    Bitmap src;
    if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
      float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
      float factor = smallest / radius;
      src = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() / factor), (int) (bmp.getHeight() / factor), false);
    } else {
      src = bmp;
    }

    final Paint paint = new Paint();
    paint.setAntiAlias(true);
    paint.setFilterBitmap(true);
    paint.setDither(true);
    paint.setColor(Color.WHITE);
    Bitmap output = Bitmap.createBitmap(radius, radius, Config.ARGB_8888);
    Canvas canvas = new Canvas(output);
    final Rect rect = new Rect(0, 0, radius, radius);
    canvas.drawARGB(0, 0, 0, 0);
    canvas.drawCircle(radius / 2 + 0.7f, radius / 2 + 0.7f, radius / 2 + 0.1f, paint);
    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    canvas.drawBitmap(src, rect, rect, paint);
    return output;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    Drawable drawable = getDrawable();
    if (drawable == null) {
      return;
    }
    if (getWidth() == 0 || getHeight() == 0) {
      return;
    }
    Bitmap b = ((BitmapDrawable) drawable).getBitmap();
    Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
    final int w = getWidth();
    final int h = getHeight();
    Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
    canvas.drawBitmap(roundBitmap, 0, 0, null);
  }
}
