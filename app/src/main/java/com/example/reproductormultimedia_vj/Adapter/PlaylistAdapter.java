package com.example.reproductormultimedia_vj.Adapter;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reproductormultimedia_vj.Clases.Metodos;
import com.example.reproductormultimedia_vj.Clases.Playlist;
import com.example.reproductormultimedia_vj.Clases.Usuario;
import com.example.reproductormultimedia_vj.Fragments.MusicaLocalFragment;
import com.example.reproductormultimedia_vj.Fragments.PlaylistFragment;
import com.example.reproductormultimedia_vj.R;
import com.example.reproductormultimedia_vj.bd.GestionBD;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private LayoutInflater minflate;
    private Context context;

    private ArrayList<Playlist> lista;
    private int idUser;

    public PlaylistAdapter(int idUser, ArrayList<Playlist> lista, Context context){
        this.context = context;
        this.minflate = LayoutInflater.from(context);
        this.lista = lista;
        this.idUser = idUser;
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = minflate.inflate(R.layout.tarjeta_playlist, parent, false);
        return new PlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        holder.bindData(lista.get(position));
        holder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView nombre, autor;
        ImageView img;
        Playlist play;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.rv_nombre_playlist);
            autor = itemView.findViewById(R.id.rv_autor_playlist);
            img = itemView.findViewById(R.id.rv_img_playlist);
        }

        public void bindData(Playlist p){
            nombre.setText(p.getNombre());
            if(p.getIdCreador() == -1) autor.setText(" · Yo");
            else autor.setText(new GestionBD(context).getUsuario(p.getIdCreador()).getUsername());
            if(p.getImgPortada() != null) img.setImageBitmap(Metodos.convertByteArrayToBitmap(p.getImgPortada()));

            this.play = p;
        }

        public void setOnClickListeners(){
            img.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            // if local mostrar canciones locales
            if(play.getIdPlaylist() == -1){
                MusicaLocalFragment musicaLocalFragment = MusicaLocalFragment.newInstance(1);
                loadFragment(musicaLocalFragment);
            }else{
                PlaylistFragment playlistFragment = PlaylistFragment.newInstance(idUser, play.getIdPlaylist());
                loadFragment(playlistFragment);
            }
        }

        public void loadFragment(Fragment fragment){
            Activity activity = (Activity) context;

            FragmentManager ft = ((FragmentActivity)activity).getSupportFragmentManager();
            FragmentTransaction transaction = ft.beginTransaction();
            transaction.replace(R.id.frame_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}