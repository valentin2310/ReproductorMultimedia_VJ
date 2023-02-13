package com.example.reproductormultimedia_vj.Fragments;

import static com.example.reproductormultimedia_vj.Fragments.MusicaFragment.mediaPlayer;
import static com.example.reproductormultimedia_vj.R.id.portada;
import static com.example.reproductormultimedia_vj.R.id.recyclerBibliotecaLocal;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reproductormultimedia_vj.Adapter.AdapterCancionLocal;
import com.example.reproductormultimedia_vj.Clases.Cancion;
import com.example.reproductormultimedia_vj.Clases.CrearNotificacion;
import com.example.reproductormultimedia_vj.Clases.MyMediaPlayer;
import com.example.reproductormultimedia_vj.Clases.cargarCancionesLocalReproductor;
import com.example.reproductormultimedia_vj.R;
import com.example.reproductormultimedia_vj.Clases.RV_Cancion;
import com.example.reproductormultimedia_vj.ReproductorActivity;
import com.example.reproductormultimedia_vj.Servicios.OnClearFromRecentService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

public class MusicaLocalFragment extends Fragment {

    AdapterCancionLocal adapterCancionLocal;
    static RecyclerView recycler;
    static ArrayList<Cancion> canciones;
    ArrayList<Cancion> cancionesFiltradas = new ArrayList<>();
    EditText filtro;
    static Cancion cancion;
    static NotificationManager notificationManager;
    static boolean broadCastCreado = false;
    static BroadcastReceiver broadcastReceiver;

    private static final String ARG_PARAM1 = "USER_ID";
    private int idUser;

    public MusicaLocalFragment() {
        // Required empty public constructor
    }

    public static MusicaLocalFragment newInstance(int idUser) {
        MusicaLocalFragment fragment = new MusicaLocalFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, idUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idUser = getArguments().getInt(ARG_PARAM1);
        }
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_mi_biblioteca_local, container, false);


        canciones = new ArrayList<>();
        filtro = view.findViewById(R.id.filtroCancion);

        //if(savedInstanceState == null){
            recycler = (RecyclerView) view.findViewById(recyclerBibliotecaLocal);

            initRecycler();
        //}

        filtro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filtro(String.valueOf(s));
            }
        });

        return view;
    }



    public void initRecycler(){
            cargarLista();
            mostrarDatos();
            createChannel();
            broadCastCreado = true;
            recycler.setHasFixedSize(true);




    }

    public static void desactivarBroadCast() {
        if (broadcastReceiver != null)
            try {
                recycler.getContext().unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {

            }
    }

    public void filtro(String s) {
        if (filtro.length() >0) {
            cancionesFiltradas = new ArrayList<>();

            for (Cancion cancion : canciones) {
                if (cancion.getTitulo().toLowerCase().contains(s.toLowerCase())) {
                    cancionesFiltradas.add(cancion);
                }
            }

            adapterCancionLocal.filtrar(cancionesFiltradas);
        } else {
            adapterCancionLocal.filtrar(canciones);
        }
    }

    public void cargarLista() {
        canciones = cargarCancionesLocalReproductor.getCancionesLocales();
    }

    public void mostrarDatos() {
        recycler.setLayoutManager(new LinearLayoutManager(recycler.getContext()));
        adapterCancionLocal = new AdapterCancionLocal(recycler.getContext(), canciones);
        recycler.setAdapter(adapterCancionLocal);

        adapterCancionLocal.setOnClickListener(v -> {
            MyMediaPlayer.getInstance().reset();

            if (!cancionesFiltradas.isEmpty()) {
                for (int i=0; i < canciones.size(); ++i) {
                    if (canciones.get(i).getTitulo() == cancionesFiltradas.get(recycler.getChildAdapterPosition(v)).getTitulo()) {
                        MyMediaPlayer.currentIndex = i;
                    }
                }
            } else {
                MyMediaPlayer.currentIndex = recycler.getChildAdapterPosition(v);
            }

            establecerDatosMusica();
            prevCancionFragment.establecerMediaPlayer(mediaPlayer);

            desactivarBroadCast();
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent)  {
                    String action = intent.getExtras().getString("actionname");
                    switch (action) {
                        case CrearNotificacion.ACTION_PREVIUOS:
                            anteriorCancion();
                            break;
                        case CrearNotificacion.ACTION_PLAY:
                            pausePlay();
                            break;
                        case CrearNotificacion.ACTION_NEXT:
                            siguienteCancion();
                            break;
                    }
                }
            };

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity().registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
                getActivity().startService(new Intent(getActivity().getBaseContext(), OnClearFromRecentService.class));
                MusicaFragment.desactivarBroadCast();

            }

        });
    }

    public static void establecerDatosMusica() {
        cancion = canciones.get(MyMediaPlayer.currentIndex);

        prevCancionFragment.actualizarDatos(cancion);

        try {
            CrearNotificacion.createNotification(recycler.getContext(), cancion, R.drawable.ic_baseline_pause_circle_filled_24);
        } catch (IOException e) {
            e.printStackTrace();
        }

        empezarMusica();

    }

    public static void empezarMusica() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(cancion.getRuta());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<Cancion> obtenerCanciones() {
        return canciones;
    }

    public static MediaPlayer obtenerMediaPlayer() {
        return mediaPlayer;
    }

    public static BroadcastReceiver obtenerBroadcast() {
        return broadcastReceiver;
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CrearNotificacion.CHANNEL_ID,
                    "Javi Valentin", NotificationManager.IMPORTANCE_LOW);
            notificationManager = getActivity().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public static void anteriorCancion() {

        if (mediaPlayer.getCurrentPosition() < 3000) {
            if (MyMediaPlayer.currentIndex == 0) {
                mediaPlayer.reset();
                establecerDatosMusica();
            } else {
                MyMediaPlayer.currentIndex -= 1;
                mediaPlayer.reset();
                establecerDatosMusica();
            }
        } else {
            mediaPlayer.reset();
            establecerDatosMusica();
        }

        try {
            CrearNotificacion.createNotification(recycler.getContext(), cancion, R.drawable.ic_baseline_pause_circle_filled_24);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }


    public static void siguienteCancion() {
        if (MyMediaPlayer.currentIndex == canciones.size() - 1)
            return;
        MyMediaPlayer.currentIndex += 1;
        mediaPlayer.reset();
        establecerDatosMusica();
    }

    private static void pausePlay() {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            try {
                CrearNotificacion.createNotification(recycler.getContext(), cancion, R.drawable.ic_baseline_play_circle_filled_24);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            mediaPlayer.start();
            try {
                CrearNotificacion.createNotification(recycler.getContext(), cancion, R.drawable.ic_baseline_pause_circle_filled_24);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}