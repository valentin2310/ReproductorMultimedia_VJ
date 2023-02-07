package com.example.reproductormultimedia_vj;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
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
import com.example.reproductormultimedia_vj.Clases.RV_Cancion;
import com.example.reproductormultimedia_vj.Fragments.MusicaLocalFragment;
import com.example.reproductormultimedia_vj.bd.GestionBD;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ReproductorActivity extends AppCompatActivity {
    Cancion cancion;
    ImageView imagen;
    TextView nombre, artista, tiempoActual, tiempoTotal;
    ImageButton pausePlay, previous, next;
    SeekBar barra;
    ArrayList<Cancion> listaCanciones;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();


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