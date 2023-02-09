package com.example.reproductormultimedia_vj;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.reproductormultimedia_vj.Adapter.AddCancionPlaylistAdapter;
import com.example.reproductormultimedia_vj.Clases.Metodos;
import com.example.reproductormultimedia_vj.Clases.Playlist;
import com.example.reproductormultimedia_vj.bd.GestionBD;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class AddPlaylistActivity extends AppCompatActivity {

    public static Playlist playlist = new Playlist();
    GestionBD gestionBD = new GestionBD(this);
    private int USER_ID;

    private ImageView imgV;
    private TextInputEditText txt_nombre;
    private RecyclerView recycler;
    private NestedScrollView nested;

    private Uri imageUri;
    private boolean mostrarRecycler = true;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_playlist);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            USER_ID = extras.getInt("USER_ID");
        }

        imgV = findViewById(R.id.addplay_img);
        txt_nombre = findViewById(R.id.addplay_txt_nombre);
        recycler = findViewById(R.id.addplay_recycler);
        nested = findViewById(R.id.addplay_scroll);

        initRecycler();
    }

    public void creaPlaylist(View view){

        String nombre = txt_nombre.getText().toString();
        byte[] img = null;

        if(nombre.isEmpty()){
            txt_nombre.setError("El nombre no puede estar vacio");
            return;
        }

        if(imageUri != null){
            img = Metodos.convertBitmapToByteArray(imgV);
        }

        playlist.setIdCreador(USER_ID);
        playlist.setNombre(nombre);
        playlist.setImgPortada(img);


        if(gestionBD.crearPlaylist(playlist)){
            int id = gestionBD.obtenerUltimoPlaylist();
            gestionBD.setCancionesPlaylist(id, playlist.getListaCanciones());
            Toast.makeText(this, "La playlist se ha creado exitosamente", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "No se ha podido introducir la playlist", Toast.LENGTH_SHORT).show();
        }

        finish();


    }
    public void salir(View view){
        finish();
    }

    public void elegirCanciones(View view){
        mostrarRecycler = !mostrarRecycler;

        if(mostrarRecycler){
            nested.setVisibility(View.VISIBLE);
        }else{
            nested.setVisibility(View.GONE);
        }
    }

    public void initRecycler(){
        AddCancionPlaylistAdapter adapter = new AddCancionPlaylistAdapter(playlist, this);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    int requestedcode = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // si se ha seleccionado una imagen
        if(this.requestedcode == requestCode && resultCode == Activity.RESULT_OK){
            if(data == null){
                return;
            }
            // almaceno la uri de la img seleecionada, asi compruebo si el usuario ha elegido una imagen personalizada
            imageUri = data.getData();
            imgV.setImageURI(imageUri);
        }
    }

    public void showFileChooser(View view){
        Intent fileChooser = new Intent(Intent.ACTION_GET_CONTENT);
        fileChooser.addCategory(Intent.CATEGORY_OPENABLE);
        fileChooser.setType("image/*");
        startActivityForResult(Intent.createChooser(fileChooser, "Elige opcion"), requestedcode);
    }
}