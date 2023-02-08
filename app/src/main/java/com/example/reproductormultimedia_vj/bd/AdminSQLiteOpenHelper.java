package com.example.reproductormultimedia_vj.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // crear tablas sqlite

        // tabla usuario
        String sqlUsuario = "CREATE TABLE usuario (" +
                "idUser INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "username TEXT UNIQUE, " +
                "password TEXT, " +
                "sexo INTEGER, " +
                "fechaNac TEXT, " +
                "avatar BLOB " +
                ")";

        String sqlCancion = "CREATE TABLE cancion(" +
                "idCancion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT, " +
                "descripcion TEXT, " +
                "artista INTEGER, " +
                "nombreArtista TEXT, " +
                "fechaCreacion TEXT, " +
                "duracion TEXT, " +
                "portada TEXT, " +
                "ruta TEXT " +
                ")";

        String sqlPlaylist = "CREATE TABLE playlist(" +
                "idPlaylist INTEGER PRIMARY KEY AUTOINCREMENT," +
                "idCreador INTEGER, " +
                "nombre TEXT, " +
                "portada BLOB, " +
                "privada INTEGER " +
                ")";

        String sqlUsuarioCancionFav = "CREATE TABLE user_song_fav(" +
                "idUser INTEGER," +
                "idCancion INTEGER" +
                ")";

        String sqlUsuarioPlaylistFav = "CREATE TABLE user_play_fav(" +
                "idUser INTEGER," +
                "idPlaylist INTEGER" +
                ")";

        String sqlCancionPlaylist = "CREATE TABLE play_song(" +
                "idPlaylist INTEGER," +
                "idCancion INTEGER" +
                ")";

        String sqlLogIn = "CREATE TABLE usuario_login(" +
                "idUser INTEGER," +
                "activo INTEGER" +
                ")";

        db.execSQL(sqlUsuario);
        db.execSQL(sqlCancion);
        db.execSQL(sqlPlaylist);
        db.execSQL(sqlCancionPlaylist);
        db.execSQL(sqlUsuarioCancionFav);
        db.execSQL(sqlUsuarioPlaylistFav);
        //db.execSQL(sqlLogIn);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String ordenBorrado = "drop table if exists usuario";
        String ordenBorrado2 = "drop table if exists cancion";
        String ordenBorrado3 = "drop table if exists playlist";
        String ordenBorrado4 = "drop table if exists usuario_login";
        String ordenBorrado5 = "drop table if exists play_song";
        String ordenBorrado6 = "drop table if exists user_song_fav";
        String ordenBorrado7 = "drop table if exists user_play_fav";
        db.execSQL(ordenBorrado);
        db.execSQL(ordenBorrado2);
        db.execSQL(ordenBorrado3);
        db.execSQL(ordenBorrado4);
        db.execSQL(ordenBorrado5);
        db.execSQL(ordenBorrado6);
        db.execSQL(ordenBorrado7);
        onCreate(db);
    }
}
