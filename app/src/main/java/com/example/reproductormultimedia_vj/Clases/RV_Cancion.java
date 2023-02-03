package com.example.reproductormultimedia_vj.Clases;

import java.io.Serializable;

public class RV_Cancion implements Serializable {
    private String nombre;
    private String artista;
    private String duracion;
    private String path;
    private int imagenID;
    private String image_path;

    public RV_Cancion() {}

    public RV_Cancion(String nombre, String artista, String duracion, String path, int imagenID) {
        this.nombre = nombre;
        this.artista = artista;
        this.duracion = duracion;
        this.path = path;
        this.imagenID = imagenID;

    }

    public RV_Cancion(String nombre, String artista, String duracion, String path, String image_path) {
        this.nombre = nombre;
        this.artista = artista;
        this.duracion = duracion;
        this.path = path;
        this.image_path = image_path;

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public int getImagenID() {
        return imagenID;
    }

    public void setImagenID(int imagenID) {
        this.imagenID = imagenID;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

}
