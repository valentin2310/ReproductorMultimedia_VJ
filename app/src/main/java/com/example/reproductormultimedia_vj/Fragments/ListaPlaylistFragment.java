package com.example.reproductormultimedia_vj.Fragments;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reproductormultimedia_vj.Adapter.PlaylistAdapter;
import com.example.reproductormultimedia_vj.Clases.Playlist;
import com.example.reproductormultimedia_vj.Clases.Usuario;
import com.example.reproductormultimedia_vj.R;
import com.example.reproductormultimedia_vj.bd.GestionBD;

import java.util.ArrayList;

public class ListaPlaylistFragment extends Fragment {

    private static final String ARG_PARAM1 = "USER_ID";
    private static final String ARG_PARAM2 = "MODO";
    // 0 = mis canciones // 1 = canciones mis canciones y favoritos // 2 = todas

    private RecyclerView recycler;

    private int idUser;
    private int modo;

    public ListaPlaylistFragment() {
        // Required empty public constructor
    }

    public static ListaPlaylistFragment newInstance(int idUser) {
        ListaPlaylistFragment fragment = new ListaPlaylistFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, idUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idUser = getArguments().getInt(ARG_PARAM1);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_playlist, container, false);

        recycler = view.findViewById(R.id.play_lista_recycler);

        initRecycler();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initRecycler();
    }

    public void initRecycler(){

        ArrayList<Playlist> lista = new GestionBD(getContext()).getAllPlaylist();

        PlaylistAdapter adapter = new PlaylistAdapter(idUser, lista, this.getContext());
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
}