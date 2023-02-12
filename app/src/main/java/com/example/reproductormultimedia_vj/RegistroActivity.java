package com.example.reproductormultimedia_vj;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.reproductormultimedia_vj.Clases.Metodos;
import com.example.reproductormultimedia_vj.Clases.Usuario;
import com.example.reproductormultimedia_vj.Fragments.DatePickerFragment;
import com.example.reproductormultimedia_vj.bd.GestionBD;
import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        imgV = findViewById(R.id.img_registro);
        txt_usuario = findViewById(R.id.registro_txt_usuario);
        txt_nombre = findViewById(R.id.registro_txt_nombre);
        txt_fecha = findViewById(R.id.registro_txt_fechaNac);
        txt_passwd = findViewById(R.id.registro_txt_passwd);
        txt_passwd2 = findViewById(R.id.registro_txt_passwd2);
        rd_sexo = findViewById(R.id.registro_rd_sexo);

        rd_sexo.check(R.id.registro_rd_none);
    }

    public void showDatePickerDialog(View view){
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                final String selectedDate = twoDigits(day) + "/"+twoDigits(month+1)+"/"+year;
                txt_fecha.setText(selectedDate);
            }
        });
        newFragment.show(this.getSupportFragmentManager(), "datePicker");
    }
    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    public void iniciarSesion(View view){
        Usuario user = comprobarCampos();
        if(user == null) return;

        GestionBD gestionBD = new GestionBD(this);

        if(gestionBD.usuarioExiste(user.getUsername())){
            txt_usuario.setError("Ya existe un usuario con ese username, prueba con otro username");
            return;
        }

        if(gestionBD.crearUsuario(user)){
            Toast.makeText(this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "No se ha podido crear", Toast.LENGTH_SHORT).show();
        }

    }
    public Usuario comprobarCampos(){
        String nombre = txt_nombre.getText().toString();
        String username = txt_usuario.getText().toString();
        String passwd = txt_passwd.getText().toString();
        String passwd2 = txt_passwd2.getText().toString();
        String fecha = txt_fecha.getText().toString();

        if(nombre.isEmpty() || username.isEmpty() || passwd.isEmpty() || passwd2.isEmpty() || fecha.isEmpty()){
            mostrarError(txt_nombre);
            mostrarError(txt_usuario);
            mostrarError(txt_passwd);
            mostrarError(txt_passwd2);
            mostrarError(txt_fecha);
            return null;
        }
        if(!passwd.equals(passwd2)){
            txt_passwd.setError("Las contraseñas no coinciden!!");
            return null;
        }

        if(!Metodos.validarCampo(Metodos.FILTRO_USUARIO, username)){
            txt_usuario.setError("El formato no es valido, trata de no usar caracteres extraños");
            return null;
        }
        else if(!Metodos.validarCampo(Metodos.FILTRO_NOMBRE, nombre)){
            txt_nombre.setError("El formato no es valido");
            return null;
        }
        else if(!Metodos.validarCampo(Metodos.FILTRO_FECHA, fecha) || Metodos.obtenerDate(fecha) == null){
            txt_fecha.setError("La fecha no es valida");
            return null;
        }

        int sexo = sexoSeleccionado();
        byte[] avatar = imageUri != null? Metodos.convertBitmapToByteArray(imgV): null;

        if(avatar == null){
            if(sexo == 0){ // hombre
                imgV.setImageResource(R.drawable.hombre_arana);
            }else if(sexo == 1){
                imgV.setImageResource(R.drawable.unicornio);
            }else{
                imgV.setImageResource(R.drawable.decepticons);
            }
            avatar = Metodos.convertBitmapToByteArray(imgV);
        }


        Usuario user = new Usuario(0, nombre, username, passwd, sexo, fecha, avatar, null, null);

        return user;
    }
    public void mostrarError(TextInputEditText et){
        if(et.getText().toString().isEmpty()){
            et.setError("El campo no debe estar vacio!!");
        }
    }
    public int sexoSeleccionado(){
        int sexo;

        int selectedSexo = rd_sexo.getCheckedRadioButtonId();
        RadioButton rd = findViewById(selectedSexo);
        if(rd.getText().toString().equalsIgnoreCase("hombre")) sexo = 0;
        else if(rd.getText().toString().equalsIgnoreCase("mujer")) sexo = 1;
        else sexo = 2;

        return sexo;
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