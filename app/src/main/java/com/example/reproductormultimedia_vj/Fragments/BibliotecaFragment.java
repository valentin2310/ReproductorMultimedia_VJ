package com.example.reproductormultimedia_vj.Fragments;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reproductormultimedia_vj.Clases.Playlist;
import com.example.reproductormultimedia_vj.Adapter.PlaylistAdapter;
import com.example.reproductormultimedia_vj.Clases.Usuario;
import com.example.reproductormultimedia_vj.R;
import com.example.reproductormultimedia_vj.bd.GestionBD;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class BibliotecaFragment extends Fragment {

    private static final String ARG_PARAM1 = "USER_ID";

    RecyclerView recycler;
    UsuarioFragment usuarioFragment = new UsuarioFragment();

    ImageView btnPerfil;
    TextView txt_usuario;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_biblioteca, container, false);

        GestionBD gestionBD = new GestionBD(this.getContext());
        Usuario user = gestionBD.getUsuario(idUser);

        txt_usuario = (TextView) view.findViewById(R.id.bibl_txt_usuario);
        btnPerfil = (ImageView) view.findViewById(R.id.bibl_btn_perfil);
        recycler = (RecyclerView) view.findViewById(R.id.recyclerPlaylist);

        txt_usuario.setText(user.getUsername());
        if(user.getImgAvatar() != null){
            try{
                // establecer imagen al view
                btnPerfil.setImageBitmap(convertByteArrayToBitmap(user.getImgAvatar()));

            }catch (Exception e){
                Toast.makeText(this.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        initRecycler();

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(usuarioFragment);
            }
        });

        return view;
    }

    public void initRecycler(){

        ArrayList<Playlist> lista = new ArrayList<>();

        lista.add(new Playlist(-1, -1, "Canciones locales", null));

        PlaylistAdapter adapter = new PlaylistAdapter(lista, this.getContext());
        recycler.setHasFixedSize(true);
        GridLayoutManager grid = new GridLayoutManager(this.getContext(), columnasGrid());
        recycler.setLayoutManager(grid);
        recycler.setAdapter(adapter);
    }

    public int columnasGrid(){
        int orientation = getResources().getConfiguration().orientation;

        switch (orientation){
            case Configuration.ORIENTATION_LANDSCAPE:
                return 4;
            case Configuration.ORIENTATION_PORTRAIT:
                return 2;
        }

        return 2;
    }

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }
    public static Bitmap convertByteArrayToBitmap(byte[] byteArrayToBeCOnvertedIntoBitMap) {
        Bitmap bitMapImage = BitmapFactory.decodeByteArray(
                byteArrayToBeCOnvertedIntoBitMap, 0,
                byteArrayToBeCOnvertedIntoBitMap.length);
        return bitMapImage;
    }

}