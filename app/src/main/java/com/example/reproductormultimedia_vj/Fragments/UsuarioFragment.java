package com.example.reproductormultimedia_vj.Fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reproductormultimedia_vj.Clases.Metodos;
import com.example.reproductormultimedia_vj.Clases.Usuario;
import com.example.reproductormultimedia_vj.R;
import com.example.reproductormultimedia_vj.bd.GestionBD;
import com.google.android.material.imageview.ShapeableImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


public class UsuarioFragment extends Fragment {

    private static final String ARG_PARAM1 = "USER_ID";

    TextView txt_seleccion;
    ShapeableImageView img;

    private int idUser;
    private GestionBD gestionBD;
    private Usuario usuario;
    Uri imageUri;

    MusicaFragment musicaFragment = new MusicaFragment();
    ListaPlaylistFragment listaPlaylistFragment = new ListaPlaylistFragment();

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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usuario, container, false);

        gestionBD = new GestionBD(this.getContext());
        usuario = gestionBD.getUsuario(idUser);

        musicaFragment = MusicaFragment.newInstance(idUser, true, false);
        listaPlaylistFragment = ListaPlaylistFragment.newInstance(idUser, ListaPlaylistFragment.MIS_PLAYLIST, ListaPlaylistFragment.INVISIBLE);

        txt_seleccion = (TextView) view.findViewById(R.id.txt_seleccion);
        img = view.findViewById(R.id.usuario_img);

        if(usuario.getImgAvatar() != null){
            // establecer imagen al view
            img.setImageBitmap(Metodos.convertByteArrayToBitmap(usuario.getImgAvatar()));
        }

        ImageButton btn_more_opc = (ImageButton) view.findViewById(R.id.usuario_more_opc);
        btn_more_opc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser(v);
            }
        });

        loadFragment(musicaFragment);

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
                        loadFragment(musicaFragment);
                        return true;
                    case R.id.gest_playlist:
                        txt_seleccion.setText("Mis playlist");
                        loadFragment(listaPlaylistFragment);
                        return true;
                    case R.id.gest_sesion:
                        getActivity().finish();
                        return true;
                }

                return false;
            }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.gestionar_usuario_opc, popup.getMenu());
        popup.show();
    }

    public void showFileChooser(View view){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Seleccionada una imagen para tu foto de perfil")
                .setRequestedSize(500, 500)
                .setMaxCropResultSize(1020, 1020)
                .setMinCropResultSize(200, 200)
                .setFixAspectRatio(true)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                img.setImageURI(imageUri);
                if(gestionBD.cambiarAvatar(idUser, Metodos.convertBitmapToByteArray(img))){
                    Toast.makeText(getContext(), "Tu avatar a sido actualizado exitosamente", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.usuario_ly_frame, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }
}