package com.example.reproductormultimedia_vj;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ReproductorActivity extends AppCompatActivity {
    RV_Cancion cancion;
    ImageView imagen;
    TextView nombre, artista, tiempoActual, tiempoTotal;
    ImageButton pausePlay, previous, next;
    SeekBar barra;
    ArrayList<RV_Cancion> listaCanciones;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);
        listaCanciones = (ArrayList<RV_Cancion>) getIntent().getSerializableExtra("listaCanciones");

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


        establecerDatosMusica();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    barra.setProgress(mediaPlayer.getCurrentPosition());
                    tiempoActual.setText(convertir_MMSS(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying()){
                        pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);

                    }else{
                        pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);

                    }

                }
                new Handler().postDelayed(this,100);
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



    }

    public void empezarMusica() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(cancion.getPath());
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

            nombre.setText(cancion.getNombre());
            artista.setText(cancion.getArtista());

            if (!cancion.getImage_path().equals(""))
                imagen.setImageURI(Uri.parse(cancion.getImage_path()));
            else
                imagen.setImageResource(R.drawable.photo_1614680376573_df3480f0c6ff);

            tiempoTotal.setText(convertir_MMSS(cancion.getDuracion()));

            empezarMusica();



        }

        private void pausePlay(){
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
        }

        public String convertir_MMSS (String duracion){
        Long milisegundos = Long.parseLong(duracion);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milisegundos) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(milisegundos) % TimeUnit.MINUTES.toSeconds(1));
         }

         public void siguienteCancion() {
             if(MyMediaPlayer.currentIndex== listaCanciones.size()-1)
                 return;
             MyMediaPlayer.currentIndex +=1;
             mediaPlayer.reset();
             establecerDatosMusica();
         }

         public void anteriorCancion() {
             if(MyMediaPlayer.currentIndex== 0)
                 return;
             MyMediaPlayer.currentIndex -=1;
             mediaPlayer.reset();
             establecerDatosMusica();
         }

}