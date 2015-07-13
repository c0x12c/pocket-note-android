package chan.android.app.pocketnote.app.settings;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import chan.android.app.pocketnote.R;

public class PasswordDialogFragment extends DialogFragment {

  private OnPasswordEnterListener listener;
  private EditText password;
  private Button enter;

  public PasswordDialogFragment() {
    //
  }

  public void setOnPasswordEnterListener(OnPasswordEnterListener listener) {
    this.listener = listener;
  }

  public void removeOnPasswordListener() {
    this.listener = null;
  }

  public void showErrorMessage(String errorMessage) {
    password.setError(errorMessage);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.password_dialog, container, false);
    password = (EditText) root.findViewById(R.id.password_dialog_$_edittext_password);
    password.requestFocus();
    enter = (Button) root.findViewById(R.id.password_dialog_$_button_enter);
    enter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        enter.setError(null);
        if (listener != null) {
          listener.onEnter(password.getText().toString());
        }
      }
    });
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    return root;
  }

  public interface OnPasswordEnterListener {

    public void onEnter(String password);
  }
}
