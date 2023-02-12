package com.example.reproductormultimedia_vj.Fragments;

import static com.example.reproductormultimedia_vj.R.id.recyclerBibliotecaLocal;

import android.content.ContentUris;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reproductormultimedia_vj.Adapter.AdapterCancionLocal;
import com.example.reproductormultimedia_vj.Clases.Cancion;
import com.example.reproductormultimedia_vj.Clases.Metodos;
import com.example.reproductormultimedia_vj.Clases.MyMediaPlayer;
import com.example.reproductormultimedia_vj.Clases.RV_Cancion;
import com.example.reproductormultimedia_vj.Clases.Usuario;
import com.example.reproductormultimedia_vj.R;
import com.example.reproductormultimedia_vj.ReproductorActivity;
import com.example.reproductormultimedia_vj.bd.GestionBD;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;

public class MusicaFragment extends Fragment {

    AdapterCancionLocal adapterCancionLocal;
    RecyclerView recycler;
    TextView saludos;
    ShapeableImageView btnPerfil;
    Toolbar toolbar;
    LinearLayout txt_filtro;
    ImageButton btn_no_buscar;
    TextInputEditText filtro;

    ArrayList<Cancion> canciones;
    ArrayList<Cancion> cancionesFiltradas = new ArrayList<>();

    private int idUser;
    private boolean edicion = false;
    private boolean buscadorOn = false;

    private static final String ARG_PARAM1 = "USER_ID";
    private static final String ARG_PARAM2 = "EDICION";
    private static final String ARG_PARAM3 = "BUSCADOR";

    public MusicaFragment() {
        // Required empty public constructor
    }
    public static MusicaFragment newInstance(int idUser) {
        MusicaFragment fragment = new MusicaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, idUser);
        fragment.setArguments(args);
        return fragment;
    }
    public static MusicaFragment newInstance(int idUser, boolean edicion, boolean buscadorOn) {
        MusicaFragment fragment = new MusicaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, idUser);
        args.putBoolean(ARG_PARAM2, edicion);
        args.putBoolean(ARG_PARAM3, buscadorOn);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                idUser = getArguments().getInt(ARG_PARAM1);
                if(getArguments().size() > 1){
                    edicion = getArguments().getBoolean(ARG_PARAM2);
                    buscadorOn = getArguments().getBoolean(ARG_PARAM3);
                }
            }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_mi_biblioteca_local, container, false);


        canciones = new ArrayList<>();
        filtro = view.findViewById(R.id.filtroCancion);

        saludos = view.findViewById(R.id.cancion_saludos);
        darLosBuenosDias();

        GestionBD gestionBD = new GestionBD(this.getContext());
        Usuario user = gestionBD.getUsuario(idUser);

        toolbar = view.findViewById(R.id.cancion_toolbar);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE || edicion){
            toolbar.setVisibility(View.GONE);
        }else{
            toolbar.setVisibility(View.VISIBLE);
        }

        txt_filtro = view.findViewById(R.id.filtro);
        txt_filtro.setVisibility(buscadorOn?View.VISIBLE:View.GONE);

        btnPerfil = view.findViewById(R.id.cancion_btn_perfil);
        btn_no_buscar = view.findViewById(R.id.cancion_no_buscar);

        if(user.getImgAvatar() != null){
            // establecer imagen al view
            btnPerfil.setImageBitmap(Metodos.convertByteArrayToBitmap(user.getImgAvatar()));
        }

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsuarioFragment usuarioFragment = UsuarioFragment.newInstance(idUser);
                loadFragment(usuarioFragment);
            }
        });

        configurarBuscador();

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
        agregarCancionesBaseDatos();
        cargarCancionesBaseDatos();
        mostrarDatos();
        recycler.setHasFixedSize(true);


    }

    public void filtro(String s) {
        cancionesFiltradas = new ArrayList<>();

        for (Cancion cancion : canciones) {
            if (cancion.getTitulo().toLowerCase().contains(s.toLowerCase())) {
                cancionesFiltradas.add(cancion);
            }
        }

        adapterCancionLocal.filtrar(cancionesFiltradas);
    }

    public void agregarCancionesBaseDatos() {
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();

        try {

            String path = "audio";

            String [] files  = getContext().getAssets().list(path);

            for (int i = 0; i < files.length; i++) {
                String file = path + "/" + files[i];

                AssetFileDescriptor afd = getContext().getAssets().openFd(file);

                metaRetriver.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

                String titulo = metaRetriver.extractMetadata(metaRetriver.METADATA_KEY_TITLE);
                String artist = metaRetriver.extractMetadata(metaRetriver.METADATA_KEY_ARTIST);
                String duracion = metaRetriver.extractMetadata(metaRetriver.METADATA_KEY_DURATION);
                String fechaCreacion = metaRetriver.extractMetadata(metaRetriver.METADATA_KEY_YEAR);

                byte [] portada = metaRetriver.getEmbeddedPicture();

                GestionBD gestion = new GestionBD(recycler.getContext());
                gestion.agregarCancion(new Cancion(0, titulo, "descripcion pito",idUser, artist, fechaCreacion, duracion, portada, path + "/" + files[i] ));
                afd.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        metaRetriver.release();



    }

    public void cargarCancionesBaseDatos() {
        GestionBD gestion = new GestionBD(recycler.getContext());
        canciones = gestion.getCanciones();
    }

    public void mostrarDatos() {
        recycler.setLayoutManager(new LinearLayoutManager(recycler.getContext()));
        adapterCancionLocal = new AdapterCancionLocal(recycler.getContext(), canciones);
        recycler.setAdapter(adapterCancionLocal);

        adapterCancionLocal.setOnClickListener(v -> {
            MyMediaPlayer.getInstance().reset();

            if (!cancionesFiltradas.isEmpty()) {
                for (int i=0; i < canciones.size(); ++i) {
                    if (canciones.get(i).getIdCancion() == cancionesFiltradas.get(recycler.getChildAdapterPosition(v)).getIdCancion()) {
                        MyMediaPlayer.currentIndex = i;
                    }
                }
            } else {
                MyMediaPlayer.currentIndex = recycler.getChildAdapterPosition(v);
            }

            Intent intent = new Intent(v.getContext(), ReproductorActivity.class);

           /*if (!cancionesFiltradas.isEmpty()) {
               ArrayList<Integer> id_canciones = new ArrayList<Integer>();
               for (Cancion cancion : cancionesFiltradas) {
                   id_canciones.add(cancion.getIdCancion());
               }
                intent.putExtra("cancionesFiltradas", id_canciones);
            }*/

            v.getContext().startActivity(intent);
        });
    }

    private void darLosBuenosDias(){
        String mensaje = "";
        int hora = new Date().getHours();

        if(hora >= 7 && hora <= 12) mensaje = "¡Buenas mañanas!";
        else if(hora > 12 && hora <= 21) mensaje = "¡Buenas tardes!";
        else mensaje = "¡Buenas noches!";

        saludos.setText(mensaje);
    }

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void configurarBuscador(){
        filtro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    btn_no_buscar.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_no_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void ocultarBuscador(boolean ocultar){
        txt_filtro.setVisibility(!ocultar?View.VISIBLE:View.GONE);
    }

}