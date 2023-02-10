package com.example.reproductormultimedia_vj.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reproductormultimedia_vj.AddPlaylistActivity;
import com.example.reproductormultimedia_vj.Clases.Cancion;
import com.example.reproductormultimedia_vj.Clases.Metodos;
import com.example.reproductormultimedia_vj.Clases.Playlist;
import com.example.reproductormultimedia_vj.R;
import com.example.reproductormultimedia_vj.bd.GestionBD;

import java.util.ArrayList;

public class AddCancionPlaylistAdapter extends RecyclerView.Adapter<AddCancionPlaylistAdapter.ViewHolder>{

    private LayoutInflater minflate;
    private Context context;

    private Playlist playlist;
    private ArrayList<Cancion> listaCanciones;

    public AddCancionPlaylistAdapter(Playlist playlist, Context context) {
        this.minflate = LayoutInflater.from(context);
        this.context = context;
        this.playlist = playlist;
        this.listaCanciones = new GestionBD(context).getCanciones();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = minflate.inflate(R.layout.tarjeta_add_cancion_playlist, parent, false);
        return new AddCancionPlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(listaCanciones.get(position));
        holder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {
        return listaCanciones.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView nombre, id;
        ImageView img;
        CheckBox check;
        Cancion cancion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.addPlaySong_rv_txt_nombre);
            id = itemView.findViewById(R.id.addPlaySong_rv_id);
            img = itemView.findViewById(R.id.addPlaySong_rv_img);
            check = itemView.findViewById(R.id.addPlaySong_rv_check);
        }

        public void bindData(Cancion c){
            nombre.setText(c.getTitulo());
            if(c.getPortada() != null){
                img.setImageBitmap(Metodos.convertByteArrayToBitmap(c.getPortada()));
            }
            this.cancion = c;

            check.setChecked(playlist.getListaCanciones().contains(c));

        }

        public void setOnClickListeners(){
            check.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            if(check.isChecked()){
                AddPlaylistActivity.playlist.getListaCanciones().add(cancion.getIdCancion());
            }else{
                AddPlaylistActivity.playlist.getListaCanciones().remove((Integer)cancion.getIdCancion());
            }
        }
    }
}
