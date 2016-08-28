package xyz.mhuy.noteit;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class NoteView extends Activity {
    private EditText content;
    private EditText title;
    String gotTitle;
    String gotContent;
    Note note;
    private int index;
    private DatabaseController notesTableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_note_view);

        this.title = (EditText) findViewById(R.id.note_edit_title);
        this.content = (EditText) findViewById(R.id.note_edit_content);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            index = bundle.getInt(MainActivity.EXTRA_CREATE_NEW_NOTE);
            if(index == -1) {

            } else if(index == 1) {
                this.note = (Note) bundle.getSerializable(MainActivity.EXTRA_NOTE_OBJECT);
                gotTitle = note.getTitle();
                gotContent = note.getContent();
                title.setText(gotTitle);
                content.setText(gotContent);
            }
        }
    }

    private void saveIt() {
        notesTableAdapter = new DatabaseController(getBaseContext());
        notesTableAdapter.open();
        if(index == -1) {
            if(title.getText().toString().trim().equals("")) {
                if(! content.getText().toString().trim().equals("")) {
                    notesTableAdapter.addNote("No Title", content.getText().toString());
                    notesTableAdapter.close();
                    Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Content is required. Nothing saved!", Toast.LENGTH_SHORT).show();
                }
            } else {
                notesTableAdapter.addNote(title.getText().toString(), content.getText().toString());
                notesTableAdapter.close();
                Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if(index == 1) {
            if(title.getText().toString().trim().equals(gotTitle)) {
                if(content.getText().toString().trim().equals(gotContent)) { // same title, same content
                    Toast.makeText(getApplicationContext(), "Nothing changed!", Toast.LENGTH_SHORT).show();
                    finish();
                } else { // same title, different content
                    notesTableAdapter.updateNote(note.getId(), gotTitle, content.getText().toString());
                    notesTableAdapter.close();
                    Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else { // different title, difference content.
                notesTableAdapter.updateNote(note.getId(), title.getText().toString(), content.getText().toString());
                notesTableAdapter.close();
                Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveIt();
    }
}