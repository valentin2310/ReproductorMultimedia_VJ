package com.example.reproductormultimedia_vj;

import static com.example.reproductormultimedia_vj.R.id.recyclerBibliotecaLocal;

import android.content.ContentUris;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class BibliotecaLocalFragment extends Fragment {

    AdapterCancion adapterCancion;
    RecyclerView recycler;
    ArrayList<RV_Cancion> canciones;
    ArrayList<RV_Cancion> cancionesFiltradas = new ArrayList<>();


    public BibliotecaLocalFragment() {
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

        adapterCancion.filtrar(cancionesFiltradas);
    }

    public void cargarLista() {
        String[] canciones_movil = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID
        };

        String seleccion = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cursor = recycler.getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, canciones_movil, seleccion, null, null);

        while (cursor.moveToNext()) {
            String albumId = cursor.getString(4);
            //Cancion cancion = new Cancion(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), getAlbumart(Long.parseLong(cursor.getString(4)) ));
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri,Long.parseLong(albumId));

            //para comprobar si tiene una imagen o no la cancion (pfd)
            try {
                ParcelFileDescriptor pfd = recycler.getContext().getContentResolver()
                        .openFileDescriptor(albumArtUri, "r");
                if (pfd !=null) {
                    RV_Cancion cancion = new RV_Cancion(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), albumArtUri.toString());
                    if (new File(cancion.getPath()).exists())
                        canciones.add(cancion);
                }
            } catch (FileNotFoundException e) {
                RV_Cancion cancion = new RV_Cancion(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), "");
                if (new File(cancion.getPath()).exists())
                    canciones.add(cancion);
            }

        }

    }

    public void mostrarDatos() {
        recycler.setLayoutManager(new LinearLayoutManager(recycler.getContext()));
        adapterCancion = new AdapterCancion(recycler.getContext(), canciones);
        recycler.setAdapter(adapterCancion);

        adapterCancion.setOnClickListener(v -> {
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