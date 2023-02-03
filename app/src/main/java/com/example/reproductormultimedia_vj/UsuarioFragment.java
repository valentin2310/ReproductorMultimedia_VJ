package com.example.reproductormultimedia_vj;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;


public class UsuarioFragment extends Fragment {

    TextView txt_seleccion;

    public UsuarioFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usuario, container, false);

        txt_seleccion = (TextView) view.findViewById(R.id.txt_seleccion);

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