package com.example.reproductormultimedia_vj.Fragments;

import static com.example.reproductormultimedia_vj.Fragments.MusicaFragment.saludos;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.reproductormultimedia_vj.Clases.Cancion;
import com.example.reproductormultimedia_vj.Clases.CrearNotificacion;
import com.example.reproductormultimedia_vj.Clases.Metodos;
import com.example.reproductormultimedia_vj.Clases.MyMediaPlayer;
import com.example.reproductormultimedia_vj.R;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link prevCancionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class prevCancionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "IDUSER";
    private static final String ARG_PARAM2 = "IDCANCION";
    private static TextView nombreCancion;
    private static TextView nombreArtista;
    private static ImageView imagen;
    private static ImageButton playPause;
    private static MediaPlayer mediaPlayer;
    private static boolean esLocal = false;
    static LottieAnimationView like;
    static Cancion cancion;

    // TODO: Rename and change types of parameters
    private int iduser;
    private int idcancion;

    public prevCancionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment prevCancionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static prevCancionFragment newInstance(int param1, int param2) {
        prevCancionFragment fragment = new prevCancionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            iduser = getArguments().getInt(ARG_PARAM1);
            idcancion = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prev_cancion, container, false);
        nombreCancion = view.findViewById(R.id.nombrePrev);
        nombreArtista = view.findViewById(R.id.artistaPrev);
        imagen = view.findViewById(R.id.portadaPrev);
        like = view.findViewById(R.id.likePrev);
        like.setAnimation(R.raw.heart_like);
        nombreCancion.setSelected(true);
        playPause = view.findViewById(R.id.playPausePrev);
        final boolean[] play = {true};


        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausePlay();
            }
        });

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {

                    if (mediaPlayer.isPlaying()) {
                        playPause.setImageResource(R.drawable.ic_baseline_pause_40);

                    } else {
                        playPause.setImageResource(R.drawable.ic_baseline_play_arrow_50);
                    }

                }
                new Handler().postDelayed(this, 100);
            }
        });



        return view;
    }

    public static void establecerMediaPlayer(MediaPlayer mediaPlayer2) {
        mediaPlayer = mediaPlayer2;
    }

    private static void pausePlay(){

        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();


            try {
                CrearNotificacion.createNotification(saludos.getContext(), cancion, R.drawable.ic_baseline_play_circle_filled_24);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else {
            mediaPlayer.start();
            try {
                CrearNotificacion.createNotification(saludos.getContext(), cancion, R.drawable.ic_baseline_pause_circle_filled_24);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static boolean obtenerSiEsLocal() {
        return esLocal;
    }

    public static void actualizarDatos(Cancion cancion2) {
        cancion = cancion2;

        if (!cancion.getRuta().startsWith("audio/")) {
            esLocal = true;
            like.setVisibility(View.GONE);
        }
        else {
            esLocal = false;
            like.setVisibility(View.VISIBLE);
        }

        nombreCancion.setText(cancion.getTitulo());
        nombreArtista.setText(cancion.getNombreArtista());

        if (!cancion.getRuta().startsWith("audio/"))
            if (!new String(cancion.getPortada(), StandardCharsets.UTF_8).equals(""))
                imagen.setImageURI(Uri.parse(new String(cancion.getPortada(), StandardCharsets.UTF_8)));
            else
                imagen.setImageResource(R.drawable.photo_1614680376573_df3480f0c6ff);
        else {
            imagen.setImageBitmap(Metodos.convertByteArrayToBitmap(cancion.getPortada()));
        }
        playPause.setImageResource(R.drawable.ic_baseline_pause_40);

    }

}