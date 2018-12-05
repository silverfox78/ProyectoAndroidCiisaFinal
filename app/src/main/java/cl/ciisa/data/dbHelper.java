package cl.ciisa.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbHelper extends SQLiteOpenHelper {

    public static final int DATA_BASE_VERSION = 1;
    public static final String DATA_BASE_NAME = "Remember";

    public dbHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String scriptUsuarios = "CREATE TABLE TBL_USUARIOS (ID INT PRIMARY KEY NOT NULL, NOMBRE TEXT NOT NULL, USUERNAME TEXT NOT NULL, PASSWORD TEXT NOT NULL, IMAGEN BLOB)";
        db.execSQL(scriptUsuarios);

        String scriptRecuerdos = "CREATE TABLE TBL_RECUERDOS (ID INT PRIMARY KEY NOT NULL, NOMBRE TEXT NOT NULL, DESCRIPCION TEXT NOT NULL, VALOR REAL NOT NULL, LATITUD REAL, LONGITUD REAL, FECHA TEXT, IMAGEN BLOB)";
        db.execSQL(scriptRecuerdos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
