package com.example.reproductormultimedia_vj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reproductormultimedia_vj.Clases.Usuario;
import com.example.reproductormultimedia_vj.Clases.cargarCancionesLocalReproductor;
import com.example.reproductormultimedia_vj.bd.GestionBD;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextInputEditText txt_usuario, txt_passwd;

    int idUser = -1;
    
    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (comprobarPermisos() == false) {
            pedirPermisos();
            //return;
        }
        cargarCancionesLocalReproductor.cargarCancionesLocales(this);

        txt_usuario = findViewById(R.id.login_txt_usuario);
        txt_passwd = findViewById(R.id.login_txt_passwd);

        GestionBD gestionBD = new GestionBD(this);

        // TODO saltar login
        //idUser = gestionBD.getUsuarioDefaultId();
        //if(idUser != -1) iniciarSesion(txt_usuario);

    }

    private Usuario comprobarRegistro(){

        String username = txt_usuario.getText().toString();
        String passwd = txt_passwd.getText().toString();

        if(username.isEmpty() || passwd.isEmpty()){
            txt_usuario.setError("Debes rellenar todos los datos!!");
            return null;
        }

        GestionBD gestionBD = new GestionBD(this);
        Usuario user = gestionBD.comprobarUsuario(username, passwd);

        if(user == null){
            txt_usuario.setError("El usuario o contrase??a no son correctos!!");
            txt_passwd.setText("");
        }

        return user;
    }

    public void iniciarSesion(View view){

       // if(idUser == -1){
            Usuario user = comprobarRegistro();

            if(user == null){
                return;
            }

            idUser = user.getIdUser();
            new GestionBD(this).setUsuarioDefault(idUser);
        //}

        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra("USER_ID", idUser);

        startActivity(intent);
        
    }

    public void registrarse(View view){
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
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
}