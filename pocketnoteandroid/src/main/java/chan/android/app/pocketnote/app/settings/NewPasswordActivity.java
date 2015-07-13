package chan.android.app.pocketnote.app.settings;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.AppPreferences;

public class NewPasswordActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.new_password);

    final EditText editTextPassword = (EditText) findViewById(R.id.new_password_$_edittext_new_password);
    final EditText editTextRepeat = (EditText) findViewById(R.id.new_password_$_edittext_repeat_password);
    final Button buttonSave = (Button) findViewById(R.id.new_password_$_button_save);
    buttonSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String password = editTextPassword.getText().toString();
        final String repeat = editTextRepeat.getText().toString();
        if (password.equals(repeat)) {
          if (password.length() < 4) {
            editTextPassword.setError("Password must have at least 4 character");
          } else if (password.length() > 8) {
            editTextPassword.setError("Password can't be longer than 8 character");
          } else {
            AppPreferences.savePassword(password);
            toast("Password was created successfully. You can lock all your notes now!");
            finish();
          }
        } else {
          editTextRepeat.setError("Password does not match!");
        }
      }
    });
  }

  private void toast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }
}
