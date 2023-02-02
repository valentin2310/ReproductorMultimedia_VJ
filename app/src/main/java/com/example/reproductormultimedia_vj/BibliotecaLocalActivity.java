package com.example.reproductormultimedia_vj;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class BibliotecaLocalActivity extends AppCompatActivity {
    AdapterCancion adapterCancion;
    RecyclerView recyclerView;
    ArrayList<RV_Cancion> canciones;
    ArrayList<RV_Cancion> cancionesFiltradas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_biblioteca_local);
        recyclerView = findViewById(R.id.recyclerBibliotecaLocal);
        canciones = new ArrayList<>();
        EditText filtro = findViewById(R.id.filtroCancion);

        if (comprobarPermisos() == false) {
            pedirPermisos();
            //return;
        }
        cargarLista();
        mostrarDatos();



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

    }

    boolean comprobarPermisos() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    void pedirPermisos() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(this, "El permiso de Lectura es Requerido, por favor activalo desde los ajustes", Toast.LENGTH_LONG).show();
        }else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
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

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, canciones_movil, seleccion, null, null);

        while (cursor.moveToNext()) {
            String albumId = cursor.getString(4);
            //Cancion cancion = new Cancion(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), getAlbumart(Long.parseLong(cursor.getString(4)) ));
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri,Long.parseLong(albumId));

            //para comprobar si tiene una imagen o no la cancion (pfd)
            try {
                ParcelFileDescriptor pfd = recyclerView.getContext().getContentResolver()
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
            //Toast.makeText(this, "" + , Toast.LENGTH_SHORT).show();
        }

    }

    public void mostrarDatos() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterCancion = new AdapterCancion(this, canciones);
        recyclerView.setAdapter(adapterCancion);

        adapterCancion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyMediaPlayer.getInstance().reset();
                MyMediaPlayer.currentIndex = recyclerView.getChildAdapterPosition(v);
                Intent intent = new Intent(v.getContext(), ReproductorActivity.class);

                if (cancionesFiltradas.isEmpty()) {
                    intent.putExtra("listaCanciones", canciones);
                }
                else {
                    intent.putExtra("listaCanciones", cancionesFiltradas);
                }
                v.getContext().startActivity(intent);

                //Toast.makeText(recyclerView.getContext(), canciones.get(recyclerView.getChildAdapterPosition(v)).getNombre(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}