package com.example.reproductormultimedia_vj.Fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reproductormultimedia_vj.AddPlaylistActivity;
import com.example.reproductormultimedia_vj.Clases.CrearNotificacion;
import com.example.reproductormultimedia_vj.Clases.Metodos;
import com.example.reproductormultimedia_vj.Clases.MyMediaPlayer;
import com.example.reproductormultimedia_vj.Clases.PlayListActual;
import com.example.reproductormultimedia_vj.Clases.Playlist;
import com.example.reproductormultimedia_vj.Adapter.PlaylistAdapter;
import com.example.reproductormultimedia_vj.Clases.Usuario;
import com.example.reproductormultimedia_vj.MenuActivity;
import com.example.reproductormultimedia_vj.R;
import com.example.reproductormultimedia_vj.Servicios.OnClearFromRecentService;
import com.example.reproductormultimedia_vj.bd.GestionBD;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class BibliotecaFragment extends Fragment {

    private static final String ARG_PARAM1 = "USER_ID";

    RecyclerView recycler;
    UsuarioFragment usuarioFragment = new UsuarioFragment();
    ListaPlaylistFragment listaPlaylistFragment = new ListaPlaylistFragment();

    ImageView btnPerfil;
    TextView txt_usuario, txt_n_play;
    ImageButton btnAddPlay;
    BroadcastReceiver broadcastReceiver;

    GestionBD gestionBD;

    private int idUser;

    public BibliotecaFragment() {
        // Required empty public constructor
    }
    public static BibliotecaFragment newInstance(int idUser) {
        BibliotecaFragment fragment = new BibliotecaFragment();
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
        View view = inflater.inflate(R.layout.fragment_biblioteca, container, false);

        gestionBD = new GestionBD(this.getContext());
        Usuario user = gestionBD.getUsuario(idUser);

        usuarioFragment = UsuarioFragment.newInstance(idUser);
        listaPlaylistFragment = ListaPlaylistFragment.newInstance(idUser, ListaPlaylistFragment.MIS_PLAYLIST_Y_FAV, ListaPlaylistFragment.INVISIBLE);

        txt_usuario = (TextView) view.findViewById(R.id.bibl_txt_usuario);
        txt_n_play = (TextView) view.findViewById(R.id.bibl_txt_n_play);
        btnPerfil = (ImageView) view.findViewById(R.id.bibl_btn_perfil);
        recycler = (RecyclerView) view.findViewById(R.id.recyclerPlaylist);
        btnAddPlay = (ImageButton) view.findViewById(R.id.bibl_btn_add_playlist);

        txt_usuario.setText(user.getUsername());
        if(user.getImgAvatar() != null){
            // establecer imagen al view
            btnPerfil.setImageBitmap(Metodos.convertByteArrayToBitmap(user.getImgAvatar()));
        }

        txt_n_play.setText("Tienes "+gestionBD.getPlaylist(idUser).size()+" playlist");

        //loadFragmentInside(listaPlaylistFragment);

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(usuarioFragment);
            }
        });

        btnAddPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddPlaylistActivity.class);
                intent.putExtra("USER_ID", user.getIdUser());

                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadFragmentInside(listaPlaylistFragment);
    }

    @Override
    public void onResume() {
        super.onResume();
        PlayListActual.comprobarSiSeHaGirado(getContext());

    }

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void loadFragmentInside(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.bibl_frame, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

}