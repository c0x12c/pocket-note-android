package chan.android.app.pocketnote.app.trash;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import chan.android.app.pocketnote.R;

public class ConfirmDialogFragment extends DialogFragment {

  private OnConfirmListener listener;
  private String message;
  private String leftButtonText;
  private String rightButtonText;

  public ConfirmDialogFragment() {
    //
  }

  public ConfirmDialogFragment(String message, String leftButtonText, String rightButtonText) {
    this.message = message;
    this.leftButtonText = leftButtonText;
    this.rightButtonText = rightButtonText;
  }

  public void setOnConfirmListener(OnConfirmListener listener) {
    this.listener = listener;
  }

  public void removeOnConfirmListener() {
    this.listener = null;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.confirm_dialog, container, false);
    Button ok = (Button) root.findViewById(R.id.confirm_dialog_$_button_ok);
    Button cancel = (Button) root.findViewById(R.id.confirm_dialog_$_button_cancel);
    TextView msg = (TextView) root.findViewById(R.id.confirm_dialog_$_textview_message);

    if (leftButtonText != null) {
      cancel.setText(leftButtonText);
    }

    if (rightButtonText != null) {
      ok.setText(rightButtonText);
    }

    if (message != null) {
      msg.setText(message);
    }

    ok.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (listener != null) {
          listener.onEnter(true);
        }
        dismiss();
      }
    });

    cancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (listener != null) {
          listener.onEnter(false);
        }
        dismiss();
      }
    });
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    return root;
  }

  public interface OnConfirmListener {

    public void onEnter(boolean ok);
  }
}
