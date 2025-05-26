package com.example.heath_android;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class DatabaseInformation extends SQLiteOpenHelper{
    public DatabaseInformation(Context context) {
        super(context, "NguoiDung.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE User(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "hoten TEXT, " +
                "email TEXT, " +
                "matkhau TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS User");
        onCreate(db);
    }

    public boolean insertUser(String hoten, String email, String matkhau) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE email = ?", new String[]{email});
        if (cursor.getCount() > 0) return false;

        ContentValues values = new ContentValues();
        values.put("hoten", hoten);
        values.put("email", email);
        values.put("matkhau", md5(matkhau)); // Mã hóa tại đây
        long result = db.insert("User", null, values);
        return result != -1;
    }


    public boolean checkUser(String email, String matkhau) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = md5(matkhau);
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE email = ? AND matkhau = ?", new String[]{email, hashedPassword});
        return cursor.getCount() > 0;
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean updatePassword(String tenDangNhap, String matKhauMoi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("matKhau", matKhauMoi);
        long result = db.update("TaiKhoan", values, "tenDangNhap = ?", new String[]{tenDangNhap});
        return result != -1;
    }
}
