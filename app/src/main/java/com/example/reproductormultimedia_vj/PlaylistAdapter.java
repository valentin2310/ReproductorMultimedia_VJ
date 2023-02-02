package com.example.reproductormultimedia_vj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private LayoutInflater minflate;
    private Context context;

    private ArrayList<Playlist> lista;

    public PlaylistAdapter(ArrayList<Playlist> lista, Context context){
        this.context = context;
        this.minflate = LayoutInflater.from(context);
        this.lista = lista;
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
            autor.setText(p.getIdCreador()+" · Creador");
            if(p.getPortada() != null) img.setImageBitmap(byteArrayToBitmap(p.getPortada()));

            this.play = p;
        }

        public void setOnClickListeners(){
            img.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, nombre.getText(), Toast.LENGTH_SHORT).show();
        }

        Bitmap byteArrayToBitmap(byte[] data) {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
    }
}