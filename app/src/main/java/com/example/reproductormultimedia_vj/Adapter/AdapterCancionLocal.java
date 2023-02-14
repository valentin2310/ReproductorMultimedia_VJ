package com.example.reproductormultimedia_vj.Adapter;


import android.content.Context;
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
import com.example.reproductormultimedia_vj.Clases.Usuario;
import com.example.reproductormultimedia_vj.MenuActivity;
import com.example.reproductormultimedia_vj.R;
import com.example.reproductormultimedia_vj.bd.GestionBD;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AdapterCancionLocal extends RecyclerView.Adapter<AdapterCancionLocal.ViewHolder> implements View.OnClickListener {

    LayoutInflater inflater;
    ArrayList<Cancion> canciones;
    GestionBD gestionBD;

    //listener
    private View.OnClickListener listener;

    private Context context;

    public AdapterCancionLocal(Context context, ArrayList<Cancion> canciones) {
        this.inflater = LayoutInflater.from(context);
        this.canciones = canciones;
        this.context = context;
        this.gestionBD = new GestionBD(context);
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
        Cancion c = canciones.get(position);
        Usuario user = MenuActivity.USUARIO;

        String nombre = c.getTitulo();
        String artista = c.getNombreArtista();
        String portada = new String(c.getPortada(), StandardCharsets.UTF_8);
        holder.nombre.setText(nombre);
        holder.artista.setText(artista);

        // si su id es -1 es cancion local
        if (c.getIdCancion() >= 0) { // cancion bd
            Metodos.setAnimation(holder.like, Metodos.ANIMACION_LIKE); // establecer animacion

            if(!c.getRuta().startsWith("audio/")){
                if (!portada.equals(""))
                    holder.imagen.setImageURI(Uri.parse(portada));
                else
                    holder.imagen.setImageResource(R.drawable.photo_1614680376573_df3480f0c6ff);
            }else{
                holder.imagen.setImageBitmap(Metodos.convertByteArrayToBitmap(c.getPortada())); // establecer portada
            }
            holder.like.setOnClickListener(v -> onClickCancionBD(holder, c, user)); // dar like

        } else { // cancion local
            Metodos.setAnimation(holder.like, Metodos.ANIMACION_ADD); // animacion
            holder.like.setOnClickListener(v -> onClickCancionLocal(holder, c, user)); // añadir a la bd
            if (!portada.isEmpty()) {
                holder.imagen.setImageURI(Uri.parse(portada)); // portada
            }
        }

        // comprobar si la cancion tiene like cancion bd
        if(c.getIdCancion() != -1)
            holder.activado = user.getListaCanciones().contains((Integer) c.getIdCancion());

        // comprobar si la cancion pertenece al usuario, cancion local
        else{
            Cancion aux = gestionBD.getCancion(gestionBD.getCancionIdPorTitulo(c.getTitulo()));
            int idArtista = aux != null? aux.getArtista():-1;
            holder.activado = (idArtista == user.getIdUser() && 0 <= gestionBD.getCancionIdPorTitulo(c.getTitulo()));
        }

        // establecer marcado, como like o añadido
        if (holder.activado) holder.like.setProgress(0.5f);
    }

    private void onClickCancionBD(ViewHolder holder, Cancion c, Usuario user) {
        Metodos.darLike(holder.activado, holder.like);

        if (!holder.activado) {
            gestionBD.setCancionFav(user.getIdUser(), c.getIdCancion());
            user.getListaCanciones().add(c.getIdCancion());
            Toast.makeText(context, "Cancion añadida en favoritos", Toast.LENGTH_SHORT).show();
        } else {
            gestionBD.eliminarCancionFav(user.getIdUser(), c.getIdCancion());
            user.getListaCanciones().remove((Integer) c.getIdCancion());
            Toast.makeText(context, "Cancion eliminada de favoritos", Toast.LENGTH_SHORT).show();
        }

        holder.activado = !holder.activado;
    }

    private void onClickCancionLocal(ViewHolder holder, Cancion c, Usuario user) {
        if(!holder.activado){
            c.setArtista(user.getIdUser());
            boolean exito = gestionBD.agregarCancion(c);
            Toast.makeText(context, exito?"Cancion añadida a la bd":"La cancion ya existe en la bd", Toast.LENGTH_SHORT).show();
            if (!exito) return;
        }else{
            gestionBD.eliminarCancion(gestionBD.getCancionIdPorTitulo(c.getTitulo()));
            Toast.makeText(context, "Cancion eliminada de la bd", Toast.LENGTH_SHORT).show();
        }

        Metodos.darLike(holder.activado, holder.like); // animacion
        holder.activado = !holder.activado; // cambio de valor
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
        boolean activado = false;

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
