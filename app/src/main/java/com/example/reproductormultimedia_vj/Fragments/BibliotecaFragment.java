package com.example.reproductormultimedia_vj.Fragments;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reproductormultimedia_vj.Clases.Playlist;
import com.example.reproductormultimedia_vj.Adapter.PlaylistAdapter;
import com.example.reproductormultimedia_vj.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BibliotecaFragment extends Fragment {

    RecyclerView recycler;
    UsuarioFragment usuarioFragment = new UsuarioFragment();

    public BibliotecaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_biblioteca, container, false);

        //if(savedInstanceState == null){
            recycler = (RecyclerView) view.findViewById(R.id.recyclerPlaylist);
            initRecycler();

            FloatingActionButton btnPerfil = (FloatingActionButton) view.findViewById(R.id.bibl_btn_perfil);
            btnPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFragment(usuarioFragment);
                }
            });
        //}

        return view;
    }

    public void initRecycler(){

        ArrayList<Playlist> lista = new ArrayList<>();

        lista.add(new Playlist(1, 1, "BAilando con osos", null));
        lista.add(new Playlist(2, 1, "Perros nocturnos", null));
        lista.add(new Playlist(3, 1, "Lluvia de sexo", null));
        lista.add(new Playlist(4, 2, "Manolo no porfavor", null));
        lista.add(new Playlist(5, 3, "Tuturu", null));

        PlaylistAdapter adapter = new PlaylistAdapter(lista, this.getContext());
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

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }
}