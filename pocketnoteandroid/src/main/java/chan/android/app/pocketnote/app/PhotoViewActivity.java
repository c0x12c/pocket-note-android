package chan.android.app.pocketnote.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import chan.android.app.pocketnote.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PhotoViewActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.photo_view);
    ImageView img = (ImageView) findViewById(R.id.photo_view_$_imageview);
    ImageLoader.getInstance().displayImage(AppPreferences.getUserPhotoFilePath(), img);
  }
}
