package chan.android.app.pocketnote.app.settings;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.*;
import android.widget.*;
import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.AppPreferences;
import chan.android.app.pocketnote.app.db.NoteDbTable;
import chan.android.app.pocketnote.app.notes.OptionsTabDialogFragment;
import chan.android.app.pocketnote.app.notes.colors.ColorPickerDialogFragment;
import chan.android.app.pocketnote.util.Logger;
import chan.android.app.pocketnote.util.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class SettingsFragment extends Fragment {

  public static final String TAG = "Settings";
  static final String[] SORTING_OPTIONS = new String[]{
    "By modified time",
    "By alphabet",
    "By color"
  };
  static final String[] VIEW_OPTIONS = new String[]{
    "As list view",
    "As grid view"
  };
  static final String[] ALPHABET_COLUMN_OPTIONS = new String[]{
    "By title",
    "By content"
  };

  private static final int INTENT_TAKE_PHOTO = 0;

  private static final int INTENT_CHOOSE_PHOTO = 1;

  private Button buttonColor;

  private Button buttonRate;

  private Spinner spinnerSorting;

  private Spinner spinnerCollectionView;

  private Spinner spinnerAlphabet;

  private RelativeLayout relativeLayoutPassword;

  private LinearLayout linearLayoutColor;

  private RelativeLayout relativeLayoutName;

  private RelativeLayout relativeLayoutPhoto;

  private CircularImageView photoImageView;

  private TextView textViewUserName;

  private String photoPath;

  public static SettingsFragment newInstance() {
    return new SettingsFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Logger.e("SettingsFragment.onCreateView()");
    View root = inflater.inflate(R.layout.settings, container, false);
    spinnerAlphabet = (Spinner) root.findViewById(R.id.settings_$_spinner_alphabet_sorting_column);
    spinnerAlphabet.setVisibility(View.GONE);
    spinnerAlphabet.setAdapter(new SettingItemAdapter(getActivity(), Arrays.asList(ALPHABET_COLUMN_OPTIONS)));
    spinnerAlphabet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
          AppPreferences.saveDefaultAlphabetSortColumn(NoteDbTable.COLUMN_TITLE);
        } else {
          AppPreferences.saveDefaultAlphabetSortColumn(NoteDbTable.COLUMN_CONTENT);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        // Ignore
      }
    });

    spinnerSorting = (Spinner) root.findViewById(R.id.settings_$_spinner_sorting);
    spinnerSorting.setAdapter(new SettingItemAdapter(getActivity(), Arrays.asList(SORTING_OPTIONS)));
    spinnerSorting.setSelection(AppPreferences.getDefaultSortBy());
    spinnerSorting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AppPreferences.saveDefaultSortBy(position);
        // TODO: Is there a better way to handle this magic index?
        if (position == 1) {
          spinnerAlphabet.setVisibility(View.VISIBLE);
        } else {
          spinnerAlphabet.setVisibility(View.GONE);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        // Ignore
      }
    });


    spinnerCollectionView = (Spinner) root.findViewById(R.id.settings_$_spinner_collection_view);
    spinnerCollectionView.setAdapter(new SettingItemAdapter(getActivity(), Arrays.asList(VIEW_OPTIONS)));
    spinnerCollectionView.setSelection(AppPreferences.getDefaultCollectionView());
    spinnerCollectionView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AppPreferences.saveDefaultCollectionView(position);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        // Ignore
      }
    });

    View.OnClickListener listener = new OnColorClickListener();
    linearLayoutColor = (LinearLayout) root.findViewById(R.id.settings_$_linearlayout_color);
    linearLayoutColor.setOnClickListener(listener);
    buttonColor = (Button) root.findViewById(R.id.settings_$_button_pick_color);
    buttonColor.setBackgroundColor(AppPreferences.getDefaultColor());
    buttonColor.setOnClickListener(listener);


    relativeLayoutPassword = (RelativeLayout) root.findViewById(R.id.settings_$_relativelayout_password);
    relativeLayoutPassword.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (AppPreferences.getPassword().equals("")) {
          startActivity(new Intent(getActivity(), NewPasswordActivity.class));
        } else {
          startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
        }
      }
    });

    buttonRate = (Button) root.findViewById(R.id.settings_$_button_rate);
    buttonRate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=chan.android.app.pocketnote"));
        startActivity(intent);
      }
    });

    relativeLayoutPhoto = (RelativeLayout) root.findViewById(R.id.settings_$_relativelayout_photo);
    relativeLayoutPhoto.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        /*
        ActionDialogFragment d = ActionDialogFragment.fragment("Change photo", PHOTO_ACTIONS);
        d.setPickItemListener(new ActionDialogFragment.OnPickItemListener() {
          @Override
          public void onPick(int index) {
            if (PHOTO_ACTIONS.get(index).getName().equals("Take photo")) {
              dispatchTakePictureIntent();
            } else {
              Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
              photoPickerIntent.setType("image/*");
              getActivity().startActivityForResult(photoPickerIntent, INTENT_CHOOSE_PHOTO);
            }
          }
        });
        d.show(getFragmentManager(), "photo_dialog");
        */
      }
    });

    photoImageView = (CircularImageView) root.findViewById(R.id.settings_$_imageview_photo);

    relativeLayoutName = (RelativeLayout) root.findViewById(R.id.settings_$_relativelayout_username);
    relativeLayoutName.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final ChangeNameDialogFragment d = ChangeNameDialogFragment.fragment(AppPreferences.getUserName());
        d.setOnChangeNameListener(new ChangeNameDialogFragment.OnChangeNameListener() {
          @Override
          public void onSave(String username) {
            AppPreferences.saveUserName(username);
            d.dismiss();
            textViewUserName.setText(AppPreferences.getUserName());
          }

          @Override
          public void onReset() {
            AppPreferences.saveUserName("Anonymous");
            d.dismiss();
            textViewUserName.setText(AppPreferences.getUserName());
          }
        });
        d.show(getFragmentManager(), ChangeNameDialogFragment.TAG);
      }
    });

    textViewUserName = (TextView) root.findViewById(R.id.settings_$_textview_display_name);
    textViewUserName.setText(AppPreferences.getUserName());
    return root;
  }

  private void dispatchTakePictureIntent() {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
      File photoFile = null;
      try {
        photoFile = createImageFile();
      } catch (IOException e) {
        Logger.e("Hmm, io exception? " + e.getMessage());
      }
      // Continue only if the File was successfully created
      if (photoFile != null) {
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        getActivity().startActivityForResult(intent, INTENT_TAKE_PHOTO);
      } else {
      }
    }
  }

  private File createImageFile() throws IOException {
    // Create an image file name
    String fileName = "user_photo";
    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    File image = File.createTempFile(
      fileName,  /* prefix */
      ".jpg",    /* suffix */
      storageDir /* directory */
    );
    // Save a file: path for use with ACTION_VIEW intents
    photoPath = "file:" + image.getAbsolutePath();
    AppPreferences.saveUserTempPhotoFilePath(image.getAbsolutePath());
    return image;
  }

  private void displayPhoto(String path) {
    Picasso
      .with(getContext())
      .load(path)
      .error(R.drawable.ic_user)
      .placeholder(R.drawable.ic_user)
      .fit()
      .into(photoImageView);
  }

  @Override
  public void onResume() {
    displayPhoto(AppPreferences.getUserPhotoFilePath());
    super.onResume();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    menu.clear();
    inflater.inflate(R.menu.blank, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  public void invalidate() {
    // Do nothing
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    displayPhoto(AppPreferences.getUserPhotoFilePath());
  }

  private class OnColorClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
      FragmentManager fm = getFragmentManager();
      ColorPickerDialogFragment d = new ColorPickerDialogFragment();
      d.setOnPickColorListener(new ColorPickerDialogFragment.OnPickColorListener() {
        @Override
        public void onPick(int color) {
          buttonColor.setBackgroundColor(color);
          AppPreferences.saveDefaultColor(color);
        }
      });
      d.show(fm, OptionsTabDialogFragment.TAG);
    }
  }
}
