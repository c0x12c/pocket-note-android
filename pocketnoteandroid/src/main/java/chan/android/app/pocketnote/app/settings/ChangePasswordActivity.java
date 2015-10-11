package chan.android.app.pocketnote.app.settings;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.AppPreferences;
import chan.android.app.pocketnote.util.Hasher;

public class ChangePasswordActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.change_password);

    final EditText editTextOldPassword = (EditText) findViewById(R.id.change_password_$_edittext_old_password);
    final EditText editTextPassword = (EditText) findViewById(R.id.change_password_$_edittext_new_password);
    final EditText editTextRepeat = (EditText) findViewById(R.id.change_password_$_edittext_repeat_password);

    final Button buttonSave = (Button) findViewById(R.id.change_password_$_button_save);
    buttonSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String old = editTextOldPassword.getText().toString();
        String password = editTextPassword.getText().toString();
        String repeat = editTextRepeat.getText().toString();
        if (doesOldPasswordMatch(old)) {
          if (password.equals(repeat)) {
            if (password.length() < 4) {
              editTextPassword.setError("Password must have at least 4 character");
            } else if (password.length() > 8) {
              editTextPassword.setError("Password can't be longer than 8 character");
            } else {
              AppPreferences.savePassword(password);
              toast("Your new password was updated successfully");
              finish();
            }
          } else {
            editTextRepeat.setError("Password mismatch!");
          }
        } else {
          editTextOldPassword.setError("Your password is invalid!");
        }
      }
    });
  }

  private boolean doesOldPasswordMatch(final String oldPassword) {
    return AppPreferences.getPassword().equals(Hasher.md5(oldPassword));
  }

  private void toast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }
}
