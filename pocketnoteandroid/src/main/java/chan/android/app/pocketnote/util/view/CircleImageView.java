package chan.android.app.pocketnote.util.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;
import chan.android.app.pocketnote.R;

/**
 * {@code CircleImageView } is a custom view to display image as a circle
 * instead of traditional rectangle view
 */
public class CircleImageView extends ImageView {

  private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

  private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

  private static final int COLOR_DRAWABLE_DIMENSION = 2;

  private static final int DEFAULT_BORDER_WIDTH = 0;

  private static final int DEFAULT_BORDER_COLOR = Color.BLACK;

  private final RectF drawableRect = new RectF();
  private final RectF borderRect = new RectF();

  private final Matrix shaderMatrix = new Matrix();
  private final Paint bitmapPaint = new Paint();
  private final Paint borderPaint = new Paint();

  private int borderColor = DEFAULT_BORDER_COLOR;
  private int borderWidth = DEFAULT_BORDER_WIDTH;

  private Bitmap bitmap;

  private BitmapShader bitmapShader;

  private int bitmapWidth;

  private int bitmapHeight;

  private float drawableRadius;

  private float borderRadius;

  private ColorFilter colorFilter;

  private boolean ready;

  private boolean setupPending;

  public CircleImageView(Context context) {
    super(context);
    init();
  }

  public CircleImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    TypedArray a = context.obtainStyledAttributes(attrs,
      R.styleable.CircleImageView, defStyle, 0);
    borderWidth = a.getDimensionPixelSize(
      R.styleable.CircleImageView_border_width, DEFAULT_BORDER_WIDTH);
    borderColor = a.getColor(
      R.styleable.CircleImageView_border_color, DEFAULT_BORDER_COLOR);
    a.recycle();
    init();
  }

  private void init() {
    super.setScaleType(SCALE_TYPE);
    ready = true;

    if (setupPending) {
      setup();
      setupPending = false;
    }
  }

  @Override
  public ScaleType getScaleType() {
    return SCALE_TYPE;
  }

  @Override
  public void setScaleType(ScaleType scaleType) {
    if (scaleType != SCALE_TYPE) {
      throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
    }
  }

  @Override
  public void setAdjustViewBounds(boolean adjustViewBounds) {
    if (adjustViewBounds) {
      throw new IllegalArgumentException("adjustViewBounds not supported.");
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (getDrawable() == null) {
      return;
    }
    canvas.drawCircle(getWidth() / 2, getHeight() / 2, drawableRadius, bitmapPaint);
    if (borderWidth != 0) {
      canvas.drawCircle(getWidth() / 2, getHeight() / 2, borderRadius, borderPaint);
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    setup();
  }

  public int getBorderColor() {
    return borderColor;
  }

  public void setBorderColor(int borderColor) {
    if (borderColor == this.borderColor) {
      return;
    }

    this.borderColor = borderColor;
    borderPaint.setColor(this.borderColor);
    invalidate();
  }

  public int getBorderWidth() {
    return borderWidth;
  }

  public void setBorderWidth(int borderWidth) {
    if (borderWidth == this.borderWidth) {
      return;
    }

    this.borderWidth = borderWidth;
    setup();
  }

  @Override
  public void setImageBitmap(Bitmap bm) {
    super.setImageBitmap(bm);
    bitmap = bm;
    setup();
  }

  @Override
  public void setImageDrawable(Drawable drawable) {
    super.setImageDrawable(drawable);
    bitmap = getBitmapFromDrawable(drawable);
    setup();
  }

  @Override
  public void setImageResource(int resId) {
    super.setImageResource(resId);
    bitmap = getBitmapFromDrawable(getDrawable());
    setup();
  }

  @Override
  public void setImageURI(Uri uri) {
    super.setImageURI(uri);
    bitmap = getBitmapFromDrawable(getDrawable());
    setup();
  }

  @Override
  public void setColorFilter(ColorFilter cf) {
    if (cf == colorFilter) {
      return;
    }
    colorFilter = cf;
    bitmapPaint.setColorFilter(colorFilter);
    invalidate();
  }

  private Bitmap getBitmapFromDrawable(Drawable drawable) {
    if (drawable == null) {
      return null;
    }
    if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable) drawable).getBitmap();
    }
    try {
      Bitmap bitmap;
      if (drawable instanceof ColorDrawable) {
        bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION, COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG);
      } else {
        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
      }
      Canvas canvas = new Canvas(bitmap);
      drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
      drawable.draw(canvas);
      return bitmap;
    } catch (OutOfMemoryError e) {
      return null;
    }
  }

  private void setup() {
    if (!ready) {
      setupPending = true;
      return;
    }
    if (bitmap == null) {
      return;
    }
    bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    bitmapPaint.setAntiAlias(true);
    bitmapPaint.setShader(bitmapShader);

    borderPaint.setStyle(Paint.Style.STROKE);
    borderPaint.setAntiAlias(true);
    borderPaint.setColor(borderColor);
    borderPaint.setStrokeWidth(borderWidth);

    bitmapHeight = bitmap.getHeight();
    bitmapWidth = bitmap.getWidth();

    borderRect.set(0, 0, getWidth(), getHeight());
    borderRadius = Math.min((borderRect.height() - borderWidth) / 2, (borderRect.width() - borderWidth) / 2);

    drawableRect.set(borderWidth, borderWidth, borderRect.width() - borderWidth, borderRect.height() - borderWidth);
    drawableRadius = Math.min(drawableRect.height() / 2, drawableRect.width() / 2);

    updateShaderMatrix();
    invalidate();
  }

  private void updateShaderMatrix() {
    float scale;
    float dx = 0;
    float dy = 0;
    shaderMatrix.set(null);
    if (bitmapWidth * drawableRect.height() > drawableRect.width() * bitmapHeight) {
      scale = drawableRect.height() / (float) bitmapHeight;
      dx = (drawableRect.width() - bitmapWidth * scale) * 0.5f;
    } else {
      scale = drawableRect.width() / (float) bitmapWidth;
      dy = (drawableRect.height() - bitmapHeight * scale) * 0.5f;
    }
    shaderMatrix.setScale(scale, scale);
    shaderMatrix.postTranslate((int) (dx + 0.5f) + borderWidth, (int) (dy + 0.5f) + borderWidth);
    bitmapShader.setLocalMatrix(shaderMatrix);
  }
}

