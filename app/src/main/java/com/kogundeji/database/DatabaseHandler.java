package com.kogundeji.database;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.kogundeji.R;
import com.kogundeji.model.Option;
import com.kogundeji.util.Util;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    public DatabaseHandler(@Nullable Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Delete existing table
        String DROP_TABLE = String.valueOf(R.string.drop_db_string);
        db.execSQL(DROP_TABLE, new String[]{Util.DATABASE_NAME});

        //create new table with same information
        createTable(db);
    }

    public void refreshTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String DROP_TABLE = "DROP TABLE IF EXISTS " + Util.TABLE_NAME;
        db.execSQL(DROP_TABLE);

        createTable(db);
    }

    public void createTable(SQLiteDatabase db) {
        String CREATE_CONTACT_TABLE = "CREATE TABLE " + Util.TABLE_NAME + "("
                + Util.KEY_ID + " INTEGER PRIMARY KEY," //autoincrements automatically because it is the primary key
                + Util.KEY_TICKER + " TEXT,"
                + Util.KEY_STRIKE + " REAL,"
                + Util.KEY_CURRENT + " REAL,"
                + Util.KEY_VOL + " REAL,"
                + Util.KEY_EXPIRATION + " TEXT,"
                + Util.KEY_RFRATE + " REAL)";

        db.execSQL(CREATE_CONTACT_TABLE);
    }

    //CRUD = Create(Add), Read, Update, Delete

    public void addOption(Option option) {
        SQLiteDatabase db = this.getWritableDatabase();

        //create database row from option information
        ContentValues values = new ContentValues();
        values.put(Util.KEY_TICKER, option.getTicker_symbol());
        values.put(Util.KEY_STRIKE, option.getStrike());
        values.put(Util.KEY_CURRENT, option.getCurrent());
        values.put(Util.KEY_VOL, option.getVolatility());
        values.put(Util.KEY_EXPIRATION, option.getExpiration());
        values.put(Util.KEY_RFRATE, option.getRfRate());

        //insert database row into db
        db.insert(Util.TABLE_NAME, null, values);
        db.close(); //close db connection
    }

    public Option getOption(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Util.TABLE_NAME,
                new String[]{Util.KEY_TICKER, Util.KEY_STRIKE, Util.KEY_CURRENT,
                        Util.KEY_VOL, Util.KEY_EXPIRATION, Util.KEY_RFRATE},
                Util.KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Option option = new Option();
            option.setId(Integer.parseInt(cursor.getString(0)));
            option.setTicker_symbol(cursor.getString(1));
            option.setStrike(Double.parseDouble(cursor.getString(2)));
            option.setCurrent(Double.parseDouble(cursor.getString(3)));
            option.setVolatility(Double.parseDouble(cursor.getString(4)));
            option.setExpiration(cursor.getString(5));
            option.setRfRate(Double.parseDouble(cursor.getString(6)));

            db.close();
            return option;
        }
        db.close();
        return null;
    }

    public ArrayList<Option> getAllOptions() {
        ArrayList<Option> optionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //select all contacts
        String selectAll = "SELECT * FROM " + Util.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll,null);

        //Loop through data
        if (cursor.moveToFirst()) {
            do {
                Option option = new Option();
                option.setId(Integer.parseInt(cursor.getString(0)));
                option.setTicker_symbol(cursor.getString(1));
                option.setStrike(Double.parseDouble(cursor.getString(2)));
                option.setCurrent(Double.parseDouble(cursor.getString(3)));
                option.setVolatility(Double.parseDouble(cursor.getString(4)));
                option.setExpiration(cursor.getString(5));
                option.setRfRate(Double.parseDouble(cursor.getString(6)));

                Log.d("ID Testing", "id: " + cursor.getString(0));

                optionList.add(option);

            } while (cursor.moveToNext());
        }
        return optionList;
    }

    public void updateOption(Option option) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_TICKER, option.getTicker_symbol());
        values.put(Util.KEY_STRIKE, option.getStrike());
        values.put(Util.KEY_CURRENT, option.getCurrent());
        values.put(Util.KEY_VOL, option.getVolatility());
        values.put(Util.KEY_EXPIRATION, option.getExpiration());
        values.put(Util.KEY_RFRATE, option.getRfRate());

        db.update(Util.TABLE_NAME, values,Util.KEY_ID + "=?",
                new String[]{String.valueOf(option.getId())});
        db.close();
    }

    public void deleteOption(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Util.TABLE_NAME,Util.KEY_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public int getCount() {
        String countQuery = "SELECT * FROM " + Util.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);

        return cursor.getCount();
    }
}
