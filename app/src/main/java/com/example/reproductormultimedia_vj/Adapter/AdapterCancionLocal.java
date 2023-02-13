package com.example.reproductormultimedia_vj.Adapter;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.reproductormultimedia_vj.Clases.Cancion;
import com.example.reproductormultimedia_vj.Clases.Metodos;
import com.example.reproductormultimedia_vj.Clases.RV_Cancion;
import com.example.reproductormultimedia_vj.Clases.Usuario;
import com.example.reproductormultimedia_vj.MenuActivity;
import com.example.reproductormultimedia_vj.R;
import com.example.reproductormultimedia_vj.bd.GestionBD;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AdapterCancionLocal extends RecyclerView.Adapter<AdapterCancionLocal.ViewHolder> implements View.OnClickListener {

    LayoutInflater inflater;
    ArrayList<Cancion> canciones;

    //listener
    private View.OnClickListener listener;

    private Context context;

    public AdapterCancionLocal(Context context, ArrayList<Cancion> canciones) {
        this.inflater = LayoutInflater.from(context);
        this.canciones = canciones;
        this.context = context;

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
        final boolean[] activado = {false};
        GestionBD gestionBD = new GestionBD(context);
        Cancion c = canciones.get(position);
        Usuario user = MenuActivity.USUARIO;

        String nombre = c.getTitulo();
        String artista = c.getNombreArtista();
        String portada = new String(c.getPortada(), StandardCharsets.UTF_8);
        holder.nombre.setText(nombre);
        holder.artista.setText(artista);

        activado[0] = user.getListaCanciones().contains(c.getIdCancion());
        if(activado[0]) holder.like.setProgress(0.5f);

        if (c.getRuta().startsWith("audio/"))
        holder.like.setAnimation(R.raw.heart_like);


        if (!c.getRuta().startsWith("audio/"))
            if (!portada.equals(""))
                holder.imagen.setImageURI(Uri.parse(portada));
            else
                holder.imagen.setImageResource(R.drawable.photo_1614680376573_df3480f0c6ff);
        else {
            holder.imagen.setImageBitmap(Metodos.convertByteArrayToBitmap(c.getPortada()));
        }

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Metodos.darLike(activado[0], holder.like);

                if (!activado[0]) {
                    gestionBD.setCancionFav(user.getIdUser(), c.getIdCancion());
                    Toast.makeText(context, "Cancion añadida en favoritos", Toast.LENGTH_SHORT).show();
                }
                else{
                    gestionBD.eliminarCancionFav(user.getIdUser(), c.getIdCancion());
                    Toast.makeText(context, "Cancion eliminada de favoritos", Toast.LENGTH_SHORT).show();
                }

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
        LottieAnimationView like;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreCancion);
            artista = itemView.findViewById(R.id.artista);
            imagen = itemView.findViewById(R.id.imagenCancion);
            like = itemView.findViewById(R.id.like);
        }
    }

    public void filtrar(ArrayList<Cancion> cancionesFiltradas) {
        canciones = cancionesFiltradas;
        notifyDataSetChanged();
    }


}
