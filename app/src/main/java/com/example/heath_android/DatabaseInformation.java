package com.example.heath_android;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class DatabaseInformation extends SQLiteOpenHelper{
    public static final String DB_NAME = "User.db";
    public static final String TABLE_NAME = "Users";
    public static final String COL_ID = "ID";
    public static final String COL_USERNAME = "username";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";

    public DatabaseInformation(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_PASSWORD + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Mã hóa mật khẩu bằng MD5
    private String md5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Đăng ký - mã hóa mật khẩu trước khi lưu
    public boolean insertUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, md5Hash(password)); // Mã hóa mật khẩu

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    // Đăng nhập - mã hóa mật khẩu trước khi so sánh
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = md5Hash(password); // Mã hóa mật khẩu nhập vào

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                " WHERE email=? AND password=?", new String[]{email, hashedPassword});

        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }
}
