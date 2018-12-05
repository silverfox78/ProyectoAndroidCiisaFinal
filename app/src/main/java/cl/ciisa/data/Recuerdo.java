package cl.ciisa.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import cl.ciisa.transfer.TrRecuerdo;

import java.util.ArrayList;

public class Recuerdo {
    private dbHelper dbhelper;

    public Recuerdo(dbHelper dbhelper){
        this.dbhelper = dbhelper;
    }

    public int maximo(){
        SQLiteDatabase db = this.dbhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(ID) FROM TBL_RECUERDOS", null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        else {
            return 0;
        }
    }

    public ArrayList<TrRecuerdo> listar(){
        ArrayList<TrRecuerdo> retorno = new ArrayList<TrRecuerdo>();
        SQLiteDatabase db = this.dbhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID, NOMBRE, DESCRIPCION, VALOR, LATITUD, LONGITUD, FECHA, IMAGEN FROM TBL_RECUERDOS", null);
        if (cursor.moveToFirst()) {
            do {
                TrRecuerdo recuerdo =
                        new TrRecuerdo(
                                cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getDouble(3),
                                cursor.getDouble(4),
                                cursor.getDouble(5),
                                cursor.getString(6),
                                cursor.getBlob(7)
                        );
                retorno.add(recuerdo);
            } while (cursor.moveToNext());
        }
        return retorno;
    }

    public TrRecuerdo buscarPorID(String id){
        SQLiteDatabase db = this.dbhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID, NOMBRE, DESCRIPCION, VALOR, LATITUD, LONGITUD, FECHA, IMAGEN FROM TBL_RECUERDOS WHERE ID=?", new String[] { id });
        if (cursor.moveToFirst()) {
            do {
                TrRecuerdo recuerdo =
                        new TrRecuerdo(
                                cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getDouble(3),
                                cursor.getDouble(4),
                                cursor.getDouble(5),
                                cursor.getString(6),
                                cursor.getBlob(7)
                        );
                return recuerdo;
            } while (cursor.moveToNext());
        }
        return null;
    }

    public void eliminar(String id){
        SQLiteDatabase db = this.dbhelper.getReadableDatabase();
        db.delete("TBL_RECUERDOS","ID=?", new String[] { id });
        db.close();
    }

    public void Grabar(TrRecuerdo recuerdo) throws Exception {
        try {
            SQLiteDatabase db = this.dbhelper.getWritableDatabase();
            String sql = "INSERT INTO TBL_RECUERDOS (ID, NOMBRE, DESCRIPCION, VALOR, LATITUD, LONGITUD, FECHA, IMAGEN) VALUES(?,?,?,?,?,?,?,?)";
            SQLiteStatement insert = db.compileStatement(sql);

            insert.bindLong(1, this.maximo() + 1);
            insert.bindString(2, recuerdo.getNombre());
            insert.bindString(3, recuerdo.getDescripcion());
            insert.bindDouble(4, recuerdo.getValor());
            insert.bindDouble(5, recuerdo.getLatitud());
            insert.bindDouble(6, recuerdo.getLongitud());
            insert.bindString(7, recuerdo.getFecha());
            insert.bindBlob(8, recuerdo.getImagen());
            insert.executeInsert();
            db.close();
        } catch(Exception ex) {
            throw new Exception("Error al generar el registro. - " + ex.getMessage());
        }
    }
}
