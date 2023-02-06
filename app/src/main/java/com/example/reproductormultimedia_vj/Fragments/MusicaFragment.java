package com.example.reproductormultimedia_vj.Fragments;

import static com.example.reproductormultimedia_vj.R.id.recyclerBibliotecaLocal;

import android.content.ContentUris;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reproductormultimedia_vj.Adapter.AdapterCancionLocal;
import com.example.reproductormultimedia_vj.Clases.Cancion;
import com.example.reproductormultimedia_vj.Clases.MyMediaPlayer;
import com.example.reproductormultimedia_vj.Clases.RV_Cancion;
import com.example.reproductormultimedia_vj.R;
import com.example.reproductormultimedia_vj.ReproductorActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

public class MusicaFragment extends Fragment {

    AdapterCancionLocal adapterCancionLocal;
    RecyclerView recycler;
    ArrayList<RV_Cancion> canciones;
    ArrayList<RV_Cancion> cancionesFiltradas = new ArrayList<>();


    public MusicaFragment() {
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
        View view = inflater.inflate(R.layout.activity_mi_biblioteca_local, container, false);


        canciones = new ArrayList<>();
        EditText filtro = view.findViewById(R.id.filtroCancion);

        //if(savedInstanceState == null){
            recycler = (RecyclerView) view.findViewById(recyclerBibliotecaLocal);

            initRecycler();
        //}

        filtro.addTextChangedListener(new TextWatcher() {
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

        return view;
    }



    public void initRecycler(){
            cargarLista();
            mostrarDatos();
            recycler.setHasFixedSize(true);


    }

    public void filtro(String s) {
        cancionesFiltradas = new ArrayList<>();

        for (RV_Cancion cancion : canciones) {
            if (cancion.getNombre().toLowerCase().contains(s.toLowerCase())) {
                cancionesFiltradas.add(cancion);
            }
        }

        adapterCancionLocal.filtrar(cancionesFiltradas);
    }

    public void cargarLista() {
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();

        try {

            String path = "audio";

            String [] files  = getContext().getAssets().list(path);

            for (int i = 0; i < files.length; i++) {
                String file = path + "/" + files[i];

                AssetFileDescriptor afd = getContext().getAssets().openFd(file);

                metaRetriver.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

                String titulo = metaRetriver.extractMetadata(metaRetriver.METADATA_KEY_TITLE);
                String artist = metaRetriver.extractMetadata(metaRetriver.METADATA_KEY_ARTIST);
                String duracion = metaRetriver.extractMetadata(metaRetriver.METADATA_KEY_DURATION);
                String portada = metaRetriver.extractMetadata(metaRetriver.METADATA_KEY_ALBUM);


                String data =  "file:///android_asset/" + path + "/" + files[i];

                canciones.add(new RV_Cancion(titulo, artist, duracion, data, "baseDatos", files[i]));
                afd.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        metaRetriver.release();



    }

    public void mostrarDatos() {
        recycler.setLayoutManager(new LinearLayoutManager(recycler.getContext()));
        adapterCancionLocal = new AdapterCancionLocal(recycler.getContext(), canciones);
        recycler.setAdapter(adapterCancionLocal);

        adapterCancionLocal.setOnClickListener(v -> {
            MyMediaPlayer.getInstance().reset();
            MyMediaPlayer.currentIndex = recycler.getChildAdapterPosition(v);
            Intent intent = new Intent(v.getContext(), ReproductorActivity.class);

            if (cancionesFiltradas.isEmpty()) {
                intent.putExtra("listaCanciones", canciones);
            }
            else {
                intent.putExtra("listaCanciones", cancionesFiltradas);
            }
            v.getContext().startActivity(intent);
        });
    }

}