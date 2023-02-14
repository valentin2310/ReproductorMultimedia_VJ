package com.example.reproductormultimedia_vj;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reproductormultimedia_vj.Clases.Cancion;
import com.example.reproductormultimedia_vj.Clases.Metodos;
import com.example.reproductormultimedia_vj.Clases.MyMediaPlayer;
import com.example.reproductormultimedia_vj.Clases.CrearNotificacion;
import com.example.reproductormultimedia_vj.Clases.PlayListActual;
import com.example.reproductormultimedia_vj.Fragments.MusicaFragment;
import com.example.reproductormultimedia_vj.Fragments.MusicaLocalFragment;
import com.example.reproductormultimedia_vj.Fragments.PlaylistFragment;
import com.example.reproductormultimedia_vj.Fragments.prevCancionFragment;
import com.example.reproductormultimedia_vj.Servicios.OnClearFromRecentService;
import com.example.reproductormultimedia_vj.bd.GestionBD;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ReproductorActivity extends AppCompatActivity {
    Cancion cancion;
    ImageView imagen;
    TextView nombre, artista, tiempoActual, tiempoTotal;
    ImageButton pausePlay, previous, next, shuffle, loop, primeraCancion, ultimaCancion;
    SeekBar barra;
    boolean aleatorio;
    boolean bucle;
    boolean playlist = false;

    ArrayList<Cancion> listaCanciones;

    NotificationManager notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        GestionBD gestion = new GestionBD(this);

        if (getIntent().hasExtra("esPlayList")) {
            listaCanciones = PlaylistFragment.obtenerCanciones();
            playlist = true;
        } else {
            if (getIntent().hasExtra("esLocal")) {
                listaCanciones = MusicaLocalFragment.obtenerCanciones();
            } else {
                listaCanciones = gestion.getCanciones();
            }
        }

        TextView reproducirDesde = findViewById(R.id.reproduccionDesde);
        reproducirDesde.setText(PlayListActual.nombrePlaylist);
        imagen = findViewById(R.id.portada);
        nombre = findViewById(R.id.nombre);
        artista = findViewById(R.id.artista);
        barra = findViewById(R.id.barra);
        tiempoActual = findViewById(R.id.tiempo_actual);
        tiempoTotal = findViewById(R.id.tiempo_total);
        pausePlay = findViewById(R.id.play);
        next = findViewById(R.id.siguiente);
        previous = findViewById(R.id.anterior);
        nombre.setSelected(true);
        shuffle = findViewById(R.id.aleatorio);
        loop = findViewById(R.id.bucle);
        primeraCancion = findViewById(R.id.primeraCancion);
        ultimaCancion = findViewById(R.id.ultimaCancion);
        aleatorio = false;
        bucle = false;
        barra.setProgress(0);

        establecerDatosMusica();
        empezarMusica();

        MyMediaPlayer.getInstance().setOnCompletionListener(v -> {
            tiempoActual.setText(convertir_MMSS(MyMediaPlayer.getInstance().getDuration() + ""));
            siguienteCancion();
        });

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MyMediaPlayer.getInstance() != null) {
                    barra.setProgress(MyMediaPlayer.getInstance().getCurrentPosition());
                    tiempoActual.setText(convertir_MMSS(MyMediaPlayer.getInstance().getCurrentPosition() + ""));

                    if (MyMediaPlayer.getInstance().isPlaying()) {
                        pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);

                    } else {
                        pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                    }

                }
                new Handler().postDelayed(this, 100);
            }
        });

        barra.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (MyMediaPlayer.getInstance() != null && fromUser) {
                    MyMediaPlayer.getInstance().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pausePlay.setOnClickListener(v -> pausePlay());
        next.setOnClickListener(v -> siguienteCancion());
        previous.setOnClickListener(v -> anteriorCancion());
        shuffle.setOnClickListener(v -> cancionAleatoria());
        loop.setOnClickListener(v -> cancionBucle());
        primeraCancion.setOnClickListener(v -> primeraCancion());
        ultimaCancion.setOnClickListener(v -> ultimaCancion());

        if (PlayListActual.aleatorio)
            shuffle.setColorFilter(Color.argb(255, 0,170,255));
        else
            shuffle.clearColorFilter();

        if (PlayListActual.bucle)
            loop.setColorFilter(Color.argb(255, 0,170,255));
        else
            loop.clearColorFilter();

    }

    public void cancionAleatoria() {
        PlayListActual.aleatorio = !PlayListActual.aleatorio;
        if (PlayListActual.aleatorio)
            shuffle.setColorFilter(Color.argb(255, 0,170,255));
        else
            shuffle.clearColorFilter();
    }

    public void cancionBucle() {
        PlayListActual.bucle = !PlayListActual.bucle;
        if (PlayListActual.bucle)
            loop.setColorFilter(Color.argb(255, 0,170,255));
        else
            loop.clearColorFilter();
    }

    public void siguienteCancion() {
        PlayListActual.siguienteCancion(getApplicationContext());
        establecerDatosMusica();
        empezarMusica();
    }

    public void anteriorCancion() {
        PlayListActual.anteriorCancion(getApplicationContext());
        establecerDatosMusica();
        empezarMusica();
    }

    public void pausePlay() {
        PlayListActual.pausePlay(getApplicationContext());
    }

    public void primeraCancion() {
        PlayListActual.primeraCancion(getApplicationContext());
        establecerDatosMusica();
        empezarMusica();
    }

    public void ultimaCancion() {
        PlayListActual.ultimaCancion(getApplicationContext());
        establecerDatosMusica();
        empezarMusica();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void empezarMusica() {
        barra.setMax(Integer.parseInt(cancion.getDuracion()));
    }

    public void establecerDatosMusica () {
        try {
            cancion = PlayListActual.getCancionesActuales().get(MyMediaPlayer.currentIndex);

            nombre.setText(cancion.getTitulo());
            artista.setText(cancion.getNombreArtista());

            if (!cancion.getRuta().startsWith("audio/"))
                if (!new String(cancion.getPortada(), StandardCharsets.UTF_8).equals(""))
                    imagen.setImageURI(Uri.parse(new String(cancion.getPortada(), StandardCharsets.UTF_8)));
                else
                    imagen.setImageResource(R.drawable.photo_1614680376573_df3480f0c6ff);
            else {
                imagen.setImageBitmap(Metodos.convertByteArrayToBitmap(cancion.getPortada()));
            }

            tiempoTotal.setText(convertir_MMSS(cancion.getDuracion()));

            // cambiar color fondo a color imagen
            findViewById(R.id.reproductor_background).setBackgroundColor(Metodos.getDominantColor(((BitmapDrawable) imagen.getDrawable()).getBitmap()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Metodos.darker(Metodos.getDominantColor(((BitmapDrawable) imagen.getDrawable()).getBitmap()), (float) 0.5));
            }
        } catch (Exception e) {

        }

    }

    public String convertir_MMSS (String duracion){
        Long milisegundos = Long.parseLong(duracion);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milisegundos) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(milisegundos) % TimeUnit.MINUTES.toSeconds(1));
    }


}