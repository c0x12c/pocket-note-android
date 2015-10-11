package chan.android.app.pocketnote.util;

public interface TimeProvider {

  long provideCurrentMilliseconds();

  long provideCurrentNanoseconds();

  TimeProvider SYSTEM = new TimeProvider() {
    @Override
    public long provideCurrentMilliseconds() {
      return System.currentTimeMillis();
    }

    @Override
    public long provideCurrentNanoseconds() {
      return System.nanoTime();
    }
  };
}
