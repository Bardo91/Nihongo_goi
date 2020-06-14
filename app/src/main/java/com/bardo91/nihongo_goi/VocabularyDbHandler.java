package com.bardo91.nihongo_goi;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

public class VocabularyDbHandler extends SQLiteOpenHelper {
    private SQLiteDatabase myDataBase;
    private Context myContext = null;

    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "nihongo_goi.db";
    public final static String DATABASE_PATH = "/data/data/com.bardo91.nihongo_goi/databases/";
    public static final String TABLE_NAME = "goi";
    public static final String COLUMN_ID = "word_id";
    public static final String COLUMN_ESP = "spanish";
    public static final String COLUMN_JAP = "japanse";

    public VocabularyDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }


    //Create a empty database on the system
    public void createDatabase() throws IOException {
        boolean dbExist = checkDataBase();
        if(dbExist)  {
            Log.v("DB Exists", "db exists");
            // By calling this method here onUpgrade will be called on a
            // writeable database, but only if the version number has been
            // bumped
            //onUpgrade(myDataBase, DATABASE_VERSION_old, DATABASE_VERSION);
        }

        boolean dbExist1 = checkDataBase();
        if(!dbExist1)  {
            this.getReadableDatabase();
            try  {
                this.close();
                copyDataBase();
            }
            catch (IOException e)  {
                throw new Error("Error copying database");
            }
        }

    }
    //Check database already exist or not
    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            String myPath = DATABASE_PATH + DATABASE_NAME;
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        }
        catch(SQLiteException e) {
        }
        return checkDB;
    }
    //Copies your database from your local assets-folder to the just created empty database in the system folder
    private void copyDataBase() throws IOException {
        InputStream mInput = myContext.getAssets().open(DATABASE_NAME);
        String outFileName = DATABASE_PATH + DATABASE_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[2024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    //delete database
    public void db_delete() {
        File file = new File(DATABASE_PATH + DATABASE_NAME);
        if(file.exists()) {
            file.delete();
            System.out.println("delete database file.");
        }
    }
    //Open database
    public void openDatabase() throws SQLException {
        String myPath = DATABASE_PATH + DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void closeDataBase()throws SQLException {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            Log.v("Database Upgrade", "Database version higher than old.");
            db_delete();
        }
    }

    ArrayList<String> tableList(){
        ArrayList<String> tables = new ArrayList<String>();
        Cursor c = myDataBase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                String tableName = c.getString( c.getColumnIndex("name"));
                c.moveToNext();
                if(tableName.contains("sql") || tableName.contains("metadata")){
                    continue;
                }
                tables.add( tableName);
            }
        }

        return tables;
    }

    public ArrayList<VocabularyWord> getAllTable( String table){
        ArrayList<VocabularyWord> wordList = new ArrayList<>();
        Cursor cursor = myDataBase.rawQuery("select * from "+table, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                VocabularyWord word = new VocabularyWord();
                word.setSpanish(cursor.getString(0));
                word.setJapanese(cursor.getString(1));
                cursor.moveToNext();
                wordList.add(word);
            }
        }
        cursor.close();

        return wordList;
    }

    public VocabularyWord randomQuery(){
        ArrayList<String> tables = new ArrayList<String>();
        Cursor c = myDataBase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                tables.add( c.getString( c.getColumnIndex("name")) );
                c.moveToNext();
            }
        }

        //String[] tables = {"family", "colours", "food", "hobbies", "jobs", "desktop", "places", "verbs", "transport"};
        String randomTable= tables.get(new Random().nextInt(tables.size()));
        Cursor cursor = myDataBase.rawQuery("select * from "+randomTable+" where rowid = (abs(random()) % (select max(rowid)+1 from family));", null);

        VocabularyWord word = new VocabularyWord();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            word.setSpanish(cursor.getString(0));
            word.setJapanese(cursor.getString(1));
            cursor.close();
        } else {
            word = null;
        }

        return word;
    }

}