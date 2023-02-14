package com.example.reproductormultimedia_vj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.reproductormultimedia_vj.Clases.CrearNotificacion;
import com.example.reproductormultimedia_vj.Clases.Metodos;
import com.example.reproductormultimedia_vj.Clases.MyMediaPlayer;
import com.example.reproductormultimedia_vj.Clases.PlayListActual;
import com.example.reproductormultimedia_vj.Fragments.BuscadorFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.reproductormultimedia_vj.Clases.Usuario;
import com.example.reproductormultimedia_vj.Fragments.BibliotecaFragment;
import com.example.reproductormultimedia_vj.Fragments.ListaPlaylistFragment;
import com.example.reproductormultimedia_vj.Fragments.MusicaFragment;
import com.example.reproductormultimedia_vj.Fragments.MusicaLocalFragment;
import com.example.reproductormultimedia_vj.Fragments.prevCancionFragment;
import com.example.reproductormultimedia_vj.Servicios.OnClearFromRecentService;
import com.example.reproductormultimedia_vj.bd.GestionBD;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class MenuActivity extends AppCompatActivity {

    BibliotecaFragment bibliotecaFragment = new BibliotecaFragment();
    MusicaFragment musicaFragment = new MusicaFragment();
    MusicaLocalFragment musicaLocalFragment = new MusicaLocalFragment();
    prevCancionFragment prevCancionFragment = new prevCancionFragment();
    BuscadorFragment buscadorFragment = new BuscadorFragment();

    NotificationManager notificationManager;
    BottomNavigationView navigation;
    public static boolean esLocal =false;


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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }

        bibliotecaFragment = BibliotecaFragment.newInstance(USER_ID);
        musicaFragment = MusicaFragment.newInstance(USER_ID, false, false);
        buscadorFragment = BuscadorFragment.newInstance(USER_ID);


        navigation = findViewById(R.id.botton_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).commit();

        if(savedInstanceState == null) {
            loadFragment(musicaFragment);
            loadFragmentPrevCancion(prevCancionFragment);
        }

        findViewById(R.id.frame_prevCancion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayListActual.esPlaylist == 1) {
                    Intent intent = new Intent(v.getContext(), ReproductorActivity.class);
                    intent.putExtra("esPlayList", true);
                    v.getContext().startActivity(intent);
                } else {
                    if (PlayListActual.esPlaylist == 0) {
                        Intent intent = new Intent(v.getContext(), ReproductorActivity.class);
                        v.getContext().startActivity(intent);
                    } else if (PlayListActual.esPlaylist == 2){
                        Intent intent = new Intent(v.getContext(), ReproductorActivity.class);
                        intent.putExtra("esLocal", true);
                        v.getContext().startActivity(intent);
                    }
                }
            }
        });

        MyMediaPlayer.getInstance().setOnCompletionListener(v -> {
            PlayListActual.siguienteCancion(navigation.getContext(), PlayListActual.esPlaylist==0 || PlayListActual.esPlaylist==1 ? false: true);
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            //obtener background del la activity
            TypedArray array = getTheme().obtainStyledAttributes(new int[] {
                    android.R.attr.windowBackground});
            int backgroundColor = array.getColor(0, 0xFF00FF);
            array.recycle();

            window.setStatusBarColor(Metodos.darker(backgroundColor, (float) 3.27));
        }

    }



    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CrearNotificacion.CHANNEL_ID,
                    "Javi Valentin", NotificationManager.IMPORTANCE_LOW);
            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)  {
            String action = intent.getExtras().getString("actionname");
            switch (action) {
                case CrearNotificacion.ACTION_PREVIUOS:
                    PlayListActual.anteriorCancion(navigation.getContext(), esLocal);
                    break;
                case CrearNotificacion.ACTION_PLAY:
                    PlayListActual.pausePlay(navigation.getContext());
                    break;
                case CrearNotificacion.ACTION_NEXT:
                    PlayListActual.siguienteCancion(navigation.getContext(), esLocal);
                    break;
            }
        }
    };

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
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        int orientacion_inicial = getResources().getConfiguration().orientation;

        if (orientacion_inicial == Configuration.ORIENTATION_PORTRAIT) {
            PlayListActual.orientacion = 1;
        } else {
            PlayListActual.orientacion = 2;
        }
    }


}