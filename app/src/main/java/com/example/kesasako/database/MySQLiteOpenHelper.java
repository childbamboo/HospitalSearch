package com.example.kesasako.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kesasako on 2015/01/11.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseOpenHelper";
    private static final String SRC_DB_NAME = "HospitalDB";  // assetsフォルダにあるdbのファイル名
    static final String DST_DB_NAME = "HospitalDB";

    static final int DB_VERSION = 1;

    private final Context context;
    private final File databasePath;
    private boolean createDatabase = false;

    public MySQLiteOpenHelper(Context c) {
        super(c, DST_DB_NAME, null, DB_VERSION);
        this.context = c;
        this.databasePath = context.getDatabasePath(DST_DB_NAME);
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase database = super.getWritableDatabase();
        if (createDatabase) {
            try {
                database = copyDatabase(database);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                // TODO: エラー処理
            }
        }
        return database;
    }

    private SQLiteDatabase copyDatabase(SQLiteDatabase database) throws IOException {
        // データベースをクローズしてからコピー
        database.close();

        // データのコピー
        InputStream input = context.getAssets().open(SRC_DB_NAME);
        OutputStream output = new FileOutputStream(databasePath);
        copy(input, output);

        createDatabase = false;

        // データベースをオープンする
        return super.getWritableDatabase();
    }

    public void onCreate(SQLiteDatabase db) {
        super.onOpen(db);
        this.createDatabase = true;
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // データのコピー
    private static int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024 * 4];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}