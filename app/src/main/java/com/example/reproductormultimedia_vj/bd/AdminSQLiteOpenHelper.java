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
                "avatar TEXT " +
                ")";

        String sqlCancion = "CREATE TABLE cancion(" +
                "idCancion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT, " +
                "descripcion TEXT, " +
                "artista INTEGER, " +
                "fechaCreacion Date, " +
                "portada TEXT, " +
                "ruta TEXT, " +
                "listaFav TEXT" +
                ")";

        String sqlPlaylist = "CREATE TABLE playlist(" +
                "idPlaylist INTEGER PRIMARY KEY AUTOINCREMENT," +
                "idCreador INTEGER, " +
                "nombre TEXT, " +
                "portada TEXT, " +
                "privada INTEGER, " +
                "listaCanciones TEXT, " +
                "listaUsuarios TEXT" +
                ")";

        db.execSQL(sqlUsuario);
        db.execSQL(sqlCancion);
        db.execSQL(sqlPlaylist);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String ordenBorrado = "drop table if exists usuario";
        String ordenBorrado2 = "drop table if exists cancion";
        String ordenBorrado3 = "drop table if exists playlist";
        db.execSQL(ordenBorrado);
        db.execSQL(ordenBorrado2);
        db.execSQL(ordenBorrado3);
        onCreate(db);
    }
}
