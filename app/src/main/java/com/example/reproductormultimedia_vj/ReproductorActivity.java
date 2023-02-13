package com.example.reproductormultimedia_vj;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.reproductormultimedia_vj.Fragments.MusicaFragment;
import com.example.reproductormultimedia_vj.Fragments.MusicaLocalFragment;
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

    ArrayList<Cancion> listaCanciones;
    MediaPlayer mediaPlayer;

    NotificationManager notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        GestionBD gestion = new GestionBD(this);

        if (getIntent().hasExtra("esLocal")) {
            listaCanciones = MusicaLocalFragment.obtenerCanciones();
        } else {
            listaCanciones = gestion.getCanciones();
        }

        mediaPlayer = MusicaFragment.obtenerMediaPlayer();


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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }


        establecerDatosMusica();
        try {
            CrearNotificacion.createNotification(this, cancion, R.drawable.ic_baseline_pause_circle_filled_24);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(v -> {
            tiempoActual.setText(convertir_MMSS(mediaPlayer.getDuration() + ""));
            siguienteCancion();
        });

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    barra.setProgress(mediaPlayer.getCurrentPosition());
                    tiempoActual.setText(convertir_MMSS(mediaPlayer.getCurrentPosition() + ""));

                    if (mediaPlayer.isPlaying()) {
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
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
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


    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)  {
            String action = intent.getExtras().getString("actionname");
            switch (action) {
                case CrearNotificacion.ACTION_PREVIUOS:
                    establecerDatosMusica();
                    break;
                case CrearNotificacion.ACTION_PLAY:
                    establecerDatosMusica();
                    break;
                case CrearNotificacion.ACTION_NEXT:
                    establecerDatosMusica();
                    break;
            }
        }
    };

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CrearNotificacion.CHANNEL_ID,
                    "Javi Valentin", NotificationManager.IMPORTANCE_LOW);
            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void primeraCancion() {
        MyMediaPlayer.currentIndex = 0;
        mediaPlayer.reset();
        establecerDatosMusica();
        reproducirMusica();
        restartActivity(this);
        try {
            CrearNotificacion.createNotification(this, cancion, R.drawable.ic_baseline_pause_circle_filled_24);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ultimaCancion() {
        MyMediaPlayer.currentIndex = listaCanciones.size()-1;
        mediaPlayer.reset();
        establecerDatosMusica();
        reproducirMusica();
        restartActivity(this);
        try {
            CrearNotificacion.createNotification(this, cancion, R.drawable.ic_baseline_pause_circle_filled_24);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancionAleatoria() {
        aleatorio = !aleatorio;
        if (aleatorio)
        shuffle.setColorFilter(Color.argb(255, 0,170,255));
        else
            shuffle.clearColorFilter();
    }

    public void cancionBucle() {
        bucle = !bucle;
        if (bucle)
            loop.setColorFilter(Color.argb(255, 0,170,255));
        else
            loop.clearColorFilter();
    }



    public void empezarMusica() {
        barra.setProgress(0);
        barra.setMax(Integer.parseInt(cancion.getDuracion()));
    }

    public void reproducirMusica() {


            mediaPlayer.reset();
        if (!cancion.getRuta().startsWith("audio/")) {
            try {
                mediaPlayer.setDataSource(cancion.getRuta());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                AssetManager assetManager = getResources().getAssets();
                AssetFileDescriptor afd = assetManager.openFd(cancion.getRuta());
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MusicaFragment.setCancion(cancion);
    }


    public void establecerDatosMusica () {
        cancion = listaCanciones.get(MyMediaPlayer.currentIndex);

        prevCancionFragment.actualizarDatos(cancion);

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
        findViewById(R.id.reproductor_background).setBackgroundColor(Metodos.getDominantColor(((BitmapDrawable)imagen.getDrawable()).getBitmap()));

        empezarMusica();

    }

    public static void restartActivity(Activity activity){

    }

    private void pausePlay(){

        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            try {
                CrearNotificacion.createNotification(this, cancion, R.drawable.ic_baseline_play_circle_filled_24);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else {
            mediaPlayer.start();
            try {
                CrearNotificacion.createNotification(this, cancion, R.drawable.ic_baseline_pause_circle_filled_24);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public String convertir_MMSS (String duracion){
        Long milisegundos = Long.parseLong(duracion);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milisegundos) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(milisegundos) % TimeUnit.MINUTES.toSeconds(1));
    }

    public void siguienteCancion() {

        if (bucle && !aleatorio && MyMediaPlayer.currentIndex == listaCanciones.size() - 1)  {
            MyMediaPlayer.currentIndex = 0;
            mediaPlayer.reset();
            establecerDatosMusica();
            reproducirMusica();
        }else if (!aleatorio) {
            if (MyMediaPlayer.currentIndex == listaCanciones.size() - 1)
                return;
            MyMediaPlayer.currentIndex += 1;
            mediaPlayer.reset();
            establecerDatosMusica();
            reproducirMusica();
        }else {
            boolean comprobar = false;
            while (!comprobar) {
                Random r = new Random();
                int index_cancion = r.nextInt(listaCanciones.size());
                if (index_cancion != MyMediaPlayer.currentIndex) {
                    comprobar = true;
                    MyMediaPlayer.currentIndex = index_cancion;
                    mediaPlayer.reset();
                    establecerDatosMusica();
                    reproducirMusica();
                }
            }
        }
        restartActivity(this);
        try {
            CrearNotificacion.createNotification(this, cancion, R.drawable.ic_baseline_pause_circle_filled_24);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void anteriorCancion() {

        if (mediaPlayer.getCurrentPosition() < 3000) {
            if (MyMediaPlayer.currentIndex == 0) {
                mediaPlayer.reset();
                establecerDatosMusica();
                reproducirMusica();

            } else {
                MyMediaPlayer.currentIndex -= 1;
                mediaPlayer.reset();
                establecerDatosMusica();
                reproducirMusica();

            }
        } else {
            mediaPlayer.reset();
            establecerDatosMusica();
            reproducirMusica();

        }
        restartActivity(this);
        try {
            CrearNotificacion.createNotification(this, cancion, R.drawable.ic_baseline_pause_circle_filled_24);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}