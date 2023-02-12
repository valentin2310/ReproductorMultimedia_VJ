package com.example.reproductormultimedia_vj.Fragments;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.reproductormultimedia_vj.Adapter.PlaylistAdapter;
import com.example.reproductormultimedia_vj.Clases.Cancion;
import com.example.reproductormultimedia_vj.Clases.Playlist;
import com.example.reproductormultimedia_vj.R;
import com.example.reproductormultimedia_vj.bd.GestionBD;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class ListaPlaylistFragment extends Fragment {

    private static final String ARG_PARAM1 = "USER_ID";
    private static final String ARG_PARAM2 = "MODO";
    private static final String ARG_PARAM3 = "TOOLBAR";

    // MODO
    public static int MIS_PLAYLIST = 0, MIS_PLAYLIST_Y_FAV = 1, TODAS = 2;

    // TOOLBAR VISIBLE
    public static boolean VISIBLE = true, INVISIBLE = false;

    private RecyclerView recycler;
    private ImageButton btn_no_buscar;
    private TextInputEditText buscador;
    private Toolbar toolbar;

    ArrayList<Playlist> listaPlaylist = new ArrayList<>();
    ArrayList<Playlist> playlistFiltradas = new ArrayList<>();
    PlaylistAdapter adapter;
    GestionBD gestionBD;

    private int idUser;
    private int modo;
    private boolean toolbarOn;

    public ListaPlaylistFragment() {
        // Required empty public constructor
    }

    public static ListaPlaylistFragment newInstance(int idUser, int modo, boolean toolbarOn) {
        ListaPlaylistFragment fragment = new ListaPlaylistFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, idUser);
        args.putInt(ARG_PARAM2, modo);
        args.putBoolean(ARG_PARAM3, toolbarOn);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idUser = getArguments().getInt(ARG_PARAM1);
            modo = getArguments().getInt(ARG_PARAM2);
            toolbarOn = getArguments().getBoolean(ARG_PARAM3);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_playlist, container, false);

        gestionBD = new GestionBD(getContext());
        recycler = view.findViewById(R.id.play_lista_recycler);
        buscador = view.findViewById(R.id.play_lista_buscador);
        btn_no_buscar = view.findViewById(R.id.play_lista_no_buscar);
        toolbar = view.findViewById(R.id.play_lista_toolbar);

        toolbar.setVisibility(toolbarOn?View.VISIBLE:View.GONE);

        buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filtro(String.valueOf(s));
            }
        });

        initRecycler();
        configurarBuscador();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initRecycler();
    }

    public void initRecycler(){

        listaPlaylist = new ArrayList<>();
        if(modo == MIS_PLAYLIST || modo == MIS_PLAYLIST_Y_FAV){
            listaPlaylist.addAll(gestionBD.getPlaylist(idUser));
        }
        if(modo == MIS_PLAYLIST_Y_FAV){
            ArrayList<Integer> ids = new ArrayList<>();

            for(Playlist p: listaPlaylist){
                ids.add(p.getIdPlaylist());
            }
            for (Playlist p : gestionBD.getFavPlaylist(idUser)){
                if(!ids.contains((Integer) p.getIdPlaylist())) listaPlaylist.add(p);
            }

            listaPlaylist.add(0, new Playlist(-1, -1, "Canciones locales", null));
            listaPlaylist.add(1, new Playlist(-2, idUser, "Mis favoritos", null));
        }
        if(modo == TODAS){
            listaPlaylist.addAll(gestionBD.getAllPlaylist());
        }

        adapter = new PlaylistAdapter(idUser, listaPlaylist, this.getContext());
        recycler.setHasFixedSize(true);
        GridLayoutManager grid = new GridLayoutManager(this.getContext(), columnasGrid());
        recycler.setLayoutManager(grid);
        recycler.setAdapter(adapter);
    }

    public int columnasGrid(){
        int orientation = getResources().getConfiguration().orientation;

        switch (orientation){
            case Configuration.ORIENTATION_LANDSCAPE:
                return 4;
            case Configuration.ORIENTATION_PORTRAIT:
                return 2;
        }

        return 2;
    }

    private void configurarBuscador(){
        buscador.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    btn_no_buscar.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_no_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void ocultarBuscador(boolean ocultar){
        toolbar.setVisibility(!ocultar?View.VISIBLE:View.GONE);

    }

    public void filtro(String s) {
        playlistFiltradas = new ArrayList<>();

        for (Playlist play : listaPlaylist) {
            if (play.getNombre().toLowerCase().contains(s.toLowerCase())) {
                playlistFiltradas.add(play);
            }
        }

        adapter.filtrar(playlistFiltradas);
    }
}