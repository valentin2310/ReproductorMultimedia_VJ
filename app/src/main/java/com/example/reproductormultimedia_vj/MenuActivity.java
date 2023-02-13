package com.example.reproductormultimedia_vj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.example.reproductormultimedia_vj.Fragments.BuscadorFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.reproductormultimedia_vj.Clases.Usuario;
import com.example.reproductormultimedia_vj.Fragments.BibliotecaFragment;
import com.example.reproductormultimedia_vj.Fragments.ListaPlaylistFragment;
import com.example.reproductormultimedia_vj.Fragments.MusicaFragment;
import com.example.reproductormultimedia_vj.Fragments.MusicaLocalFragment;
import com.example.reproductormultimedia_vj.Fragments.prevCancionFragment;
import com.example.reproductormultimedia_vj.bd.GestionBD;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class MenuActivity extends AppCompatActivity {

    BibliotecaFragment bibliotecaFragment = new BibliotecaFragment();
    MusicaFragment musicaFragment = new MusicaFragment();
    MusicaLocalFragment musicaLocalFragment = new MusicaLocalFragment();
    prevCancionFragment prevCancionFragment = new prevCancionFragment();
    BuscadorFragment buscadorFragment = new BuscadorFragment();


    public static int USER_ID = -1;
    public static Usuario USUARIO = null;
    GestionBD gestionBD = new GestionBD(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            USER_ID = extras.getInt("USER_ID");
            USUARIO = gestionBD.getUsuario(USER_ID);
            USUARIO.setListaCanciones(gestionBD.getCancionesFav(USER_ID));
        }



        bibliotecaFragment = BibliotecaFragment.newInstance(USER_ID);
        musicaFragment = MusicaFragment.newInstance(USER_ID);
        buscadorFragment = BuscadorFragment.newInstance(USER_ID);


        BottomNavigationView navigation = findViewById(R.id.botton_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).commit();

        if(savedInstanceState == null) {
            loadFragment(musicaFragment);
            loadFragmentPrevCancion(prevCancionFragment);
        }

        findViewById(R.id.frame_prevCancion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!prevCancionFragment.obtenerSiEsLocal()) {
                    Intent intent = new Intent(v.getContext(), ReproductorActivity.class);
                    v.getContext().startActivity(intent);
                }else {
                    Intent intent = new Intent(v.getContext(), ReproductorActivity.class);
                    intent.putExtra("esLocal", true);
                    v.getContext().startActivity(intent);
                }
            }
        });
    }
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch(item.getItemId()){
                case R.id.bibliotecaFragment:
                    loadFragment(bibliotecaFragment);
                    return true;

               case R.id.primero:
                    loadFragment(musicaFragment);
                    return true;

                case R.id.buscadorFragment:
                    loadFragment(buscadorFragment);
                    return true;
            }

            return false;
        }
    };

    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            musicaFragment.getNotificationManager().cancelAll();
        }
        unregisterReceiver(musicaFragment.obtenerBroadcast());
    }


    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadFragmentPrevCancion(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_prevCancion, fragment);
        transaction.commit();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof TextInputEditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}