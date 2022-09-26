package uk.ac.abertay.cmp309.assessmentempty;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBManager extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_REMINDERS = " create table REMINDERS ( _id INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER NOT NULL, info TEXT NOT NULL , date DATE NOT NULL, time TEXT NOT NULL, location TEXT, notification INTEGER NOT NULL );";
    private static final String DB_NAME = "REMINDERS_DB";
    SQLiteDatabase database;

    //No injection protection as the only records the user can harm is their own and including injection can lead to inefficiency.

    //Constructor
    public DBManager(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    //Creating table
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_REMINDERS);
    }

    //Upgrading table
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS REMINDERS");
        onCreate(db);
    }

    //Adding data to database
    public String insert(int type, String info, String date, String time, String location, int notification) {
        database = this.getReadableDatabase();
        ContentValues contentValue = new ContentValues();

            contentValue.put("type", type);
            contentValue.put("info", info);
            contentValue.put("date", date);
            contentValue.put("time", time);
            contentValue.put("location", location);
            contentValue.put("notification", notification);

            float result = database.insert("REMINDERS", null, contentValue);

        if (result == -1)
        {
            return "Failure";
        }
        else
        {
            return "Success";
        }


    }

    //Read all records from database
    public Cursor readAll(){
        database = this.getWritableDatabase();
        String query = "select _id, type, info, date, time, location, notification from REMINDERS order by date asc";
        return database.rawQuery(query, null);
    }

    //Read all records, suitable for Calendar Fragment
    public Cursor readAllCalendar(String date){
        database = this.getReadableDatabase();
        String query = "select _id, type, info, date, time, location, notification from REMINDERS where date = '" + date + "' order by time asc";
        return database.rawQuery(query, null);
    }

    //Reading needed records for populating map with data
    public Cursor readAllMap(){
        database = this.getReadableDatabase();
        String query = "select _id, info, location from REMINDERS";
        return database.rawQuery(query, null);
    }


    //Retrieve one specific record
    public Cursor retrieveSpecific(int id) {
        database = this.getReadableDatabase();
        String query = "select _id, type, info, date, time, location, notification from REMINDERS where _id = " + id;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    //Retrieve the latest added record
    public Cursor retrieveLatest() {
        database = this.getReadableDatabase();
        String query = "select MAX(_id) from REMINDERS";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    //Deleting a specific record
    public void delete(int _id) {
        database = this.getWritableDatabase();
        database.delete("REMINDERS", "_id=" + _id, null);
    }

    //Updating the notification value in the database
    public void updateNotifications(int _id, int notification) {
        database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("notification", notification);
        database.update("REMINDERS", cv, "_id = " + _id, null);
    }

}
