package chan.android.app.pocketnote.util;

import android.util.Log;

public class Logger {

  private static final String TAG = "PocketNote";

  public static void e(String msg) {
    Log.e(TAG, msg);
  }

  public static void w(String msg) {
    Log.w(TAG, msg);
  }

  public static void i(String msg) {
    Log.i(TAG, msg);
  }

  public static void d(String msg) {
    Log.d(TAG, msg);
  }

  public static void v(String msg) {
    Log.v(TAG, msg);
  }
}
