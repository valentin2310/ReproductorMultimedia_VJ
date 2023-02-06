package com.example.reproductormultimedia_vj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.reproductormultimedia_vj.Fragments.BibliotecaFragment;
import com.example.reproductormultimedia_vj.Fragments.MusicaFragment;
import com.example.reproductormultimedia_vj.Fragments.MusicaLocalFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuActivity extends AppCompatActivity {

    BibliotecaFragment bibliotecaFragment = new BibliotecaFragment();
    MusicaFragment musicaFragment = new MusicaFragment();
    MusicaLocalFragment musicaLocalFragment = new MusicaLocalFragment();

    int USER_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            USER_ID = extras.getInt("USER_ID");
        }

        bibliotecaFragment = BibliotecaFragment.newInstance(USER_ID);

        BottomNavigationView navigation = findViewById(R.id.botton_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).commit();
        //loadFragment(bibliotecaFragment);
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
            }

            return false;
        }
    };

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }
}