package com.example.reproductormultimedia_vj;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
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
import com.example.reproductormultimedia_vj.Fragments.MusicaLocalFragment;
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
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();

    NotificationManager notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);
        GestionBD gestion = new GestionBD(this);

        if (getIntent().hasExtra("esLocal")) {
            listaCanciones = MusicaLocalFragment.obtenerCanciones();
        } else {
            listaCanciones = gestion.getCanciones();
        }


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
        CrearNotificacion.createNotification(this, cancion, R.drawable.ic_baseline_pause_circle_filled_24, MyMediaPlayer.currentIndex, listaCanciones.size()-1);
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
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");
            switch (action) {
                case CrearNotificacion.ACTION_PREVIUOS:
                    anteriorCancion();
                    break;
                case CrearNotificacion.ACTION_PLAY:
                    pausePlay();
                    break;
                case CrearNotificacion.ACTION_NEXT:
                    siguienteCancion();
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
        restartActivity(this);
        CrearNotificacion.createNotification(this, cancion, R.drawable.ic_baseline_pause_circle_filled_24, MyMediaPlayer.currentIndex, listaCanciones.size()-1);
    }

    public void ultimaCancion() {
        MyMediaPlayer.currentIndex = listaCanciones.size()-1;
        mediaPlayer.reset();
        establecerDatosMusica();
        restartActivity(this);
        CrearNotificacion.createNotification(this, cancion, R.drawable.ic_baseline_pause_circle_filled_24, MyMediaPlayer.currentIndex, listaCanciones.size()-1);
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
        mediaPlayer.reset();
        try {

            if (!cancion.getRuta().startsWith("audio/"))
                mediaPlayer.setDataSource(cancion.getRuta());
            else {
                //Toast.makeText(artista.getContext(), cancion.getNombre(), Toast.LENGTH_LONG).show();
                AssetManager assetManager = getResources().getAssets();
                AssetFileDescriptor afd = assetManager.openFd(cancion.getRuta());
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            }

            mediaPlayer.prepare();
            mediaPlayer.start();
            barra.setProgress(0);
            barra.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void establecerDatosMusica () {
        cancion = listaCanciones.get(MyMediaPlayer.currentIndex);

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

        empezarMusica();

    }

    public static void restartActivity(Activity activity){
        if (!Build.VERSION.RELEASE.equals("13"))
        if (Build.VERSION.SDK_INT >= 11) {
            activity.recreate();
        } else {
            activity.finish();
            activity.startActivity(activity.getIntent());
        }
    }

    private void pausePlay(){

        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            CrearNotificacion.createNotification(this, cancion, R.drawable.ic_baseline_play_circle_filled_24, MyMediaPlayer.currentIndex, listaCanciones.size()-1);

        }
        else {
            mediaPlayer.start();
            CrearNotificacion.createNotification(this, cancion, R.drawable.ic_baseline_pause_circle_filled_24, MyMediaPlayer.currentIndex, listaCanciones.size()-1);

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
        }else if (!aleatorio) {
            if (MyMediaPlayer.currentIndex == listaCanciones.size() - 1)
                return;
            MyMediaPlayer.currentIndex += 1;
            mediaPlayer.reset();
            establecerDatosMusica();
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
                }
            }
        }
        restartActivity(this);
        CrearNotificacion.createNotification(this, cancion, R.drawable.ic_baseline_pause_circle_filled_24, MyMediaPlayer.currentIndex, listaCanciones.size()-1);

    }

    public void anteriorCancion() {

        if (mediaPlayer.getCurrentPosition() < 3000) {
            if (MyMediaPlayer.currentIndex == 0) {
                mediaPlayer.reset();
                establecerDatosMusica();
            } else {
                MyMediaPlayer.currentIndex -= 1;
                mediaPlayer.reset();
                establecerDatosMusica();
            }
        } else {
            mediaPlayer.reset();
            establecerDatosMusica();
        }
        restartActivity(this);
        CrearNotificacion.createNotification(this, cancion, R.drawable.ic_baseline_pause_circle_filled_24, MyMediaPlayer.currentIndex, listaCanciones.size()-1);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
    }
}