package com.example.reproductormultimedia_vj.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.reproductormultimedia_vj.Clases.Usuario;

public class GestionBD {

    private static final String NOMBREBD = "ReproductorVJ";
    private static final String TABLA_USUARIO = "usuario";
    private static final String TABLA_CANCION = "cancion";
    private static final String TABLA_PLAYLIST = "playlist";
    private static int version = 1;
    private AdminSQLiteOpenHelper admin;

    public GestionBD(Context context) {
        this.admin = new AdminSQLiteOpenHelper(context, NOMBREBD, null, version);
    }

    public boolean usuarioExisteId(int id){
        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            Cursor fila = bd.rawQuery("select * from usuario where idUser = "+id, null);

            if(fila.moveToFirst()) return true;
            else return false;

        }catch (Exception e){
            return false;
        }finally {
            if(bd.isOpen()) bd.close();
        }
    }
    public boolean usuarioExiste(String usuario){
        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            Cursor fila = bd.rawQuery("select * from usuario where username like '"+usuario+"'", null);

            if(fila.moveToFirst()) return true;
            else return false;

        }catch (Exception e){
            return false;
        }finally {
            if(bd.isOpen()) bd.close();
        }
    }
    public boolean crearUsuario(Usuario user){
        // si el usuario existe, no se puede sobreescribir
        if(usuarioExisteId(user.getIdUser()) || usuarioExiste(user.getUsername())) return false;

        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            ContentValues registro = new ContentValues();

            registro.put("nombre", user.getNombre());
            registro.put("username", user.getUsername());
            registro.put("password", user.getPassword());
            registro.put("sexo", user.getSexo());
            registro.put("fechaNac", user.getFechaNac());
            registro.put("avatar", user.getImgAvatar());

            bd.insert(TABLA_USUARIO, null, registro);
            bd.close();
            return true;
        }catch (Exception e){
            return false;
        }finally {
            if(bd.isOpen()) bd.close();
        }
    }
    public Usuario getUsuario(int idUser){
        Usuario user = null;
        SQLiteDatabase bd = admin.getWritableDatabase();

        try{

            String sql = "select * from usuario where idUser = "+idUser;

            Cursor fila = bd.rawQuery(sql, null);

            if(fila.moveToFirst()){
                int id = fila.getInt(0);
                String nombre = fila.getString(1);
                String username = fila.getString(2);
                String password = fila.getString(3);
                int sexo = fila.getInt(4);
                String fecha = fila.getString(5);
                byte[] avatar = fila.getBlob(6);

                user = new Usuario(id, nombre, username, password, sexo, fecha, avatar, null, null);
            }

        }catch (Exception e){
            return null;
        }finally {
            if(bd.isOpen()) bd.close();
        }
        return user;
    }
    public int getUsuarioId(String usuario){
        int idUser = -1;
        SQLiteDatabase bd = admin.getWritableDatabase();

        try{

            String sql = "select * from usuario where username like '"+usuario+"'";

            Cursor fila = bd.rawQuery(sql, null);

            if(fila.moveToFirst()){
                idUser = fila.getInt(0);
            }

        }catch (Exception e){
            // no hacer nada
        }finally {
            if(bd.isOpen()) bd.close();
        }
        return idUser;
    }
    public Usuario comprobarUsuario(String usuario, String passwd){
        Usuario user = null;
        SQLiteDatabase bd = admin.getWritableDatabase();

        try{

            String sql = "select * from usuario where username like '"+usuario+"' and password like '"+passwd+"'";

            Cursor fila = bd.rawQuery(sql, null);

            if(fila.moveToFirst()){
                int id = fila.getInt(0);
                String nombre = fila.getString(1);
                String username = fila.getString(2);
                String password = fila.getString(3);
                int sexo = fila.getInt(4);
                String fecha = fila.getString(5);
                byte[] avatar = fila.getBlob(6);

                user = new Usuario(id, nombre, username, password, sexo, fecha, avatar, null, null);
            }

        }catch (Exception e){
            return null;
        }finally {
            if(bd.isOpen()) bd.close();
        }
        return user;
    }

}
