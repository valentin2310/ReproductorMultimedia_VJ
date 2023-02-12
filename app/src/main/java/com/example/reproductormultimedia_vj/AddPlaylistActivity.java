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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.reproductormultimedia_vj.Adapter.AddCancionPlaylistAdapter;
import com.example.reproductormultimedia_vj.Clases.Metodos;
import com.example.reproductormultimedia_vj.Clases.Playlist;
import com.example.reproductormultimedia_vj.bd.GestionBD;
import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

public class AddPlaylistActivity extends AppCompatActivity {

    public static Playlist playlist = new Playlist();
    GestionBD gestionBD = new GestionBD(this);
    private int USER_ID;
    private int PLAY_ID = -1;

    private ImageView imgV;
    private TextInputEditText txt_nombre;
    private RecyclerView recycler;
    private NestedScrollView nested;
    private Button btn_delete;

    private Uri imageUri;
    private boolean mostrarRecycler = true;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_playlist);

        imgV = findViewById(R.id.addplay_img);
        txt_nombre = findViewById(R.id.addplay_txt_nombre);
        recycler = findViewById(R.id.addplay_recycler);
        nested = findViewById(R.id.addplay_scroll);
        btn_delete = findViewById(R.id.addplay_btn_delete);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            USER_ID = extras.getInt("USER_ID");
            if(extras.size() > 1){
                PLAY_ID = extras.getInt("PLAY_ID");
                rellenarDatos(PLAY_ID);
            }
        }


        //initRecycler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initRecycler();
    }

    public void rellenarDatos(int idPlay){
        playlist = gestionBD.getPlaylistId(idPlay);
        playlist.setListaCanciones(gestionBD.getPlaylistCancionesIds(PLAY_ID));

        if(playlist.getImgPortada() != null){
            imgV.setImageBitmap(Metodos.convertByteArrayToBitmap(playlist.getImgPortada()));
        }
        txt_nombre.setText(playlist.getNombre());
        btn_delete.setVisibility(View.VISIBLE);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gestionBD.eliminarPlaylist(PLAY_ID) > 0){
                    Toast.makeText(AddPlaylistActivity.this, "La playlist se elimino exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

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

        if(PLAY_ID != -1){
            playlist.setIdPlaylist(PLAY_ID);
            if(gestionBD.updatePlaylist(playlist)){
                gestionBD.setCancionesPlaylist(PLAY_ID, playlist.getListaCanciones());
                Toast.makeText(this, "La playlist se ha creado exitosamente", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "No se ha podido introducir la playlist", Toast.LENGTH_SHORT).show();
            }
        }else{
            if(gestionBD.crearPlaylist(playlist)){
                int id = gestionBD.obtenerUltimoPlaylist();
                gestionBD.setCancionesPlaylist(id, playlist.getListaCanciones());
                Toast.makeText(this, "La playlist se ha creado exitosamente", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "No se ha podido introducir la playlist", Toast.LENGTH_SHORT).show();
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                imgV.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void showFileChooser(View view){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Seleccionada una imagen para tu foto de perfil")
                .setRequestedSize(500, 500)
                .setMaxCropResultSize(1020, 1020)
                .setMinCropResultSize(200, 200)
                .setFixAspectRatio(true)
                .start(this);
    }
}