package xyz.mhuy.noteit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Huy N on 28-Aug-16.
 */
public class DatabaseController {
    private static final String TAG = "seafjgsdf";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mynote.db";

    public static final String TABLE_NOTES = "notes";

    public static final String KEY_NID = "nid";
    public static final String KEY_DATE = "date";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "content";

    private String[] allNoteColumn = {
            KEY_NID,
            KEY_DATE,
            KEY_TITLE,
            KEY_CONTENT};

    public static final String CREATE_NOTE_TABLES = "CREATE TABLE " + TABLE_NOTES + "( "
            + KEY_NID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + KEY_DATE + " DATETIME, "
            + KEY_TITLE + " TEXT, "
            + KEY_CONTENT + " TEXT);";

    private Context context;
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;


    public DatabaseController(Context context) {
        this.context = context;
    }

    public DatabaseController open() throws android.database.SQLException {
        dbHelper = new MySQLiteHelper(this.context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() { dbHelper.close(); }

    public ArrayList<Note> getAllNotes() {
        ArrayList<Note> res = new ArrayList<Note>();

        Cursor cursor = database.query(TABLE_NOTES, allNoteColumn, null, null, null, null, null);
        for(cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            Log.v(TAG, "FAILED HERE");
            Note note = cursorToNote(cursor);
            res.add(note);
        }
        cursor.close();
        return res;
    }

    private Note cursorToNote(Cursor cursor) {
        Note note = new Note(
                cursor.getLong(0),
                cursor.getLong(1),
                cursor.getString(2),
                cursor.getString(3));
        return note;
    }

    public Note addNote(String title, String content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DATE, "" + Calendar.getInstance().getTimeInMillis());
        contentValues.put(KEY_TITLE, title);
        contentValues.put(KEY_CONTENT, content);
        long id = (int) database.insert(TABLE_NOTES, null, contentValues);

        Cursor c = database.query(TABLE_NOTES,
                allNoteColumn, KEY_NID + " = " + id, null, null, null, null);
        c.moveToFirst();
        Note note = cursorToNote(c);
        c.close();
        return note;
    }

    // DELETE
    public long deleteNote(long id) {
        return database.delete(TABLE_NOTES, KEY_NID + " = " + id, null);
    }

    public long updateNote(long id, String title, String content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TITLE, title);
        contentValues.put(KEY_CONTENT, content);
        contentValues.put(KEY_DATE, "" + Calendar.getInstance().getTimeInMillis());

        return database.update(TABLE_NOTES, contentValues, KEY_NID + " = " + id, null);
    }



    private static class MySQLiteHelper extends SQLiteOpenHelper {

        MySQLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_NOTE_TABLES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "onUpgrade: old version " + oldVersion + "--- new version: " + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
            onCreate(db);
        }
    }
}
