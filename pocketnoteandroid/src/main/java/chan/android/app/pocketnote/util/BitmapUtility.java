package chan.android.app.pocketnote.util;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BitmapUtility {

  private BitmapUtility() {
    throw new AssertionError(BitmapUtility.class.getSimpleName());
  }

  public static String toBase64(Bitmap image) {
    Bitmap bitmap = image;
    ByteArrayOutputStream outstream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 25, outstream);
    byte[] b = outstream.toByteArray();
    return Base64.encodeToString(b, Base64.DEFAULT);
  }

  public static Bitmap fromBase64(String input) {
    byte[] decodedByte = Base64.decode(input, 0);
    return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
  }

  public static int calculateInSampleSize(BitmapFactory.Options options, int requiredWidth, int requiredHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;
    if (height > requiredHeight || width > requiredWidth) {
      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while ((halfHeight / inSampleSize) > requiredHeight && (halfWidth / inSampleSize) > requiredWidth) {
        inSampleSize *= 2;
      }
    }
    return inSampleSize;
  }

  public static Bitmap decodeBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

    // First decode with inJustDecodeBounds=true to check dimensions
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(res, resId, options);

    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeResource(res, resId, options);
  }

  public static Bitmap decodeBitmapFromFile(File imageFile, int reqWidth, int reqHeight) throws IOException {
    // First decode with inJustDecodeBounds=true to check dimensions
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    FileInputStream instream = new FileInputStream(imageFile);
    BitmapFactory.decodeStream(instream, null, options);
    instream.close();

    // Calculate best in sample size
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    options.inJustDecodeBounds = false;

    // Recreate from stream
    instream = new FileInputStream(imageFile);
    Bitmap out = BitmapFactory.decodeStream(instream, null, options);
    instream.close();

    // To get correct orientation (face up - 90 deg) we need to fix the angle
    ExifInterface exif = new ExifInterface(imageFile.getPath());
    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
    int angle = 0;
    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
      angle = 90;
    } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
      angle = 180;
    } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
      angle = 270;
    }
    Matrix matrix = new Matrix();
    matrix.postRotate(angle);
    return Bitmap.createBitmap(out, 0, 0, out.getWidth(), out.getHeight(), matrix, true);
  }
}
