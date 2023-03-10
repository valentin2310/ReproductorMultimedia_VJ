package com.example.reproductormultimedia_vj.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.reproductormultimedia_vj.Clases.Cancion;
import com.example.reproductormultimedia_vj.Clases.Playlist;
import com.example.reproductormultimedia_vj.Clases.Usuario;

import java.util.ArrayList;

public class GestionBD {

    private static final String NOMBREBD = "ReproductorVJ";
    private static final String TABLA_USUARIO = "usuario";
    private static final String TABLA_CANCION = "cancion";
    private static final String TABLA_PLAYLIST = "playlist";

    private static final String TABLA_USUARIO_LOGIN = "usuario_login";
    private static final String TABLA_USUARIO_CANCION_FAV = "user_song_fav";
    private static final String TABLA_USUARIO_PLAYLIST_FAV = "user_play_fav";
    private static final String TABLA_PLAYLIST_CANCION = "play_song";

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
    public int getCancionIdPorTitulo(String titulo){
        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            Cursor fila = bd.rawQuery("select * from cancion where titulo like '"+titulo+"'", null);

            if(fila.moveToFirst()) return fila.getInt(0);
            else return -1;

        }catch (Exception e){
            return -1;
        }finally {
            if(bd.isOpen()) bd.close();
        }
    }
    public Cancion getCancion(int idCancion){
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cancion c = new Cancion();
        try{
            Cursor fila = bd.rawQuery("select * from cancion where idCancion = "+idCancion, null);

            if(fila.moveToFirst()){
                c.setIdCancion(fila.getInt(0));
                c.setArtista(fila.getInt(3));
            }

            return c;

        }catch (Exception e){
            return null;
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
        // si la canci??n existe, no se puede sobreescribir
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
    public int eliminarCancion(int idCancion){
        int cant = 0;
        SQLiteDatabase bd = admin.getWritableDatabase();
        try{

            String sql = "idCancion = "+idCancion;
            cant += bd.delete(TABLA_CANCION, sql, null);
            cant += bd.delete(TABLA_PLAYLIST_CANCION, sql, null);
            cant += bd.delete(TABLA_USUARIO_CANCION_FAV, sql, null);

        }catch (Exception e){
            return 0;
        }finally {
            if(bd.isOpen()) bd.close();
            return cant;
        }
    }

    public boolean cambiarAvatar(int idUser, byte[] nuevaImg){
        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            ContentValues registro = new ContentValues();

            registro.put("avatar", nuevaImg);

            bd.update(TABLA_USUARIO, registro, "idUser = "+idUser, null);
            bd.close();
            return true;
        }catch (Exception e){
            return false;
        }finally {
            if(bd.isOpen()) bd.close();
        }
    }
    public boolean cambiarUsername(int idUser, String newUsername){
        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            ContentValues registro = new ContentValues();

            registro.put("username", newUsername);

            bd.update(TABLA_USUARIO, registro, "idUser = "+idUser, null);
            bd.close();
            return true;
        }catch (Exception e){
            return false;
        }finally {
            if(bd.isOpen()) bd.close();
        }
    }

    public Cancion getCancionSimple(int idCancion){
        Cancion c = null;
        SQLiteDatabase bd = admin.getWritableDatabase();

        try{

            String sql = "select * from "+TABLA_CANCION+" where idCancion = "+idCancion;

            Cursor fila = bd.rawQuery(sql, null);

            if(fila.moveToFirst()){
                int id = fila.getInt(0);
                String titulo = fila.getString(1);
                byte[] portada = fila.getBlob(7);

                c = new Cancion(id, titulo, null, -1, null, null, null, portada, null);
            }

        }catch (Exception e){
            return null;
        }finally {
            if(bd.isOpen()) bd.close();
        }
        return c;
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
    public ArrayList<Cancion> getCancionesUser(int idUser){
        ArrayList<Cancion> canciones = new ArrayList<>();
        SQLiteDatabase bd = admin.getWritableDatabase();

        try {

            String sql = "select * from cancion where artista = "+idUser;

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
    public ArrayList<Cancion> getCanciones(ArrayList<Integer> indices){
        ArrayList<Cancion> canciones = new ArrayList<>();
        SQLiteDatabase bd = admin.getWritableDatabase();

        try {

            String busq = "";
            for(int i = 0; i<indices.size(); i++){
                busq += indices.get(i);
                if(i < indices.size()-1){
                    busq += ",";
                }
            }
            String sql = "select * from cancion where idCancion in ("+busq+")";

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

    public ArrayList<Playlist> getAllPlaylist(){
        ArrayList<Playlist> lista = new ArrayList<>();
        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor fila = bd.rawQuery("SELECT * FROM "+TABLA_PLAYLIST+" where privada = 0", null);

        while(fila.moveToNext()){
            int idPlay = fila.getInt(0);
            int idCreador = fila.getInt(1);
            String nombre = fila.getString(2);
            byte[] portada = fila.getBlob(3);
            int privada  = fila.getInt(4);

            Playlist play = new Playlist(idPlay, idCreador, nombre, portada, privada == 1, null, null);
            lista.add(play);
        }

        return  lista;
    }
    public ArrayList<Playlist> getPlaylist(int idUser){
        ArrayList<Playlist> lista = new ArrayList<>();
        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor fila = bd.rawQuery("SELECT * FROM "+TABLA_PLAYLIST+" where idCreador = "+idUser, null);

        while(fila.moveToNext()){
            int idPlay = fila.getInt(0);
            int idCreador = fila.getInt(1);
            String nombre = fila.getString(2);
            byte[] portada = fila.getBlob(3);
            int privada  = fila.getInt(4);

            Playlist play = new Playlist(idPlay, idCreador, nombre, portada, privada == 1, null, null);
            lista.add(play);
        }

        return  lista;
    }
    public ArrayList<Playlist> getFavPlaylist(int idUser){
        ArrayList<Playlist> lista = new ArrayList<>();
        SQLiteDatabase bd = admin.getWritableDatabase();

        String sql = "SELECT DISTINCT p.* FROM "+TABLA_PLAYLIST+" p JOIN "+TABLA_USUARIO_PLAYLIST_FAV+" uf " +
                "ON p.idPlaylist = uf.idPlaylist\n" +
                "WHERE uf.idUser = "+idUser;

        Cursor fila = bd.rawQuery(sql, null);

        while(fila.moveToNext()){
            int idPlay = fila.getInt(0);
            int idCreador = fila.getInt(1);
            String nombre = fila.getString(2);
            byte[] portada = fila.getBlob(3);
            int privada  = fila.getInt(4);

            Playlist play = new Playlist(idPlay, idCreador, nombre, portada, privada == 1, null, null);
            lista.add(play);
        }

        return  lista;
    }
    public int obtenerUltimoPlaylist(){
        SQLiteDatabase bd = admin.getWritableDatabase();
        int id = -1;
        try{
            Cursor fila = bd.rawQuery("select max(idPlaylist) from playlist", null);
            if(fila.moveToFirst()){
                id = fila.getInt(0);
            }

        }catch (Exception e){
            // no hacer nada
        }finally {
            if(bd.isOpen()) bd.close();
            return id;
        }
    }
    public boolean crearPlaylist(Playlist play){

        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            ContentValues registro = new ContentValues();

            registro.put("idCreador", play.getIdCreador());
            registro.put("nombre", play.getNombre());
            registro.put("portada", play.getImgPortada());
            registro.put("privada", 0);

            bd.insert(TABLA_PLAYLIST, null, registro);
            bd.close();
            return true;
        }catch (Exception e){
            return false;
        }finally {
            if(bd.isOpen()) bd.close();
        }
    }
    public boolean updatePlaylist(Playlist play){

        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            ContentValues registro = new ContentValues();

            registro.put("nombre", play.getNombre());
            registro.put("portada", play.getImgPortada());

            bd.update(TABLA_PLAYLIST, registro, "idPlaylist = "+play.getIdPlaylist(), null);
            bd.close();
            return true;
        }catch (Exception e){
            return false;
        }finally {
            if(bd.isOpen()) bd.close();
        }
    }
    public Playlist getPlaylistId(int idPlay){
        Playlist play = null;
        SQLiteDatabase bd = admin.getWritableDatabase();

        try{

            String sql = "select * from "+TABLA_PLAYLIST+" where idPlaylist = "+idPlay;

            Cursor fila = bd.rawQuery(sql, null);

            if(fila.moveToFirst()){
                int id = fila.getInt(0);
                int creador = fila.getInt(1);
                String nombre = fila.getString(2);
                byte[] portada = fila.getBlob(3);
                int privada = fila.getInt(4);

                play = new Playlist(id, creador, nombre, portada, privada == 1, null, null);
            }

        }catch (Exception e){
            return null;
        }finally {
            if(bd.isOpen()) bd.close();
        }
        return play;
    }
    public boolean setCancionesPlaylist(int idPlaylist, ArrayList<Integer> listaCanciones){
        SQLiteDatabase bd = admin.getWritableDatabase();
        try{

            bd.delete(TABLA_PLAYLIST_CANCION, "idPlaylist = "+idPlaylist, null);

            for(Integer i : listaCanciones){
                ContentValues registro = new ContentValues();

                registro.put("idPlaylist", idPlaylist);
                registro.put("idCancion", i);

                bd.insert(TABLA_PLAYLIST_CANCION, null, registro);
            }
            bd.close();
            return true;
        }catch (Exception e){
            return false;
        }finally {
            if(bd.isOpen()) bd.close();
        }
    }
    public ArrayList<Cancion> getPlaylistCanciones(int idPlaylist){
        ArrayList<Cancion> lista = new ArrayList<>();
        try{
            SQLiteDatabase bd = admin.getWritableDatabase();

            Cursor fila = bd.rawQuery("SELECT * FROM "+TABLA_PLAYLIST_CANCION+" where idPlaylist = "+idPlaylist, null);

            while(fila.moveToNext()){
                int idCancion = fila.getInt(1);

                Cursor fila2 = bd.rawQuery("SELECT * FROM "+TABLA_CANCION+" WHERE idCancion = "+idCancion, null);

                while (fila2.moveToNext()){
                    String titulo = fila2.getString(1);
                    String descripcion = fila2.getString(2);
                    int artista = fila2.getInt(3);
                    String nombreArtista = fila2.getString(4);
                    String fechaCreacion = fila2.getString(5);
                    String duracion = fila2.getString(6);
                    byte[] portada = fila2.getBlob(7);
                    String ruta = fila2.getString(8);


                    Cancion c = new Cancion(idCancion, titulo, descripcion, artista, nombreArtista, fechaCreacion, duracion, portada, ruta);
                    lista.add(c);
                }

            }

        }catch (Exception e){
            String hola = e.toString();
        }

        return  lista;
    }
    public ArrayList<Integer> getPlaylistCancionesIds(int idPlaylist){
        ArrayList<Integer> lista = new ArrayList<>();
        try{
            SQLiteDatabase bd = admin.getWritableDatabase();

            Cursor fila = bd.rawQuery("SELECT * FROM "+TABLA_PLAYLIST_CANCION+" where idPlaylist = "+idPlaylist, null);

            while(fila.moveToNext()){
                lista.add(fila.getInt(1));
            }

        }catch (Exception e){

        }

        return  lista;
    }
    public int eliminarPlaylist(int idPlaylist){
        SQLiteDatabase db = admin.getWritableDatabase();
        int cant = db.delete(TABLA_PLAYLIST, "idPlaylist = "+idPlaylist, null);
        cant += db.delete(TABLA_USUARIO_PLAYLIST_FAV, "idPlaylist = "+idPlaylist, null);
        cant += db.delete(TABLA_PLAYLIST_CANCION, "idPlaylist = "+ idPlaylist, null);

        db.close();

        return cant;
    }
    public boolean setCancionPlaylist(int idPlaylist, int idCancion){
        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            ContentValues registro = new ContentValues();

            registro.put("idPlaylist", idPlaylist);
            registro.put("idCancion", idCancion);

            bd.insert(TABLA_PLAYLIST_CANCION, null, registro);
            bd.close();
            return true;
        }catch (Exception e){
            return false;
        }finally {
            if(bd.isOpen()) bd.close();
        }
    }
    public int eliminarCancionPlaylist(int idPlaylist, int idCancion){
        SQLiteDatabase db = admin.getWritableDatabase();
        int cant = db.delete(TABLA_PLAYLIST_CANCION, "idPlaylist = "+idPlaylist+" and idCancion = "+idCancion, null);
        db.close();

        return cant;
    }

    // los likes que tiene una cancion
    public int getPlaylistFav(int idPlaylist){
        int n = 0;
        SQLiteDatabase db = admin.getWritableDatabase();
        try{
            Cursor fila = db.rawQuery("SELECT COUNT(idUser) FROM "+TABLA_USUARIO_PLAYLIST_FAV+" WHERE idPlaylist = "+idPlaylist, null);

            fila.moveToFirst();
            n = fila.getInt(0);

        }catch (Exception e){
            e.toString();
        }finally {
            db.close();
        }
        return n;
    }
    public boolean setPlaylistFav(int idUser, int idPlaylist){
        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            ContentValues registro = new ContentValues();

            registro.put("idUser", idUser);
            registro.put("idPlaylist", idPlaylist);

            bd.insert(TABLA_USUARIO_PLAYLIST_FAV, null, registro);
            bd.close();
            return true;
        }catch (Exception e){
            return false;
        }finally {
            if(bd.isOpen()) bd.close();
        }
    }
    public int eliminarPlaylistFav(int idUser, int idPlaylist){
        SQLiteDatabase db = admin.getWritableDatabase();
        int cant = db.delete(TABLA_USUARIO_PLAYLIST_FAV, "idUser = "+idUser+" and idPlaylist = "+idPlaylist, null);
        db.close();

        return cant;
    }

    public ArrayList<Integer> getCancionesFav(int idUser){
        ArrayList<Integer> lista = new ArrayList<>();
        try{
            SQLiteDatabase bd = admin.getWritableDatabase();

            Cursor fila = bd.rawQuery("SELECT DISTINCT * FROM "+TABLA_USUARIO_CANCION_FAV+" where idUser = "+idUser, null);

            while(fila.moveToNext()){
                    lista.add(fila.getInt(1));
            }

        }catch (Exception e){
            String hola = e.toString();
        }

        return  lista;
    }
    public boolean setCancionFav(int idUser, int idCancion){
        SQLiteDatabase bd = null;
        try{
            bd = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();

            registro.put("idUser", idUser);
            registro.put("idCancion", idCancion);

            bd.insert(TABLA_USUARIO_CANCION_FAV, null, registro);
            bd.close();
            return true;
        }catch (Exception e){
            return false;
        }finally {
            if(bd.isOpen()) bd.close();
        }
    }
    public int eliminarCancionFav(int idUser, int idCancion){
        int cant = 0;

        try{
            SQLiteDatabase db = admin.getWritableDatabase();
            cant = db.delete(TABLA_USUARIO_CANCION_FAV, "idUser = "+idUser+" and idCancion = "+idCancion, null);
            db.close();
        }catch (Exception e){
            e.toString();
        }

        return cant;
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

    public boolean setUsuarioDefault(int idUser){
        if(!usuarioExisteId(idUser)) return false;

        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            ContentValues registro = new ContentValues();

            registro.put("idUser", idUser);
            registro.put("valido", 1);

            Cursor fila = bd.rawQuery("select * from "+TABLA_USUARIO_LOGIN, null);

            if(fila.moveToFirst()){
                bd.update(TABLA_USUARIO_LOGIN, registro, null, null);
            }else{
                bd.insert(TABLA_USUARIO_LOGIN, null, registro);
            }
            bd.close();
            return true;
        }catch (Exception e){
            return false;
        }finally {
            if(bd.isOpen()) bd.close();
        }
    }
    public int getUsuarioDefaultId(){
        int idUser = -1;
        SQLiteDatabase bd = admin.getWritableDatabase();

        try{

            String sql = "select * from "+TABLA_USUARIO_LOGIN;

            Cursor fila = bd.rawQuery(sql, null);

            if(fila.moveToFirst()){
                idUser = fila.getInt(0);
                if(fila.getInt(1) == 0) return -1;
            }

        }catch (Exception e){
            // no hacer nada
        }finally {
            if(bd.isOpen()) bd.close();
        }
        return idUser;
    }
}
