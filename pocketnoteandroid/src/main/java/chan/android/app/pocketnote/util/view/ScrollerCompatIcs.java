package chan.android.app.pocketnote.util.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.widget.Scroller;

/**
 * ICS API access for Scroller
 */
class ScrollerCompatIcs {
  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  public static float getCurrVelocity(Scroller scroller) {
    return scroller.getCurrVelocity();
  }
}