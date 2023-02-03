package com.example.reproductormultimedia_vj.Clases;

import java.util.ArrayList;

public class Cancion {
    private int idCancion;
    private String titulo;
    private String descripcion;
    private int artista;
    private String fechaCreacion;
    private byte[] portada;
    private String image_path;
    private ArrayList<Integer> listaFavoritos;

    public Cancion(){

    }
    public Cancion(int idCancion){
        this.idCancion = idCancion;
    }
    public Cancion(int idCancion, String titulo, String descripcion, int artista, String fechaCreacion, byte[] portada, ArrayList<Integer> listaFavoritos) {
        this.idCancion = idCancion;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.artista = artista;
        this.fechaCreacion = fechaCreacion;
        this.portada = portada;
        this.listaFavoritos = listaFavoritos;
    }



    public int getIdCancion() {
        return idCancion;
    }

    public void setIdCancion(int idCancion) {
        this.idCancion = idCancion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getArtista() {
        return artista;
    }

    public void setArtista(int artista) {
        this.artista = artista;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public byte[] getPortada() {
        return portada;
    }

    public void setPortada(byte[] portada) {
        this.portada = portada;
    }

    public ArrayList<Integer> getListaFavoritos() {
        return listaFavoritos;
    }

    public void setListaFavoritos(ArrayList<Integer> listaFavoritos) {
        this.listaFavoritos = listaFavoritos;
    }

    @Override
    public String toString() {
        return titulo+" ❤️ "+listaFavoritos.size();
    }
}
