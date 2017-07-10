package gwhitmore.ict4ncd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by whitmorg on 28/04/2017.
 */

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "NCDData.db";
    private static final String TABLE_LOGIN = "LOGIN";
    private static final String TABLE_PRESSURE_DATA = "PRESSURE";
    private static final String TABLE_SUGAR_DATA = "SUGAR";
    private static final String TABLE_WEIGHT_DATA = "WEIGHT";
    private static final String KEY_USERNAME = "USERNAME";
    private static final String KEY_PASSWORD = "PASSWORD";
    private static final String KEY_LANGUAGE = "LANGUAGE";
    private static final String KEY_HEIGHT = "HEIGHT";
    private static final String KEY_ID = "ID";
    private static final String KEY_HEARTRATE = "HEART";
    private static final String KEY_DIASTOLIC = "DIASTOLIC";
    private static final String KEY_SYSTOLIC = "SYSTOLIC";
    private static final String KEY_DATE = "DATE";
    private static final String KEY_SUGAR = "SUGAR";
    private static final String KEY_FOOD = "FOOD";
    private static final String KEY_WEIGHT = "WEIGHT";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_USERNAME + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_LANGUAGE + " TEXT)";
        String CREATE_PRESSURE_DATA_TABLE = "CREATE TABLE " + TABLE_PRESSURE_DATA + "("
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USERNAME + " TEXT,"
                + KEY_HEARTRATE + " INTEGER,"
                + KEY_SYSTOLIC + " INTEGER,"
                + KEY_DIASTOLIC + " INTEGER,"
                + KEY_DATE + " TEXT)";
        String CREATE_SUGAR_DATA_TABLE = "CREATE TABLE " + TABLE_SUGAR_DATA + "("
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USERNAME + " TEXT, "
                + KEY_SUGAR + " DOUBLE,"
                + KEY_FOOD + " TEXT,"
                + KEY_DATE + " TEXT)";
        String CREATE_WEIGHT_DATA_TABLE = "CREATE TABLE " + TABLE_WEIGHT_DATA + "("
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USERNAME + " TEXT,"
                + KEY_HEIGHT + " DOUBLE,"
                + KEY_WEIGHT + " DOUBLE,"
                + KEY_DATE + " TEXT)";
        db.execSQL(CREATE_PRESSURE_DATA_TABLE);
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_SUGAR_DATA_TABLE);
        db.execSQL(CREATE_WEIGHT_DATA_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRESSURE_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUGAR_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHT_DATA);
        onCreate(db);
    }

    public boolean insertLoginData(String username, String password, String language) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USERNAME, username);
        contentValues.put(KEY_PASSWORD, password);
        contentValues.put(KEY_LANGUAGE, language);
        long result = db.insert(TABLE_LOGIN, null, contentValues);
        if (result == -1) return false;
        return true;
    }

    public boolean insertPressureData(String username, Integer heartRateData, Integer systolicData, Integer diastolicData, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USERNAME, username);
        contentValues.put(KEY_HEARTRATE, heartRateData);
        contentValues.put(KEY_SYSTOLIC, systolicData);
        contentValues.put(KEY_DIASTOLIC, diastolicData);
        contentValues.put(KEY_DATE, date);
        long result = db.insert(TABLE_PRESSURE_DATA, null, contentValues);
        if (result == -1) return false;
        return true;
    }

    public boolean insertSugarData(String username, Double sugarData, String food, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USERNAME, username);
        contentValues.put(KEY_SUGAR, sugarData);
        contentValues.put(KEY_FOOD, food);
        contentValues.put(KEY_DATE, date);
        long result = db.insert(TABLE_SUGAR_DATA, null, contentValues);
        if (result == -1) return false;
        return true;
    }

    public boolean insertHeightData(String username, Double heightData, Double weightData, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USERNAME, username);
        contentValues.put(KEY_HEIGHT, heightData);
        contentValues.put(KEY_WEIGHT, weightData);
        contentValues.put(KEY_DATE, date);
        long result = db.insert(TABLE_WEIGHT_DATA, null, contentValues);
        if (result == -1) return false;
        return true;
    }

    public Cursor getAllLoginData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from " + TABLE_LOGIN, null);

        return cursor;
    }

    public void deleteItemPressureTable(Integer idNum) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PRESSURE_DATA + " WHERE " + KEY_ID + "=" + idNum);
    }

    public void deleteItemSugarTable(Integer idNum) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PRESSURE_DATA + " WHERE " + KEY_ID + "=" + idNum);
    }

    public void deleteUserData(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE From " + TABLE_PRESSURE_DATA + " WHERE " + KEY_USERNAME + "='" + username + "'");
        db.execSQL("DELETE FROM " + TABLE_LOGIN + " WHERE " + KEY_USERNAME + "='" + username + "'");
    }

    public void updateUserTable(String username, String language, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_LOGIN + " SET " + KEY_LANGUAGE + "='" + language + "' WHERE "
                + KEY_USERNAME + "='" + username + "'");
    }

    public Cursor getAllPressureData(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from " + TABLE_PRESSURE_DATA + " WHERE " + KEY_USERNAME
                + "='" + username + "'", null);
        return cursor;
    }

    public int getSinglePressureDataID(String username, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRESSURE_DATA + " WHERE " + KEY_USERNAME
                + "='" + username + "' and " + KEY_DATE + "='" + date + "'", null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public int getSingleSugarDataID(String username, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRESSURE_DATA + " WHERE " + KEY_USERNAME
                + "='" + username + "' and " + KEY_DATE + "='" + date + "'", null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public Cursor getAllSugarData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from " + TABLE_SUGAR_DATA, null);
        return cursor;
    }

    public Cursor getAllWeightData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from " + TABLE_WEIGHT_DATA, null);
        return cursor;
    }

//    public Cursor getDataByID(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
////        Cursor cursor = db.query(TABLE_DATA, new String[] { KEY_ID,
////                        KEY_USERNAME, KEY_HEARTRATE, KEY_SYSTOLIC, KEY_DIASTOLIC, KEY_DATE }, KEY_ID + "=?",
////                new String[] { String.valueOf(id) }, null, null, null, null);
//        return cursor;
//    }

    public String getTableAsString() {
        Log.d("something", "getTableAsString called");
        SQLiteDatabase db = this.getWritableDatabase();
        String tableString = String.format("Table %s:\n", TABLE_PRESSURE_DATA);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + TABLE_PRESSURE_DATA, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }


}
