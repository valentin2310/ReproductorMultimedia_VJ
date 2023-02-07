package com.example.reproductormultimedia_vj.Clases;

import java.util.ArrayList;

public class Cancion {
    private int idCancion;
    private String titulo;
    private String descripcion;
    private int artista;
    private String nombreArtista;
    private String fechaCreacion;
    private String duracion;
    private byte[] portada;
    private String ruta;

    public Cancion(){

    }
    public Cancion(int idCancion){
        this.idCancion = idCancion;
    }

    public Cancion(int idCancion, String titulo, String descripcion, int artista, String nombreArtista, String fechaCreacion, String duracion, byte[] portada, String ruta) {
        this.idCancion = idCancion;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.artista = artista;
        this.nombreArtista = nombreArtista;
        this.fechaCreacion = fechaCreacion;
        this.duracion = duracion;
        this.portada = portada;
        this.ruta = ruta;
    }


    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public byte[] getPortada() {
        return portada;
    }

    public void setPortada(byte[] portada) {
        this.portada = portada;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getNombreArtista() {
        return nombreArtista;
    }

    public void setNombreArtista(String nombreArtista) {
        this.nombreArtista = nombreArtista;
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


    /*@Override
    public String toString() {
        return titulo+" ❤️ "+listaFavoritos.size();
    }*/
}
