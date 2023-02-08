package com.example.reproductormultimedia_vj;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class AddPlaylistActivity extends AppCompatActivity {

    private int USER_ID;

    private ImageView imgV;
    private TextInputEditText txt_nombre;
    private Uri imageUri;

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
    }

    public void creaPlaylist(View view){

        String nombre = txt_nombre.getText().toString();

        if(nombre.isEmpty()){
            txt_nombre.setError("El nombre no puede estar vacio");
            return;
        }
        else{
            //TODO guardar playlist en la base de datos
            Toast.makeText(this, "La playlist se ha creado exitosamente", Toast.LENGTH_SHORT).show();
            finish();
        }


    }
    public void salir(View view){
        finish();
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