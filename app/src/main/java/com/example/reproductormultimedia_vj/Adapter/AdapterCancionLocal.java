package com.example.reproductormultimedia_vj.Adapter;


import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reproductormultimedia_vj.Clases.RV_Cancion;
import com.example.reproductormultimedia_vj.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AdapterCancionLocal extends RecyclerView.Adapter<AdapterCancionLocal.ViewHolder> implements View.OnClickListener {

    LayoutInflater inflater;
    ArrayList<RV_Cancion> canciones;

    //listener
    private View.OnClickListener listener;

    private Context context;

    public AdapterCancionLocal(Context context, ArrayList<RV_Cancion> canciones) {
    this.inflater = LayoutInflater.from(context);
    this.canciones = canciones;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = inflater.inflate(R.layout.recycler_view_row, parent, false);
        vista.setOnClickListener(this);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nombre = canciones.get(position).getNombre();
        String artista = canciones.get(position).getArtista();
        String imagen_path = canciones.get(position).getImage_path();
        holder.nombre.setText(nombre);
        holder.artista.setText(artista);

        if (canciones.get(position).getImage_path() != "")
            holder.imagen.setImageURI(Uri.parse(imagen_path));
        else
            holder.imagen.setImageResource(R.drawable.photo_1614680376573_df3480f0c6ff);


        final boolean[] activado = {false};

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (!activado[0]) {
                        holder.like.setImageResource(R.drawable.ic_baseline_favorite_24);
                        holder.like.setColorFilter(Color.argb(255, 0,170,255));
                    }
                    else
                        holder.like.setImageResource(R.drawable.ic_baseline_favorite_border_24);

                    activado[0] = !activado[0];
            }
        });

    }

    @Override
    public int getItemCount() {
        return canciones.size();
    }

    @Override
    public void onClick(View v) {
        if (listener != null)
            listener.onClick(v);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, artista;
        ImageView imagen;
        FloatingActionButton like;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreCancion);
            artista = itemView.findViewById(R.id.artista);
            imagen = itemView.findViewById(R.id.imagenCancion);
            like = itemView.findViewById(R.id.like);
        }
    }

    public void filtrar(ArrayList<RV_Cancion> cancionesFiltradas) {
        canciones = cancionesFiltradas;
        notifyDataSetChanged();
    }


}
