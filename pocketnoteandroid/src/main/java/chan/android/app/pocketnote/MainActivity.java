package chan.android.app.pocketnote;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import chan.android.app.pocketnote.app.AppPreferences;
import chan.android.app.pocketnote.app.calendar.CalendarFragment;
import chan.android.app.pocketnote.app.notes.NotesFragment;
import chan.android.app.pocketnote.app.settings.SettingsFragment;
import chan.android.app.pocketnote.app.trash.TrashFragment;
import chan.android.app.pocketnote.util.Logger;
import chan.android.app.pocketnote.util.view.CircularImageView;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

  private static final int INTENT_TAKE_PHOTO = 0;

  private static final int INTENT_CHOOSE_PHOTO = 1;

  private CircularImageView photoImageView;

  private Uri imageUri;

  private NavigationView navigationView;

  private Toolbar toolbar;

  private DrawerLayout drawerLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(
      R.layout.main);
    drawerLayout = (DrawerLayout) findViewById(
      R.id.main___drawerlayout);
    navigationView = (NavigationView) findViewById(
      R.id.main___navigation_view);
    toolbar = (Toolbar) findViewById(
      R.id.main___toolbar);
    initialize();
  }

  private void replaceFragment(Fragment fragment, String tag) {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.main___framelayout_container, fragment, tag);
    transaction.commit();
  }

  private void initialize() {
    setSupportActionBar(toolbar);
    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
          case R.id.main___note:
            replaceFragment(NotesFragment.instance(), NotesFragment.TAG);
            return true;
          case R.id.main___calendar:
            replaceFragment(CalendarFragment.instance(), NotesFragment.TAG);
            return true;
          case R.id.main___trash:
            replaceFragment(TrashFragment.instance(), NotesFragment.TAG);
            return true;
          case R.id.main___settings:
            replaceFragment(SettingsFragment.instance(), NotesFragment.TAG);
            return true;
        }
        return false;
      }
    });


    final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
      this,
      drawerLayout,
      toolbar,
      R.string.drawer_open,
      R.string.drawer_close) {

      @Override
      public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
      }

      @Override
      public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
      }
    };
    drawerLayout.setDrawerListener(drawerToggle);
    drawerToggle.syncState();
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  private void displayPhoto(String path) {
    Picasso
      .with(this)
      .load(path)
      .into(photoImageView);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    final MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.search, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home: {
        break;
      }
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      switch (requestCode) {
        case INTENT_TAKE_PHOTO:
          AppPreferences.saveUserPhotoFilePath(AppPreferences.getUserTempPhotoFilePath());
          displayPhoto(AppPreferences.getUserPhotoFilePath());
          break;

        case INTENT_CHOOSE_PHOTO:
          Uri uri = data.getData();
          String imagePath = getRealPathFromUri(uri);
          Logger.e("Image from gallery path=" + imagePath);
          AppPreferences.saveUserPhotoFilePath(imagePath);
          displayPhoto(imagePath);
          break;

        default:
          break;
      }
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (imageUri != null) {
      outState.putString("camera_image_uri", imageUri.toString());
    }
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    if (savedInstanceState.containsKey("camera_image_uri")) {
      imageUri = Uri.parse(savedInstanceState.getString("camera_image_uri"));
    }
  }

  private String getRealPathFromUri(Uri uri) {
    String result;
    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
    if (cursor == null) { // Source is Dropbox or other similar local file path
      result = uri.getPath();
    } else {
      cursor.moveToFirst();
      int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
      result = cursor.getString(idx);
      cursor.close();
    }
    return result;
  }
}
