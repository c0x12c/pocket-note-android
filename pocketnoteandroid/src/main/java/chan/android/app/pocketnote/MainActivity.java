package chan.android.app.pocketnote;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import chan.android.app.pocketnote.app.AppPreferences;
import chan.android.app.pocketnote.app.calendar.CalendarFragment;
import chan.android.app.pocketnote.app.notes.ActionListDialogFragment;
import chan.android.app.pocketnote.app.notes.NotesFragment;
import chan.android.app.pocketnote.app.settings.SettingsFragment;
import chan.android.app.pocketnote.app.trash.TrashFragment;
import chan.android.app.pocketnote.util.BitmapUtility;
import chan.android.app.pocketnote.util.DeviceUtility;
import chan.android.app.pocketnote.util.Logger;
import chan.android.app.pocketnote.util.view.CircularImageView;
import chan.android.app.pocketnote.util.view.NavigationDrawerAdapter;
import chan.android.app.pocketnote.util.view.NavigationDrawerItem;
import chan.android.app.pocketnote.util.view.NavigationMenuItem;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SherlockFragmentActivity {

  private static final int INTENT_TAKE_PHOTO = 0;
  private static final int INTENT_CHOOSE_PHOTO = 1;

  private static final List<ActionListDialogFragment.Item> PHOTO_ACTIONS = new ArrayList<>();

  static {
    PHOTO_ACTIONS.add(new ActionListDialogFragment.Item(R.drawable.ic_action_device_access_camera, "Take photo"));
    PHOTO_ACTIONS.add(new ActionListDialogFragment.Item(R.drawable.ic_action_content_picture, "Choose photo"));
  }

  private NavigationDrawerItem[] DRAWER_ITEMS;
  private DrawerLayout drawerLayout;
  private LinearLayout drawerContainer;
  private ListView drawerList;
  private ActionBarDrawerToggle drawerToggle;
  private CircularImageView photoImageView;
  private TextView username;
  private CharSequence drawerTitle;
  private CharSequence title;
  private Fragment[] fragments;
  private Uri imageUri;
  private Fragment fragmentNote;
  private Fragment fragmentCalendar;
  private Fragment fragmentTrash;
  private Fragment fragmentSettings;
  private int fragIndex;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    setupActionBar();
    setupNavigationDrawer();

    fragmentNote = NotesFragment.newInstance();
    fragmentTrash = TrashFragment.newInstance();
    fragmentCalendar = CalendarFragment.newInstance();
    fragmentSettings = SettingsFragment.newInstance();

    fragments = new Fragment[4];
    fragments[0] = fragmentNote;
    fragments[1] = fragmentCalendar;
    fragments[2] = fragmentTrash;
    fragments[3] = fragmentSettings;

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.add(R.id.content, fragmentNote, NotesFragment.TAG);
    transaction.add(R.id.content, fragmentTrash, TrashFragment.TAG);
    transaction.add(R.id.content, fragmentCalendar, CalendarFragment.TAG);
    transaction.add(R.id.content, fragmentSettings, SettingsFragment.TAG);

    if (savedInstanceState == null) {
      transaction.hide(fragmentCalendar);
      transaction.hide(fragmentTrash);
      transaction.hide(fragmentSettings);
      transaction.show(fragmentNote);
      fragIndex = 0;
    }
    transaction.commit();
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  private void setupNavigationDrawer() {
    DRAWER_ITEMS = new NavigationDrawerItem[]{
      NavigationMenuItem.create(NavigationDrawerItemConstants.NOTES_ID, NavigationDrawerItemConstants.NOTES_NAME, "ic_drawer_note", true, true, this),
      NavigationMenuItem.create(NavigationDrawerItemConstants.CALENDAR_ID, NavigationDrawerItemConstants.CALENDAR_NAME, "ic_drawer_calendar", true, true, this),
      NavigationMenuItem.create(NavigationDrawerItemConstants.TRASH_ID, NavigationDrawerItemConstants.TRASH_NAME, "ic_drawer_trash", true, true, this),
      NavigationMenuItem.create(NavigationDrawerItemConstants.SETTINGS_ID, NavigationDrawerItemConstants.SETTINGS_NAME, "ic_drawer_settings", true, true, this),
    };

    title = drawerTitle = getTitle();

    drawerLayout = (DrawerLayout) findViewById(R.id.main_$_drawer_layout);
    drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    drawerLayout.setFocusableInTouchMode(false);

    drawerContainer = (LinearLayout) findViewById(R.id.main_$_linearlayout_container);

    drawerList = (ListView) findViewById(R.id.main_$_listview_items);
    drawerList.setAdapter(new NavigationDrawerAdapter(this, R.layout.navdrawer_item, DRAWER_ITEMS));
    drawerList.setOnItemClickListener(new DrawerItemClickListener());
    drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
      public void onDrawerClosed(View view) {
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu();
      }

      public void onDrawerOpened(View drawerView) {
        getSupportActionBar().setTitle(drawerTitle);
        invalidateOptionsMenu();
      }
    };
    drawerLayout.setDrawerListener(drawerToggle);

    photoImageView = (CircularImageView) findViewById(R.id.main_$_imageview_user);
    if (AppPreferences.getUserPhotoFilePath() != null) {
      displayPhoto(AppPreferences.getUserPhotoFilePath());
    }

    username = (TextView) findViewById(R.id.main_$_textview_username);
    username.setText(AppPreferences.getUserName());
  }

  private void displayPhoto(String path) {
    try {
      if (path.startsWith("https")) {
        ImageLoader.getInstance().displayImage(path, photoImageView);
      } else {
        File file = new File(path);
        int size = DeviceUtility.dpToPx(this, 48);
        photoImageView.setImageBitmap(BitmapUtility.decodeBitmapFromFile(file, size, size));
      }
    } catch (Exception e) {
      Logger.e("displayPhoto raise exception: " + e.getMessage());
    }
  }

  private void animateBackground() {
    LinearLayout main = (LinearLayout) findViewById(R.id.main_$_linearlayout_animated_background);
    Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
    main.startAnimation(anim);
  }

  private void setupActionBar() {
    // Enable ActionBar app icon to behave as action to toggle nav drawer
    getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setHomeButtonEnabled(true);
    getActionBar().setTitle("Pocket Note");
  }

  @Override
  public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
    com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.search, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home: {
        if (drawerLayout.isDrawerOpen(drawerContainer)) {
          closeDrawer();
        } else {
          openDrawer();
        }
        break;
      }
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    drawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    // Pass any configuration change to the drawer toggles
    drawerToggle.onConfigurationChanged(newConfig);
  }

  private void openDrawer() {
    username.setText(AppPreferences.getUserName());
    drawerLayout.openDrawer(drawerContainer);
  }

  private void closeDrawer() {
    username.setText(AppPreferences.getUserName());
    drawerLayout.closeDrawer(drawerContainer);
  }

  @Override
  public void onBackPressed() {
    if (!drawerLayout.isDrawerOpen(drawerContainer)) {
      openDrawer();
    } else {
      closeDrawer();
      super.onBackPressed();
    }
  }

  private void swapFragment(int i, int j) {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
    transaction.hide(fragments[i]);
    transaction.show(fragments[j]);
    fragments[j].onResume();
    transaction.commit();
  }

  private void selectItem(int position) {
    if (position != fragIndex) {
      swapFragment(fragIndex, position);
    }
    fragIndex = position;
    closeDrawer();
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
    fragments[fragIndex].onActivityResult(requestCode, resultCode, data);
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

  private class NavigationDrawerItemConstants {

    public static final int NOTES_ID = 99;
    public static final String NOTES_NAME = "Notes";
    public static final int CALENDAR_ID = 100;
    public static final String CALENDAR_NAME = "Calendar";
    public static final int TRASH_ID = 101;
    public static final String TRASH_NAME = "Trash";
    public static final int SETTINGS_ID = 102;
    public static final String SETTINGS_NAME = "Settings";
    private NavigationDrawerItemConstants() {
      throw new AssertionError("Constructor is private!");
    }
  }

  /**
   * The click listener for ListView in the navigation drawer
   */
  private class DrawerItemClickListener implements ListView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      supportInvalidateOptionsMenu();
      selectItem(position);
    }
  }
}
