package com.example.reproductormultimedia_vj;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class BibliotecaFragment extends Fragment {

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
        //}

        return view;
    }

    RecyclerView recycler;

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
}