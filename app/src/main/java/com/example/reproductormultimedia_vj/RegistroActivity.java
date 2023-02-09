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

        byte[] avatar = imageUri != null? Metodos.convertBitmapToByteArray(imgV): null;
        int sexo = sexoSeleccionado();

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