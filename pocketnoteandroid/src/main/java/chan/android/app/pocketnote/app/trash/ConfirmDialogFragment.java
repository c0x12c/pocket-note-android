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

  public static final String TAG = ConfirmDialogFragment.class.getSimpleName();

  private OnConfirmListener listener;

  interface Args {

    String MESSAGE = TAG + ".message";

    String LEFT_BUTTON_TEXT = TAG + ".left";

    String RIGHT_BUTTON_TEXT = TAG + ".right";
  }

  private String message;

  private String leftButtonText;

  private String rightButtonText;

  public static ConfirmDialogFragment fragment(String message, String leftButtonText, String rightButtonText) {
    final Bundle args = new Bundle();
    args.putString(Args.MESSAGE, message);
    args.putString(Args.LEFT_BUTTON_TEXT, leftButtonText);
    args.putString(Args.RIGHT_BUTTON_TEXT, rightButtonText);
    final ConfirmDialogFragment d = new ConfirmDialogFragment();
    d.setArguments(args);
    return d;
  }

  @Override
  public void onCreate(Bundle bundle) {
    final Bundle args = getArguments();
    message = args.getString(Args.MESSAGE);
    leftButtonText = args.getString(Args.LEFT_BUTTON_TEXT);
    rightButtonText = args.getString(Args.RIGHT_BUTTON_TEXT);
  }

  public void setOnConfirmListener(OnConfirmListener listener) {
    this.listener = listener;
  }

  public void removeOnConfirmListener() {
    this.listener = null;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View root = inflater.inflate(
      R.layout.confirm_dialog, container, false);
    final Button ok = (Button) root.findViewById(
      R.id.confirm_dialog_$_button_ok);
    final Button cancel = (Button) root.findViewById(
      R.id.confirm_dialog_$_button_cancel);
    final TextView msg = (TextView) root.findViewById(
      R.id.confirm_dialog_$_textview_message);

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

    void onEnter(boolean ok);
  }
}
