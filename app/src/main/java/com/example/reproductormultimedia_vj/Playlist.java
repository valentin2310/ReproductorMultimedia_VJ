package com.example.reproductormultimedia_vj;

import java.util.ArrayList;

public class Playlist { // playlist
    private int idPlaylist;
    private int idCreador; // id del usuario que ha creado la playlist
    private String nombre;
    private byte[] portada;
    private boolean privada; // la playlist podra ser publica o privada
    private ArrayList<Integer> listaCanciones; // almacena los id's de las canciones de la playlist
    private ArrayList<Integer> listaUsuarios; // almacena los id's de los usuarios que tiene esta playlist en favoritos

    public Playlist(){

    }
    public Playlist(int idPlaylist){
        this.idPlaylist = idPlaylist;
    }

    public Playlist(int idPlaylist, int idCreador, String nombre, byte[] portada) {
        this.idPlaylist = idPlaylist;
        this.idCreador = idCreador;
        this.nombre = nombre;
        this.portada = portada;
    }

    public Playlist(int idPlaylist, int idCreador, String nombre, byte[] portada, boolean privada, ArrayList<Integer> listaCanciones, ArrayList<Integer> listaUsuarios) {
        this.idPlaylist = idPlaylist;
        this.idCreador = idCreador;
        this.nombre = nombre;
        this.portada = portada;
        this.privada = privada;
        this.listaCanciones = listaCanciones;
        this.listaUsuarios = listaUsuarios;
    }

    public int getIdPlaylist() {
        return idPlaylist;
    }

    public void setIdPlaylist(int idPlaylist) {
        this.idPlaylist = idPlaylist;
    }

    public int getIdCreador() {
        return idCreador;
    }

    public void setIdCreador(int idCreador) {
        this.idCreador = idCreador;
    }

    public String getNombre() {
        return nombre;
    }

    public byte[] getPortada() {
        return portada;
    }

    public void setPortada(byte[] portada) {
        this.portada = portada;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isPrivada() {
        return privada;
    }

    public void setPrivada(boolean privada) {
        this.privada = privada;
    }

    public ArrayList<Integer> getListaCanciones() {
        return listaCanciones;
    }

    public void setListaCanciones(ArrayList<Integer> listaCanciones) {
        this.listaCanciones = listaCanciones;
    }

    public ArrayList<Integer> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(ArrayList<Integer> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    @Override
    public String toString() {
        return nombre+" ❤️ "+listaUsuarios.size();
    }
}
