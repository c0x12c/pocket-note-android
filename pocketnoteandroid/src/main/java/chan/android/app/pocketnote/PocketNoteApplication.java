package chan.android.app.pocketnote;


import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import chan.android.app.pocketnote.app.AppPreferences;
import chan.android.app.pocketnote.app.AppResources;
import chan.android.app.pocketnote.util.Logger;

public class PocketNoteApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Context context = getApplicationContext();
    AppPreferences.initialize(context);
    AppResources.initialize(context);

    if (BuildConfig.DEBUG) {
      StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build());
      StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build());

      // LeakCanary.install(this);
    }
  }

  @Override
  public void onLowMemory() {
    Logger.e("onLowMemory() called");
    super.onLowMemory();
  }

  @Override
  public void onTrimMemory(int level) {
    Logger.e("onTrimMemory() called");
    super.onTrimMemory(level);
  }
}
