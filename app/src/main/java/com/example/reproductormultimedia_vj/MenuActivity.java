package com.example.reproductormultimedia_vj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.reproductormultimedia_vj.Fragments.BibliotecaFragment;
import com.example.reproductormultimedia_vj.Fragments.MusicaFragment;
import com.example.reproductormultimedia_vj.Fragments.MusicaLocalFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

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
        musicaFragment = MusicaFragment.newInstance(USER_ID);

        BottomNavigationView navigation = findViewById(R.id.botton_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).commit();
        loadFragment(musicaFragment);
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
        transaction.addToBackStack(null);
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