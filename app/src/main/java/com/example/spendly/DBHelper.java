package com.example.spendly;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "spendly.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "expenses";

    private static DBHelper instance;

    // One shared database helper for the whole app
    public static synchronized DBHelper get(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "amount REAL NOT NULL, "
                + "category TEXT NOT NULL, "
                + "spent_on INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    // CREATE
    public long insertExpense(Expense e) {
        ContentValues values = new ContentValues();
        values.put("title", e.title);
        values.put("amount", e.amount);
        values.put("category", e.category);
        values.put("spent_on", e.date);
        return getWritableDatabase().insert(TABLE, null, values);
    }

    // DELETE
    public int deleteExpense(long id) {
        return getWritableDatabase().delete(TABLE, "id = ?", new String[]{String.valueOf(id)});
    }

    // READ: everything, newest first
    public List<Expense> getAll() {
        return query(null, null);
    }

    // READ: everything from a given time onwards
    public List<Expense> getSince(long start) {
        return query("spent_on >= ?", new String[]{String.valueOf(start)});
    }

    private List<Expense> query(String where, String[] args) {
        List<Expense> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query(
                TABLE, null, where, args, null, null, "spent_on DESC, id DESC")) {
            while (c.moveToNext()) {
                list.add(new Expense(
                        c.getLong(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("title")),
                        c.getDouble(c.getColumnIndexOrThrow("amount")),
                        c.getString(c.getColumnIndexOrThrow("category")),
                        c.getLong(c.getColumnIndexOrThrow("spent_on"))));
            }
        }
        return list;
    }
}