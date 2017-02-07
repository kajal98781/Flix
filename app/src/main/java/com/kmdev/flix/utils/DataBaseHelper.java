package com.kmdev.flix.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.kmdev.flix.models.DataBaseMovieDetails;
import com.kmdev.flix.models.ResponseMovieDetails;
import com.kmdev.flix.models.ResponseMovieReview;
import com.kmdev.flix.models.ResponsePopularMovie;
import com.kmdev.flix.models.ResponseTvDetails;
import com.kmdev.flix.models.ResponseTvPopular;
import com.kmdev.flix.models.ResponseVideo;

import java.util.ArrayList;
import java.util.List;


public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "favouriteManager";
    private static final String TABLE_FAVOURITES = "myFavourites";
    private static final String KEY_FAVOURITE = "favourite";
    //0 for movie ,1 for TV
    private static final String KEY_TYPE = "type";
    private static final String KEY_ID = "movieId";
    private static final String KEY_REVIEW = "review";
    private static final String KEY_VIDEO = "video";
    private static final String KEY_SIMILAR_MOVIES = "similar_movies";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables/
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_MOVIE = "CREATE TABLE " + TABLE_FAVOURITES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FAVOURITE + " TEXT,"
                + KEY_TYPE + " INTEGER,"
                + KEY_SIMILAR_MOVIES + " TEXT,"
                + KEY_REVIEW + " TEXT," + KEY_VIDEO + " TEXT" + ")";


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

    // code to add the new messaage
    public void addMovies(DataBaseMovieDetails dataBaseMovieDetails) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, dataBaseMovieDetails.getId());
        if (dataBaseMovieDetails.getType() == 0) {
            values.put(KEY_FAVOURITE, new Gson().toJson(dataBaseMovieDetails.getResponseMovieDetails())); // Contact Name
            values.put(KEY_TYPE, new Gson().toJson(dataBaseMovieDetails.getType()));
            values.put(KEY_SIMILAR_MOVIES, new Gson().toJson(dataBaseMovieDetails.getResponseSimilarMovies()));

        } else if (dataBaseMovieDetails.getType() == 1) {
            values.put(KEY_FAVOURITE, new Gson().toJson(dataBaseMovieDetails.getResponseTvDetails())); // Contact Name
            values.put(KEY_TYPE, new Gson().toJson(dataBaseMovieDetails.getType()));
            values.put(KEY_SIMILAR_MOVIES, new Gson().toJson(dataBaseMovieDetails.getResponseTvSimilarShows()));

        }
        values.put(KEY_REVIEW, new Gson().toJson(dataBaseMovieDetails.getResponseMovieReview()));
        values.put(KEY_VIDEO, new Gson().toJson(dataBaseMovieDetails.getResponseMovieVideo()));
        // Inserting Row
        db.insert(TABLE_FAVOURITES, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }


    // code to get all contacts in a list view
    public List<ResponseMovieDetails> getAllMovies() {
        List<ResponseMovieDetails> movieList = new ArrayList<ResponseMovieDetails>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FAVOURITES + " WHERE " + KEY_TYPE + "=0";

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

    // code to get all contacts in a list view
    public List<ResponseTvDetails> getAllTvShows() {
        List<ResponseTvDetails> tvDetailsList = new ArrayList<ResponseTvDetails>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FAVOURITES + " WHERE " + KEY_TYPE + "=1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //   contact.setID(Integer.parseInt(cursor.getString(0)));
                ResponseTvDetails tvDetails = new Gson().fromJson(cursor.getString(1), ResponseTvDetails.class);
                // Adding contact to list
                tvDetailsList.add(tvDetails);
            } while (cursor.moveToNext());
        }

        // return contact list
        return tvDetailsList;
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
        return cursor.getCount() > 0;
    }

    public DataBaseMovieDetails getMovieDetails(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVOURITES, new String[]{KEY_ID, KEY_FAVOURITE, KEY_SIMILAR_MOVIES,
                        KEY_REVIEW, KEY_VIDEO}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        DataBaseMovieDetails dataBaseMovieDetails = new DataBaseMovieDetails();
        dataBaseMovieDetails.setId(id);
        dataBaseMovieDetails.setResponseSimilarMovies(new Gson().fromJson(cursor.getString(2), ResponsePopularMovie.class));
        dataBaseMovieDetails.setResponseMovieReview(new Gson().fromJson(cursor.getString(3), ResponseMovieReview.class));
        dataBaseMovieDetails.setResponseMovieVideo(new Gson().fromJson(cursor.getString(4), ResponseVideo.class));
        dataBaseMovieDetails.setResponseMovieDetails(new Gson().fromJson(cursor.getString(1), ResponseMovieDetails.class));

        // return contact
        return dataBaseMovieDetails;
    }

    public DataBaseMovieDetails getShowDetails(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVOURITES, new String[]{KEY_ID, KEY_FAVOURITE, KEY_SIMILAR_MOVIES,
                        KEY_REVIEW, KEY_VIDEO}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        DataBaseMovieDetails dataBaseMovieDetails = new DataBaseMovieDetails();
        dataBaseMovieDetails.setId(id);
        dataBaseMovieDetails.setResponseTvSimilarShows(new Gson().fromJson(cursor.getString(2), ResponseTvPopular.class));
        dataBaseMovieDetails.setResponseMovieReview(new Gson().fromJson(cursor.getString(3), ResponseMovieReview.class));
        dataBaseMovieDetails.setResponseMovieVideo(new Gson().fromJson(cursor.getString(4), ResponseVideo.class));
        dataBaseMovieDetails.setResponseTvDetails(new Gson().fromJson(cursor.getString(1), ResponseTvDetails.class));

        // return contact
        return dataBaseMovieDetails;
    }

    public DataBaseMovieDetails getMovieVideo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVOURITES, new String[]{KEY_ID, KEY_FAVOURITE, KEY_REVIEW, KEY_VIDEO},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        DataBaseMovieDetails responseMovieDetails = new DataBaseMovieDetails();
        responseMovieDetails.setId(id);
        responseMovieDetails.setResponseMovieVideo(new Gson().fromJson(cursor.getString(3), ResponseVideo.class));
        responseMovieDetails.setResponseMovieDetails(new Gson().fromJson(cursor.getString(1), ResponseMovieDetails.class));
        // return contact
        return responseMovieDetails;
    }


}
