package cl.ciisa.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.support.annotation.RequiresApi;
import cl.ciisa.transfer.TrUsuario;
//import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class Usuario {
    private dbHelper dbhelper;

    public Usuario(dbHelper dbhelper){
        this.dbhelper = dbhelper;
    }

    public Boolean Existe(String username){
        if(isNullOrBlank(username)){
            return false;
        }

        try {
            String usernameLimpio = username.toUpperCase().trim();
            SQLiteDatabase db = this.dbhelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT ID FROM TBL_USUARIOS WHERE USUERNAME = ?", new String[] { usernameLimpio });
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                return id > 0;
            }
            else {
                return false;
            }
        } catch(Exception ex) {
            return false;
        }
    }

    public Boolean EsValido(String username, String clave){
        if(isNullOrBlank(username) || isNullOrBlank(clave)){
            return false;
        }

        try {
            String usernameLimpio = username.toUpperCase().trim();
            String claveHash = claveEncriptada(clave);

            SQLiteDatabase db = this.dbhelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT ID FROM TBL_USUARIOS WHERE USUERNAME = ? AND PASSWORD=?", new String[] { usernameLimpio, claveHash });
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                return id > 0;
            }
            else {
                return false;
            }
        } catch(Exception ex) {
            return false;
        }
    }

    public TrUsuario BuscaUsuario(String username){
        try {
            String usernameLimpio = username.toUpperCase().trim();

            SQLiteDatabase db = this.dbhelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM TBL_USUARIOS WHERE USUERNAME=?", new String[] { usernameLimpio });
            if (cursor.moveToFirst()) {
                return
                        new TrUsuario(
                                cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getBlob(4)
                        );
            }
            else {
                return null;
            }
        } catch(Exception ex) {
            return null;
        }
    }

    public TrUsuario BuscaUsuarioPorID(String ID) throws Exception {
        try {
            SQLiteDatabase db = this.dbhelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM TBL_USUARIOS WHERE ID=?", new String[] { ID });
            if (cursor.moveToFirst()) {
                do {
                    return
                            new TrUsuario(
                                    cursor.getInt(0),
                                    cursor.getString(1),
                                    cursor.getString(2),
                                    cursor.getString(3),
                                    cursor.getBlob(4)
                            );
                } while (cursor.moveToNext());
            }
            else {
                throw new Exception("No se encontro registro.");
            }
        } catch(Exception ex) {
            throw new Exception("Error al buscar el registro - " + ex.getMessage());
        }
    }

    public int maximo(){
        SQLiteDatabase db = this.dbhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(ID) FROM TBL_USUARIOS", null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        else {
            return 0;
        }
    }

    public void GrabarUsuario(TrUsuario usuario) throws Exception {
        try {
            String usernameLimpio = usuario.getUsername().toUpperCase().trim();
            String claveHash = claveEncriptada(usuario.getClave());

            /*SQLiteDatabase db = this.dbhelper.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID", this.maximo() + 1);
            contentValues.put("NOMBRE", usuario.getNombre());
            contentValues.put("USUERNAME", usernameLimpio);
            contentValues.put("PASSWORD", claveHash);
            db.insert("TBL_USUARIOS", null, contentValues);
            db.close();*/

            SQLiteDatabase db = this.dbhelper.getWritableDatabase();
            String sql = "INSERT INTO TBL_USUARIOS (ID, NOMBRE, USUERNAME, PASSWORD, IMAGEN) VALUES(?,?,?,?,?)";
            SQLiteStatement insert = db.compileStatement(sql);

            insert.bindLong(1, this.maximo() + 1);
            insert.bindString(2, usuario.getNombre());
            insert.bindString(3, usernameLimpio);
            insert.bindString(4, claveHash);
            insert.bindBlob(5, usuario.getImagen());
            insert.executeInsert();
            db.close();
        } catch(Exception ex) {
            throw new Exception("Error al generar el registro. - " + ex.getMessage());
        }
    }

    public static boolean isNullOrBlank(String param) {
        return param == null || param.trim().length() == 0;
    }

    private static String claveEncriptada(String clave){
        return clave;
        //String hashed = org.apache.commons.codec.digest.DigestUtils.sha256Hex(clave);
        /*String hashed = Hashing.sha256()
                .hashString(clave, StandardCharsets.UTF_8)
                .toString();*/
        //return hashed;
    }
}
