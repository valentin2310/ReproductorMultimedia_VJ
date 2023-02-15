package com.example.reproductormultimedia_vj.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.reproductormultimedia_vj.Clases.Cancion;
import com.example.reproductormultimedia_vj.Clases.Playlist;
import com.example.reproductormultimedia_vj.R;

import java.util.ArrayList;

public class BuscadorFragment extends Fragment {

    private static final String ARG_PARAM1 = "USER_ID";

    private int idUser;

    ListaPlaylistFragment listaPlaylistFragment = new ListaPlaylistFragment();
    MusicaFragment musicaFragment = new MusicaFragment();

    private TextView txt_seleccion;
    private ImageButton more_opc, busq_img;

    private int seleccion = 0; // 0 cancion // 1 playlist
    private boolean mostrarBusq = true;

    public BuscadorFragment() {
        // Required empty public constructor
    }

    public static BuscadorFragment newInstance(int idUser) {
        BuscadorFragment fragment = new BuscadorFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buscador, container, false);

        txt_seleccion = view.findViewById(R.id.busq_txt_seleccion);
        more_opc = view.findViewById(R.id.busq_more_opc);
        busq_img = view.findViewById(R.id.busq_btn_busq);

        more_opc.setOnClickListener(this::showPopup);
        busq_img.setOnClickListener(v -> mostrarOcultarBuscador());

        listaPlaylistFragment = ListaPlaylistFragment.newInstance(idUser, ListaPlaylistFragment.TODAS, ListaPlaylistFragment.VISIBLE);
        musicaFragment = MusicaFragment.newInstance(idUser, true, true);

        loadFragment(musicaFragment);


        return view;
    }

    private void mostrarOcultarBuscador(){
        mostrarBusq = !mostrarBusq;

        if(seleccion == 0)
            musicaFragment.ocultarBuscador(mostrarBusq);
        else
            listaPlaylistFragment.ocultarBuscador(mostrarBusq);

        if(mostrarBusq) busq_img.setImageResource(R.drawable.lupa);
        else busq_img.setImageResource(R.drawable.lupa_off);
    }

    private void showPopup(View v){
        PopupMenu popup = new PopupMenu(getContext(), v);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.busq_canciones:
                        txt_seleccion.setText("Todas las Canciones");
                        loadFragment(musicaFragment);
                        seleccion = 0;
                        return true;
                    case R.id.busq_playlist:
                        txt_seleccion.setText("Todas las Playlist");
                        loadFragment(listaPlaylistFragment);
                        seleccion = 1;
                        return true;
                }

                return false;
            }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.buscar_opc, popup.getMenu());
        popup.show();
    }

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.busq_frame, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
