package com.example.reproductormultimedia_vj;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.reproductormultimedia_vj.Clases.Metodos;
import com.example.reproductormultimedia_vj.Clases.Usuario;
import com.example.reproductormultimedia_vj.bd.GestionBD;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RegistroActivity extends AppCompatActivity {

    ImageView imgV;
    TextInputEditText txt_usuario, txt_nombre, txt_passwd, txt_passwd2, txt_fecha;
    RadioGroup rd_sexo;

    Uri imageUri;

    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        imgV = findViewById(R.id.img_registro);
        txt_usuario = findViewById(R.id.registro_txt_usuario);
        txt_nombre = findViewById(R.id.registro_txt_nombre);
        txt_fecha = findViewById(R.id.registro_txt_fechaNac);
        txt_passwd = findViewById(R.id.registro_txt_passwd);
        txt_passwd2 = findViewById(R.id.registro_txt_passwd2);
        rd_sexo = findViewById(R.id.registro_rd_sexo);

    }

    public void iniciarSesion(View view){

        byte[] avatar = null;

        if (imageUri != null){
            avatar = Metodos.convertBitmapToByteArray(imgV);
        }

        int sexo;
        int selectedSexo = rd_sexo.getCheckedRadioButtonId();
        RadioButton rd = findViewById(selectedSexo);
        if(rd.getText().toString().equalsIgnoreCase("hombre")) sexo = 0;
        else if(rd.getText().toString().equalsIgnoreCase("mujer")) sexo = 1;
        else sexo = 2;

        Usuario user = new Usuario(0, txt_nombre.getText().toString(), txt_usuario.getText().toString(), txt_passwd.getText().toString(), sexo, txt_fecha.getText().toString(), avatar, null, null);

        GestionBD gestionBD = new GestionBD(this);
        if(gestionBD.crearUsuario(user)){
            Toast.makeText(this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "No se ha podido crear", Toast.LENGTH_SHORT).show();
        }
        
    }

    int requestedcode = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(this.requestedcode == requestCode && resultCode == Activity.RESULT_OK){
            if(data == null){
                return;
            }
            imageUri = data.getData();
            imgV.setImageURI(imageUri);

            //createImage();
/*
            Bitmap bitmap = ((BitmapDrawable) imgV.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageInByte = baos.toByteArray();

            Bitmap b = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);
            imgV.setImageBitmap(Bitmap.createScaledBitmap(b, 1080, 1080, false));

 */
        }
    }

    public void showFileChooser(View view){
        Intent fileChooser = new Intent(Intent.ACTION_GET_CONTENT);
        fileChooser.addCategory(Intent.CATEGORY_OPENABLE);
        fileChooser.setType("image/*");
        startActivityForResult(Intent.createChooser(fileChooser, "Elige opcion"), requestedcode);
    }

    private Uri createImage(){
        Uri uri = null;

        BitmapDrawable drawable = (BitmapDrawable) imgV.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        File filepath = Environment.getExternalStorageDirectory();
        File dir = new File(filepath.getAbsolutePath()+"/REPRODUCTOR_VJ/");
        dir.mkdir();
        File file = new File(dir, System.currentTimeMillis()+".jpg");
        FileOutputStream outputStream = null;

        try{
            outputStream = new FileOutputStream(file);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        Toast.makeText(this, "Image saved to internal", Toast.LENGTH_SHORT).show();

        Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        try{
            outputStream.flush();
            outputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        return uri;
    }

    private void startCrop(Uri uri){

    }
}