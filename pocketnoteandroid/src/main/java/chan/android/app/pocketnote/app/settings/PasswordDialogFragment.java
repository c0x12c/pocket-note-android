package chan.android.app.pocketnote.app.settings;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import chan.android.app.pocketnote.R;

public class PasswordDialogFragment extends DialogFragment {

  public static final String TAG = PasswordDialogFragment.class.getSimpleName();

  private OnPasswordEnterListener listener;

  private EditText passwordEditor;

  private Button enterButton;

  public static PasswordDialogFragment fragment() {
    return new PasswordDialogFragment();
  }

  public void setOnPasswordEnterListener(OnPasswordEnterListener listener) {
    this.listener = listener;
  }

  public void removeOnPasswordListener() {
    this.listener = null;
  }

  public void showErrorMessage(String errorMessage) {
    passwordEditor.setError(errorMessage);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.password_dialog, container, false);
    passwordEditor = (EditText) root.findViewById(R.id.password_dialog_$_edittext_password);
    passwordEditor.requestFocus();
    enterButton = (Button) root.findViewById(R.id.password_dialog_$_button_enter);
    enterButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        enterButton.setError(null);
        if (listener != null) {
          listener.onEnter(passwordEditor.getText().toString());
        }
      }
    });
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    return root;
  }

  public interface OnPasswordEnterListener {

    void onEnter(String password);
  }
}
