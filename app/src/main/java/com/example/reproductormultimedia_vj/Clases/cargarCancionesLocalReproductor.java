package com.example.reproductormultimedia_vj.Clases;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class cargarCancionesLocalReproductor {
    private static ArrayList<Cancion> canciones = new ArrayList<>();

    public static void cargarCancionesLocales(Context context) {
        String[] canciones_movil = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID
        };

        String seleccion = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, canciones_movil, seleccion, null, null);

        while (cursor.moveToNext()) {
            String titulo = cursor.getString(0);
            String artista = cursor.getString(1);
            String fecha = cursor.getString(2);
            String duracion = cursor.getString(3);
            String ruta = cursor.getString(4);
            String albumId = cursor.getString(5);

            //Cancion cancion = new Cancion(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), getAlbumart(Long.parseLong(cursor.getString(4)) ));
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri portadaLocal = ContentUris.withAppendedId(sArtworkUri,Long.parseLong(albumId));

            //para comprobar si tiene una imagen o no la cancion (pfd)
            try {
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(portadaLocal, "r");
                if (pfd !=null) {
                    Cancion cancion = new Cancion(0, titulo, "pito", 1, artista, fecha, duracion, portadaLocal.toString().getBytes(StandardCharsets.UTF_8), ruta);
                    if (new File(cancion.getRuta()).exists())
                        canciones.add(cancion);
                }
            } catch (FileNotFoundException e) {
                Cancion cancion = new Cancion(0, titulo, "pito", 1, artista, fecha, duracion, "".getBytes(StandardCharsets.UTF_8), ruta);
                if (new File(cancion.getRuta()).exists())
                    canciones.add(cancion);
            }

        }
    }

    public static ArrayList<Cancion> getCancionesLocales() {
        return canciones;
    }

    public static void setCancionesLocales(ArrayList<Cancion> canciones) {
        cargarCancionesLocalReproductor.canciones = canciones;
    }
}
