package chan.android.app.pocketnote.util.rx;

import android.util.Log;
import rx.Subscriber;

public abstract class SimpleSubscriber<T> extends Subscriber<T> {

  private static final String TAG = SimpleSubscriber.class.getSimpleName();

  @Override
  public void onCompleted() {

  }

  @Override
  public void onError(Throwable e) {
    Log.e(TAG, e.getMessage(), e);
  }

  @Override
  public abstract void onNext(T t);
}
