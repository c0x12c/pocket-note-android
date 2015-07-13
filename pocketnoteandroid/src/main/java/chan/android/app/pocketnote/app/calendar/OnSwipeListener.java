package chan.android.app.pocketnote.app.calendar;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

class OnSwipeListener implements View.OnTouchListener {

  private static final int SWIPE_THRESHOLD = 100;

  private static final int SWIPE_VELOCITY_THRESHOLD = 100;

  private final GestureDetector gestureDetector;

  private final Swiper swiper;

  public OnSwipeListener(Context context, Swiper swiper) {
    gestureDetector = new GestureDetector(context, new GestureListener());
    this.swiper = swiper;
  }

  public GestureDetector getGestureDetector() {
    return gestureDetector;
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    return true;
  }

  private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;

    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    @Override
    public boolean onDown(MotionEvent e) {
      return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      boolean result = false;
      try {
        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();
        if (Math.abs(diffX) > Math.abs(diffY)) {
          if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffX > 0) {
              swiper.onSwipeRight();
            } else {
              swiper.onSwipeLeft();
            }
          }
        } else {
          if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffY > 0) {
              swiper.onSwipeDown();
            } else {
              swiper.onSwipeUp();
            }
          }
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      return result;
    }
  }
}

