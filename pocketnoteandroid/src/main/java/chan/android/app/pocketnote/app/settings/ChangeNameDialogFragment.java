package chan.android.app.pocketnote.app.settings;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import chan.android.app.pocketnote.R;

public class ChangeNameDialogFragment extends DialogFragment {

  private OnChangeNameListener listener;
  private EditText username;
  private Button reset;
  private Button save;
  private String name;

  public ChangeNameDialogFragment(String name) {
    this.name = name;
  }

  public void setOnChangeNameListener(OnChangeNameListener listener) {
    this.listener = listener;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.username_dialog, container, false);
    username = (EditText) root.findViewById(R.id.username_dialog_$_edittext_username);
    username.setText(name);
    username.requestFocus();
    reset = (Button) root.findViewById(R.id.username_dialog_$_button_reset);
    reset.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        reset.setError(null);
        if (listener != null) {
          listener.onReset();
        }
      }
    });
    save = (Button) root.findViewById(R.id.username_dialog_$_button_save);
    save.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        save.setError(null);
        if (listener != null) {
          listener.onSave(username.getText().toString());
        }
      }
    });
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    return root;
  }

  public interface OnChangeNameListener {

    public void onSave(String username);

    public void onReset();
  }
}

