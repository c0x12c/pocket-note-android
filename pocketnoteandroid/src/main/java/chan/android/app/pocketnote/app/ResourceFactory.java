package chan.android.app.pocketnote.app;

import android.content.Context;
import chan.android.app.pocketnote.app.db.NoteResource;
import chan.android.app.pocketnote.app.db.NoteResourceManager;
import chan.android.app.pocketnote.app.preferences.PreferenceResource;
import chan.android.app.pocketnote.app.preferences.PreferenceResourceManager;
import chan.android.app.pocketnote.util.TimeProvider;

public interface ResourceFactory {

  /**
   * Provide a contract to access shared preference
   * @see PreferenceResource
   * @return
   */
  PreferenceResource providePreferenceResource();

  /**
   * Provide a contract to access note data
   * @see NoteResource
   * @return
   */
  NoteResource provideNoteResource();

  class Main implements ResourceFactory {

    final Context context;

    final PreferenceResource preferenceResource;

    final NoteResource noteResource;

    private Main(Context context) {
      this.context = context.getApplicationContext();
      this.preferenceResource = new PreferenceResourceManager(context);
      this.noteResource = new NoteResourceManager(context, TimeProvider.SYSTEM);
    }

    @Override
    public PreferenceResource providePreferenceResource() {
      return preferenceResource;
    }

    @Override
    public NoteResource provideNoteResource() {
      return noteResource;
    }

    private static Main singleton;

    public static void initialize(Context context) {
      if (singleton == null) {
        singleton = new Main(context);
      }
    }

    public static Main getSingleton() {
      return singleton;
    }
  }
}
