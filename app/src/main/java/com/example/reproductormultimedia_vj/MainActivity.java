package com.example.reproductormultimedia_vj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.reproductormultimedia_vj.Clases.Usuario;
import com.example.reproductormultimedia_vj.bd.GestionBD;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    TextInputEditText txt_usuario, txt_passwd;
    
    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (comprobarPermisos() == false) {
            pedirPermisos();
            //return;
        }
        
        txt_usuario = findViewById(R.id.login_txt_usuario);
        txt_passwd = findViewById(R.id.login_txt_passwd);
    }

    public void iniciarSesion(View view){

        GestionBD gestionBD = new GestionBD(this);
        Usuario user = gestionBD.comprobarUsuario(txt_usuario.getText().toString(), txt_passwd.getText().toString());
        
        if(user != null){

            Toast.makeText(this, "Exitoso", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra("USER_ID", gestionBD.getUsuarioId(txt_usuario.getText().toString()));

            startActivity(intent);
            
        }else{
            Toast.makeText(this, "No has introducido bien los datos", Toast.LENGTH_SHORT).show();
        }
        
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