package xyz.mhuy.noteit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener{
    public static final String EXTRA_NOTE_OBJECT = "extra_object";
    public static final String EXTRA_CREATE_NEW_NOTE = "new_blank_note";
    private Toast toast = null;
    private boolean backPressedToExitOnce = false;

    private ListView mainList;
    private ImageView addButton;
    public static List<Note> noteList;
    private static NoteListAdapter noteAdapter;
    private static DatabaseController databaseController;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        addButton = (ImageView) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NoteView.class);
                intent.putExtra(EXTRA_CREATE_NEW_NOTE, -1);
                startActivity(intent);
            }
        });

        mainList = (ListView) this.findViewById(R.id.main_list);
        mainList.setOnItemClickListener(this);
        registerForContextMenu(mainList);


        databaseController = new DatabaseController(this);
        databaseController.open();
        noteList = databaseController.getAllNotes();
        noteAdapter = new NoteListAdapter(this, noteList);
        this.mainList.setAdapter(noteAdapter);
        databaseController.close();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, NoteView.class);
        intent.putExtra(EXTRA_CREATE_NEW_NOTE, 1);
        intent.putExtra(EXTRA_NOTE_OBJECT, noteList.get(i));
        startActivity(intent);
    }

    public static void refresh() {
        databaseController.open();
        noteList.clear();
        noteList.addAll(databaseController.getAllNotes());
        if(noteList.isEmpty()) {
            Toast.makeText(context, "Empty!", Toast.LENGTH_SHORT).show();
        }
        databaseController.close();
        noteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, R.id.context_menu_delete_note, Menu.NONE, "Delete");
    }

    void askDelete(final Note note) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Delete?");
        dialog.setMessage(note.getTitle());
        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseController.open();
                databaseController.deleteNote(note.getId());
                databaseController.close();
                refresh();
            }
        });
        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(context, "Cancel delete!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.create().show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        Note note = noteList.get(index);
        switch(item.getItemId()) {
            case R.id.context_menu_delete_note:
                askDelete(note);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (backPressedToExitOnce) {
            super.onBackPressed();
        } else {
            this.backPressedToExitOnce = true;
            showToast("Press again to exit");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressedToExitOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onPause() {
        killToast();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void killToast() {
        if (this.toast != null) {
            this.toast.cancel();
        }
    }

    private void showToast(String message) {
        if (this.toast == null) {
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        } else if (this.toast.getView() == null) {
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        } else {
            this.toast.setText(message);
        }
        this.toast.show();
    }
}
