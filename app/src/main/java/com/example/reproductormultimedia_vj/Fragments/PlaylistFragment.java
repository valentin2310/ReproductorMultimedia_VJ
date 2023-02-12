package com.example.reproductormultimedia_vj.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.reproductormultimedia_vj.Adapter.AdapterCancionLocal;
import com.example.reproductormultimedia_vj.Clases.Cancion;
import com.example.reproductormultimedia_vj.Clases.Metodos;
import com.example.reproductormultimedia_vj.Clases.MyMediaPlayer;
import com.example.reproductormultimedia_vj.Clases.Playlist;
import com.example.reproductormultimedia_vj.Clases.Usuario;
import com.example.reproductormultimedia_vj.R;
import com.example.reproductormultimedia_vj.ReproductorActivity;
import com.example.reproductormultimedia_vj.bd.GestionBD;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class PlaylistFragment extends Fragment {

    private static final String ARG_PARAM1 = "USER_ID";
    private static final String ARG_PARAM2 = "PLAY_ID";

    private int userId;
    private int playId;

    private GestionBD gestionBD;
    private Playlist playlist;
    private ArrayList<Cancion> canciones;
    private ArrayList<Cancion> cancionesFiltradas = new ArrayList<>();

    private TextInputEditText buscador;
    private ImageView img;
    private RelativeLayout ly_datos;
    private TextView nombre, creador, likes;
    private ImageButton btn_bucle, btn_aleatorio, btn_play, btn_no_buscar;
    private FloatingActionButton btn_edit;
    private RecyclerView recycler;
    LottieAnimationView btn_like;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    public static PlaylistFragment newInstance(int userId, int playId) {
        PlaylistFragment fragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, userId);
        args.putInt(ARG_PARAM2, playId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(ARG_PARAM1);
            playId = getArguments().getInt(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        initViews(view);
        mostrarDatos();

        configurarBuscador();
        view.setBackgroundColor(Metodos.getDominantColor(((BitmapDrawable) img.getDrawable()).getBitmap()));

        darLike();

        return view;
    }
    public void initViews(View view){
        gestionBD = new GestionBD(getContext());
        playlist = gestionBD.getPlaylistId(playId);
        Usuario creadorObj = null;

        buscador = view.findViewById(R.id.play_buscador);
        img = view.findViewById(R.id.play_img);
        ly_datos = view.findViewById(R.id.play_datos);
        nombre = view.findViewById(R.id.play_nombre);
        likes = view.findViewById(R.id.play_likes);
        creador = view.findViewById(R.id.play_creador);
        btn_aleatorio = view.findViewById(R.id.play_btn_aleatorio);
        btn_bucle = view.findViewById(R.id.play_btn_bucle);
        btn_play = view.findViewById(R.id.play_btn_play);
        recycler = view.findViewById(R.id.play_recycler);
        btn_no_buscar = view.findViewById(R.id.play_no_buscar);
        btn_edit = view.findViewById(R.id.play_edit);
        btn_like = view.findViewById(R.id.btn_animacion);

        if(playId == -2){
            canciones = gestionBD.getCanciones(gestionBD.getCancionesFav(userId));
        }else{
            canciones = gestionBD.getPlaylistCanciones(playId);
            creadorObj = gestionBD.getUsuario(playlist.getIdCreador());
        }

        if(playlist == null){
            nombre.setText("Mis canciones favoritas");
            creador.setText("Yo");
            likes.setText("");
            img.setImageResource(R.drawable.favorites_confondo);
            return;
        }
        if(playlist.getImgPortada() != null){
            img.setImageBitmap(Metodos.convertByteArrayToBitmap(playlist.getImgPortada()));
        }
        nombre.setText(playlist.getNombre());
        creador.setText(creadorObj.getUsername());
        likes.setText(gestionBD.getPlaylistFav(playId)+" 'me gusta' . Duracion");

    }

    public void mostrarDatos() {
        recycler.setLayoutManager(new LinearLayoutManager(recycler.getContext()));
        AdapterCancionLocal adapterCancionLocal = new AdapterCancionLocal(recycler.getContext(), canciones);
        recycler.setAdapter(adapterCancionLocal);
        recycler.setHasFixedSize(true);

        adapterCancionLocal.setOnClickListener(v -> {
            MyMediaPlayer.getInstance().reset();


            if (!cancionesFiltradas.isEmpty()) {
                for (int i=0; i < canciones.size(); ++i) {
                    if (canciones.get(i).getIdCancion() == cancionesFiltradas.get(recycler.getChildAdapterPosition(v)).getIdCancion()) {
                        MyMediaPlayer.currentIndex = i;
                    }
                }
            } else {
                MyMediaPlayer.currentIndex = recycler.getChildAdapterPosition(v);
            }


            Intent intent = new Intent(v.getContext(), ReproductorActivity.class);

           /*if (!cancionesFiltradas.isEmpty()) {
               ArrayList<Integer> id_canciones = new ArrayList<Integer>();
               for (Cancion cancion : cancionesFiltradas) {
                   id_canciones.add(cancion.getIdCancion());
               }
                intent.putExtra("cancionesFiltradas", id_canciones);
            }*/

            v.getContext().startActivity(intent);
        });
    }

    private void configurarBuscador(){
        buscador.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    ly_datos.setVisibility(View.GONE);
                    btn_no_buscar.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_no_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ly_datos.setVisibility(View.VISIBLE);
                v.setVisibility(View.INVISIBLE);
            }
        });

        if(playlist == null) {
            btn_like.setVisibility(View.GONE);
            btn_edit.setVisibility(View.GONE);
            return;
        }

        if(userId == playlist.getIdCreador()){
            btn_edit.setVisibility(View.VISIBLE);
        }else{
            btn_edit.setVisibility(View.GONE);
        }
    }

    private void darLike(){
        btn_like.setAnimation(R.raw.heart_like);
        final boolean[] like = {false};
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like[0] = !like[0];
                Metodos.darLike(like[0], btn_like);
            }
        });
    }
}