package chan.android.app.pocketnote;


import android.app.Application;
import android.content.Context;
import chan.android.app.pocketnote.app.AppPreferences;
import chan.android.app.pocketnote.app.AppResources;
import chan.android.app.pocketnote.app.db.PocketNoteManager;
import chan.android.app.pocketnote.util.Logger;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class PocketNoteApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Context context = getApplicationContext();
    PocketNoteManager.initialize(context);
    AppPreferences.initialize(context);
    AppResources.initialize(context);
    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
    ImageLoader.getInstance().init(config);
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
