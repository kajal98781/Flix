package com.kmdev.flix.ui.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.kmdev.flix.models.ResponseMovieDetails;
import com.kmdev.flix.models.ResponseMovieReview;
import com.kmdev.flix.models.ResponseMovieVideo;

import java.util.ArrayList;
import java.util.List;


public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "favouriteManager";
    private static final String TABLE_FAVOURITES = "myFavourites";
    private static final String KEY_FAVOURITE = "favourite";
    private static final String KEY_ID = "movieId";
    private static final String KEY_REVIEW = "review";
    private static final String KEY_VIDEO = "video";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
/*
        String CREATE_MESSAGE_TABLE = "CREATE TABLE " + TABLE_FAVOURITES + "(" + KEY_FAVOURITE + " TEXT" + ")";
*/

        String CREATE_TABLE_MOVIE = "CREATE TABLE " + TABLE_FAVOURITES + "(" + KEY_ID +
                " INTEGER PRIMARY KEY," + KEY_FAVOURITE + " TEXT" + KEY_REVIEW + "TEXT" + KEY_VIDEO +
                "TEXT" + ")";

        db.execSQL(CREATE_TABLE_MOVIE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);

        // Create tables again
        onCreate(db);
    }

    // code to add the new message
    public void addMovies(ResponseMovieDetails movieDetails) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, movieDetails.getId());
        values.put(KEY_FAVOURITE, new Gson().toJson(movieDetails)); // Contact Name
        // values.put(KEY_REVIEW, new Gson().toJson(reviewDetails));
        // values.put(KEY_VIDEO, new Gson().toJson(videoDetails));
        // Inserting Row
        db.insert(TABLE_FAVOURITES, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
/*    MessageModel getMessage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MESSAGE, new String[] { KEY_ID,
                        KEY_MESSAGE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MessageModel contact = new MessageModel(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1));
        // return contact
        return contact;
    }*/


    // code to get all contacts in a list view
    public List<ResponseMovieDetails> getAllMovies() {
        List<ResponseMovieDetails> movieList = new ArrayList<ResponseMovieDetails>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FAVOURITES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //   contact.setID(Integer.parseInt(cursor.getString(0)));
                ResponseMovieDetails movieDetail = new Gson().fromJson(cursor.getString(1), ResponseMovieDetails.class);
                // Adding contact to list
                movieList.add(movieDetail);
            } while (cursor.moveToNext());
        }

        // return contact list
        return movieList;
    }

    //code to get single movie details
    public ResponseMovieDetails getMovieDetails(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVOURITES, new String[]{KEY_ID}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ResponseMovieDetails responseMovieDetails = new ResponseMovieDetails(Integer.parseInt(cursor.getString(0)));
        // return contact
        return responseMovieDetails;
    }

   /* // code to update the single contact
    public int updateContact(ResponseMovieDetails messageModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE, messageModel.getMessage());

        // updating row
        return db.update(TABLE_MESSAGE, values, KEY_MESSAGE + " = ?",
                new String[]{String.valueOf(messageModel.getMessage())});
    }

    // Deleting single contact
    public void deleteContact(MessageModel messageModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGE, KEY_MESSAGE + " = ?",
                new String[]{String.valueOf(messageModel.getMessage())});
   db.delete("tablename","id=? and name=?",new String[]{"1","jack"});

        this is like useing this command:

        delete from tablename where id='1' and name ='jack'

        db.close();
    }*/

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_FAVOURITES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVOURITES, null, null);
    }

    // Deleting single contact
    public void deleteMovie(String movieId) {
        SQLiteDatabase db = this.getWritableDatabase();
   /*     db.delete(TABLE_FAVOURITES, KEY_ID + " = ?",
                new String[]{String.valueOf(movieDetails.getMov())});
        db.delete("tablename","id=?",new String[]{"1","jack"});*/
        db.execSQL("DELETE FROM " + TABLE_FAVOURITES + " WHERE " + KEY_ID + "= '" + movieId + "'");
        /*
        this is like useing this command:
    delete from tablename where id='1' and name ='jack'
*/
        db.close();
    }

    public boolean checkISDataExist(String movieId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String sql = "SELECT * FROM " + TABLE_FAVOURITES + " WHERE " + KEY_ID + "=" + movieId;
        cursor = db.rawQuery(sql, null);
/*
        Log("Cursor Count : " + cursor.getCount());
*/

        //PID Not Found
        return cursor.getCount() > 0;
    }


    public ResponseMovieReview getMovieReview(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVOURITES, new String[]{KEY_ID}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ResponseMovieReview responseMovieDetails = new ResponseMovieReview(Integer.parseInt(cursor.getString(2)));
        // return contact
        return responseMovieDetails;
    }

    public ResponseMovieVideo getMovieVideos(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVOURITES, new String[]{KEY_ID}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ResponseMovieVideo responseMovieDetails = new ResponseMovieVideo(Integer.parseInt(cursor.getString(3)));
        // return contact
        return responseMovieDetails;
    }


}
