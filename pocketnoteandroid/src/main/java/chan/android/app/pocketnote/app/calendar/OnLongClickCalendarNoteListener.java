package chan.android.app.pocketnote.app.calendar;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import chan.android.app.pocketnote.R;
import chan.android.app.pocketnote.app.AppPreferences;
import chan.android.app.pocketnote.app.Note;
import chan.android.app.pocketnote.app.NoteDateFormatter;
import chan.android.app.pocketnote.app.db.PocketNoteManager;
import chan.android.app.pocketnote.app.notes.ActionListDialogFragment;
import chan.android.app.pocketnote.app.reminder.ReminderActivity;
import chan.android.app.pocketnote.app.settings.NewPasswordActivity;
import chan.android.app.pocketnote.app.settings.PasswordDialogFragment;

import java.util.ArrayList;
import java.util.List;

class OnLongClickCalendarNoteListener implements AdapterView.OnItemLongClickListener {

  public Fragment fragment;

  public OnLongClickCalendarNoteListener(Fragment fragment) {
    this.fragment = fragment;
  }

  protected List<Option> getAvailableOptions(Note note) {
    List<Option> options = new ArrayList<Option>();
    options.add(note.isChecked() ? Option.UNCHECK : Option.CHECK);
    options.add(note.isLocked() ? Option.UNLOCK : Option.LOCK);
    options.add(Option.TRASH);
    options.add(Option.EMAIL);
    return options;
  }

  public List<ActionListDialogFragment.Item> getOptionItems(List<Option> options) {
    List<ActionListDialogFragment.Item> result = new ArrayList<ActionListDialogFragment.Item>();
    for (int i = 0, n = options.size(); i < n; ++i) {
      result.add(new ActionListDialogFragment.Item(options.get(i).iconId, options.get(i).name));
    }
    return result;
  }

  @Override
  public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
    final NoteListDialogFragment f = (NoteListDialogFragment) fragment;
    final Note note = (Note) f.adapter.getItem(position);
    final List<Option> options = getAvailableOptions(note);
    ActionListDialogFragment d = ActionListDialogFragment.newInstance(note.getTitle(), getOptionItems(options));
    d.setPickItemListener(new ActionListDialogFragment.OnPickItemListener() {
      @Override
      public void onPick(int index) {
        Option opt = options.get(index);
        opt.choose(f, note);
        f.adapter.notifyDataSetChanged();
      }
    });
    d.show(fragment.getFragmentManager(), "dialog");
    return true;
  }

  public enum Option {

    CHECK("Check", R.drawable.ic_action_check) {
      @Override
      public void choose(Fragment fragment, final Note note) {
        Callback callback = new Callback() {
          @Override
          public void doWork(Fragment fragment, Note note) {
            PocketNoteManager.getPocketNoteManager().check(note);
            NoteAdapterNotifier notifier = (NoteAdapterNotifier) fragment;
            notifier.notifyAdapter();
          }
        };
        if (note.isLocked()) {
          askPassword(fragment, note, callback);
        } else {
          callback.doWork(fragment, note);
        }
      }
    },

    UNCHECK("Uncheck", R.drawable.ic_action_uncheck) {
      @Override
      public void choose(Fragment fragment, Note note) {
        Callback callback = new Callback() {
          @Override
          public void doWork(Fragment fragment, Note note) {
            PocketNoteManager.getPocketNoteManager().uncheck(note);
            NoteAdapterNotifier notifier = (NoteAdapterNotifier) fragment;
            notifier.notifyAdapter();
          }
        };
        if (note.isLocked()) {
          askPassword(fragment, note, callback);
        } else {
          callback.doWork(fragment, note);
        }
      }
    },

    LOCK("Lock", R.drawable.ic_lock) {
      @Override
      public void choose(Fragment fragment, Note note) {
        if (AppPreferences.getPassword().equals("")) {
          Intent intent = new Intent(fragment.getActivity(), NewPasswordActivity.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          fragment.startActivity(intent);
        } else {
          PocketNoteManager.getPocketNoteManager().lock(note);
          NoteAdapterNotifier notifier = (NoteAdapterNotifier) fragment;
          notifier.notifyAdapter();
        }
      }
    },

    UNLOCK("Unlock", R.drawable.ic_unlock) {
      @Override
      public void choose(Fragment fragment, final Note note) {
        askPassword(fragment, note, new Callback() {
          @Override
          public void doWork(Fragment fragment, Note note) {
            PocketNoteManager.getPocketNoteManager().unlock(note);
            NoteAdapterNotifier notifier = (NoteAdapterNotifier) fragment;
            notifier.notifyAdapter();
          }
        });
      }
    },

    TRASH("Delete", R.drawable.ic_drawer_trash) {
      @Override
      public void choose(Fragment fragment, Note note) {
        Callback callback = new Callback() {
          @Override
          public void doWork(Fragment fragment, Note note) {
            PocketNoteManager.getPocketNoteManager().trash(note);
            NoteAdapterNotifier notifier = (NoteAdapterNotifier) fragment;
            List<Note> clone = cloneNoteWithNoTrash(notifier.getNotes());
            notifier.setNotes(clone);
            // TODO: Ugly!!!
            if (notifier instanceof NoteListDialogFragment) {
              NoteListDialogFragment f = (NoteListDialogFragment) fragment;
              f.listener.onNotesChanged();
            }
          }
        };
        if (note.isLocked()) {
          askPassword(fragment, note, callback);
        } else {
          callback.doWork(fragment, note);
        }
      }

      private List<Note> cloneNoteWithNoTrash(List<Note> notes) {
        List<Note> copy = new ArrayList<Note>();
        for (Note n : notes) {
          if (!n.isTrashed()) {
            copy.add(n);
          }
        }
        return copy;
      }
    },

    REMINDER("Reminder", R.drawable.ic_action_clock) {
      @Override
      public void choose(Fragment fragment, Note note) {
        Callback callback = new Callback() {
          @Override
          public void doWork(Fragment fragment, Note note) {
            Intent intent = new Intent(fragment.getActivity(), ReminderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Note.BUNDLE_KEY, note);
            fragment.startActivity(intent);
          }
        };

        if (note.isLocked()) {
          askPassword(fragment, note, callback);
        } else {
          callback.doWork(fragment, note);
        }
      }
    },

    EMAIL("Email", R.drawable.ic_email) {
      @Override
      public void choose(Fragment fragment, Note note) {
        Callback callback = new Callback() {
          @Override
          public void doWork(Fragment fragment, Note note) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, note.getTitle());
            intent.putExtra(Intent.EXTRA_TEXT, note.getContent() + "\n" + NoteDateFormatter.toString(note.getModifiedTime()));
            fragment.startActivity(intent);
          }
        };
        if (note.isLocked()) {
          askPassword(fragment, note, callback);
        } else {
          callback.doWork(fragment, note);
        }
      }
    };

    final String name;
    final int iconId;

    Option(String name, int iconId) {
      this.name = name;
      this.iconId = iconId;
    }

    public abstract void choose(Fragment fragment, Note note);

    public void askPassword(final Fragment fragment, final Note note, final Callback callback) {
      final PasswordDialogFragment d = new PasswordDialogFragment();
      d.show(fragment.getFragmentManager(), "password");
      d.setOnPasswordEnterListener(new PasswordDialogFragment.OnPasswordEnterListener() {
        @Override
        public void onEnter(String password) {
          if (AppPreferences.hasCorrectPassword(password)) {
            d.dismiss();
            callback.doWork(fragment, note);
          } else {
            d.showErrorMessage("Password is incorrect!");
          }
        }
      });
    }
  }

  public interface Callback {
    public void doWork(Fragment fragment, final Note note);
  }
}
