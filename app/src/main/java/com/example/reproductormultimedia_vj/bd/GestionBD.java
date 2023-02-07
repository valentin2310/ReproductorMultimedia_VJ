package com.example.reproductormultimedia_vj.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.reproductormultimedia_vj.Clases.Cancion;
import com.example.reproductormultimedia_vj.Clases.Usuario;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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

    public boolean cancionExiste(String titulo2, String artista){
        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            Cursor fila = bd.rawQuery("select * from cancion where titulo like '"+titulo2+"' and nombreArtista like '" + artista + "'", null);

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


    public boolean agregarCancion(Cancion cancion){
        // si la canción existe, no se puede sobreescribir
        if(cancionExiste(cancion.getTitulo(), cancion.getNombreArtista())) return false;

        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            ContentValues registro = new ContentValues();

            registro.put("titulo", cancion.getTitulo());
            registro.put("descripcion", cancion.getDescripcion());
            registro.put("artista", cancion.getArtista());
            registro.put("nombreArtista", cancion.getNombreArtista());
            registro.put("fechaCreacion", cancion.getFechaCreacion());
            registro.put("duracion", cancion.getDuracion());
            registro.put("portada", cancion.getPortada());
            registro.put("ruta", cancion.getRuta());

            bd.insert(TABLA_CANCION, null, registro);
            bd.close();
            return true;
        }catch (Exception e){
            return false;
        }finally {
            if(bd.isOpen()) bd.close();
        }
    }

    public ArrayList<Cancion> getCanciones(){
        ArrayList<Cancion> canciones = new ArrayList<>();
        SQLiteDatabase bd = admin.getWritableDatabase();

        try {

            String sql = "select * from cancion";

            Cursor fila = bd.rawQuery(sql, null);

            while (fila.moveToNext()) {
                int idCancion = fila.getInt(0);
                String titulo = fila.getString(1);
                String descripcion = fila.getString(2);
                int artista = fila.getInt(3);
                String nombreArtista = fila.getString(4);
                String fechaCreacion = fila.getString(5);
                String duracion = fila.getString(6);
                byte[] portada = fila.getBlob(7);
                String ruta = fila.getString(8);

                canciones.add(new Cancion(idCancion, titulo, descripcion, artista, nombreArtista, fechaCreacion, duracion, portada, ruta));

            }
        }catch (Exception e){
            return null;
        }finally {
            if(bd.isOpen()) bd.close();
        }
        return canciones;
    }

   /* public ArrayList<Cancion> getCancionesFiltradas(ArrayList<Integer> id_cancionesFiltradas){
        ArrayList<Cancion> canciones = new ArrayList<>();
        SQLiteDatabase bd = admin.getWritableDatabase();

        for (Integer id_cancion : id_cancionesFiltradas)
        try {

            String sql = "select * from cancion where id like " + id_cancion;

            Cursor fila = bd.rawQuery(sql, null);

            while (fila.moveToNext()) {
                int idCancion = fila.getInt(0);
                String titulo = fila.getString(1);
                String descripcion = fila.getString(2);
                int artista = fila.getInt(3);
                String nombreArtista = fila.getString(4);
                String fechaCreacion = fila.getString(5);
                String duracion = fila.getString(6);
                byte[] portada = fila.getBlob(7);
                String ruta = fila.getString(8);

                canciones.add(new Cancion(idCancion, titulo, descripcion, artista, nombreArtista, fechaCreacion, duracion, portada, ruta));

            }
        }catch (Exception e){
            return null;
        }finally {
            if(bd.isOpen()) bd.close();
        }
        return canciones;
    }*/

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
