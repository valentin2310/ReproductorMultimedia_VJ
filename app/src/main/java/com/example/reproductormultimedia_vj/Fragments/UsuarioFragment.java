package com.example.reproductormultimedia_vj.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.reproductormultimedia_vj.Clases.Metodos;
import com.example.reproductormultimedia_vj.Clases.Usuario;
import com.example.reproductormultimedia_vj.R;
import com.example.reproductormultimedia_vj.bd.GestionBD;


public class UsuarioFragment extends Fragment {

    private static final String ARG_PARAM1 = "USER_ID";

    TextView txt_seleccion;
    ImageView img;

    private int idUser;

    public UsuarioFragment() {
        // Required empty public constructor
    }

    public static UsuarioFragment newInstance(int idUser) {
        UsuarioFragment fragment = new UsuarioFragment();
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
        View view = inflater.inflate(R.layout.fragment_usuario, container, false);

        GestionBD gestionBD = new GestionBD(this.getContext());
        Usuario user = gestionBD.getUsuario(idUser);

        txt_seleccion = (TextView) view.findViewById(R.id.txt_seleccion);
/*
        if(user.getImgAvatar() != null){
            // establecer imagen al view
            btnPerfil.setImageBitmap(Metodos.convertByteArrayToBitmap(user.getImgAvatar()));
        }
*/
        ImageButton btn_more_opc = (ImageButton) view.findViewById(R.id.usuario_more_opc);
        btn_more_opc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        return view;
    }

    private void showPopup(View v){
        PopupMenu popup = new PopupMenu(getContext(), v);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.gest_canciones:
                        txt_seleccion.setText("Mis canciones");
                        return true;
                    case R.id.gest_playlist:
                        txt_seleccion.setText("Mis playlist");
                        return true;
                }

                return false;
            }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.gestionar_usuario_opc, popup.getMenu());
        popup.show();
    }
}