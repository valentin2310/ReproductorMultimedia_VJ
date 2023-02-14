package com.example.reproductormultimedia_vj.Clases;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;

import com.example.reproductormultimedia_vj.Fragments.prevCancionFragment;
import com.example.reproductormultimedia_vj.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class PlayListActual {
    public static ArrayList<Cancion> cancionesActuales;
    public static MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    public static Cancion cancion;

    //si es musica global (primera pantalla será 0), si es una playlist 1 y por último si es musica local será 2
    public static int esPlaylist = 0;

    public static int orientacion = -1;

    public static String nombrePlaylist = "";

    public static int idPlaylist = -8;

    public static boolean aleatorio = false;

    public static boolean bucle = false;


    public static ArrayList<Cancion> getCancionesActuales() {
        return cancionesActuales;
    }

    public static void setCancionesActuales(ArrayList<Cancion> cancionesActuales) {
        PlayListActual.cancionesActuales = cancionesActuales;
    }

    public static void establecerDatosMusica(Context context) {
        cancion = PlayListActual.getCancionesActuales().get(MyMediaPlayer.currentIndex);
        Boolean esLocal = PlayListActual.getCancionesActuales().get(MyMediaPlayer.currentIndex).getRuta().startsWith("audio/")? false:true;
        prevCancionFragment.actualizarDatos(cancion);

        try {
            CrearNotificacion.createNotification(context, cancion, R.drawable.ic_baseline_pause_circle_filled_24);
        } catch (IOException e) {
            e.printStackTrace();
        }

        empezarMusica(context, esLocal);

    }

    public static void empezarMusica(Context context, boolean esLocal) {
        mediaPlayer.reset();
        try {
        if (!esLocal) {
            AssetManager assetManager = context.getResources().getAssets();
            AssetFileDescriptor afd = assetManager.openFd(cancion.getRuta());
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } else mediaPlayer.setDataSource(cancion.getRuta());


        mediaPlayer.prepare();
        mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void comprobarSiSeHaGirado(Context context) {
        if (MyMediaPlayer.currentIndex != -1 && PlayListActual.orientacion != context.getResources().getConfiguration().orientation)
            PlayListActual.establecerDatosMusicaSinReiniciar(context);

        if (PlayListActual.orientacion != context.getResources().getConfiguration().orientation)
            PlayListActual.orientacion = context.getResources().getConfiguration().orientation;
    }

    public static void anteriorCancion(Context context) {
        if (mediaPlayer.getCurrentPosition() < 3000) {
            if (MyMediaPlayer.currentIndex == 0) {
                mediaPlayer.reset();
                establecerDatosMusica(context);
            } else {
                MyMediaPlayer.currentIndex -= 1;
                mediaPlayer.reset();
                establecerDatosMusica(context);
            }
        } else {
            mediaPlayer.reset();
            establecerDatosMusica(context);
        }

        try {
            CrearNotificacion.createNotification(context, cancion, R.drawable.ic_baseline_pause_circle_filled_24);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void siguienteCancion(Context context) {
        if (bucle && !aleatorio && MyMediaPlayer.currentIndex == cancionesActuales.size() - 1)  {
            MyMediaPlayer.currentIndex = 0;
            mediaPlayer.reset();
            establecerDatosMusica(context);
        }else if (!aleatorio) {
            if (MyMediaPlayer.currentIndex == cancionesActuales.size() - 1)
                return;
            MyMediaPlayer.currentIndex += 1;
            mediaPlayer.reset();
            establecerDatosMusica(context);
        }else {
            boolean comprobar = false;
            while (!comprobar) {
                Random r = new Random();
                int index_cancion = r.nextInt(cancionesActuales.size());
                if (index_cancion != MyMediaPlayer.currentIndex) {
                    comprobar = true;
                    MyMediaPlayer.currentIndex = index_cancion;
                    establecerDatosMusica(context);
                }
            }
        }
    }

    public static void pausePlay(Context context){
        cancion = PlayListActual.getCancionesActuales().get(MyMediaPlayer.currentIndex);
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            try {
                CrearNotificacion.createNotification(context, cancion, R.drawable.ic_baseline_play_circle_filled_24);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else {
            mediaPlayer.start();
            try {
                CrearNotificacion.createNotification(context, cancion, R.drawable.ic_baseline_pause_circle_filled_24);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public static void establecerDatosMusicaSinReiniciar(Context context) {
        prevCancionFragment.actualizarDatos(cancion);

        try {
            if (mediaPlayer.isPlaying())
            CrearNotificacion.createNotification(context, cancion, R.drawable.ic_baseline_pause_circle_filled_24);
            else
                CrearNotificacion.createNotification(context, cancion, R.drawable.ic_baseline_play_circle_filled_24);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void primeraCancion(Context context) {
        MyMediaPlayer.currentIndex = 0;
        mediaPlayer.reset();
        establecerDatosMusica(context);
    }

    public static void ultimaCancion(Context context) {
        MyMediaPlayer.currentIndex = PlayListActual.getCancionesActuales().size()-1;
        mediaPlayer.reset();
        establecerDatosMusica(context);

        try {
            CrearNotificacion.createNotification(context, cancion, R.drawable.ic_baseline_pause_circle_filled_24);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
