package com.example.reproductormultimedia_vj.Clases;

import java.util.ArrayList;

public class Usuario {
    private int idUser;
    private String nombre;
    private String username;
    private String password;
    private int sexo;
    private String fechaNac;
    private byte[] imgAvatar;
    private ArrayList<Integer> listaCanciones; // canciones que tiene en favoritos
    private ArrayList<Integer> listaPlaylist; // playlist que tiene en favoritos

    public Usuario(){

    }
    public Usuario(int idUser){
        this.idUser = idUser;
    }
    public Usuario(int idUser, String nombre, String username, String password, int sexo, String fechaNac, byte[] avatar, ArrayList<Integer> listaCancioes, ArrayList<Integer> listaPlaylist) {
        this.idUser = idUser;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.sexo = sexo;
        this.fechaNac = fechaNac;
        this.imgAvatar = avatar;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSexo() {
        return sexo;
    }

    public void setSexo(int sexo) {
        this.sexo = sexo;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    public byte[] getImgAvatar() {
        return imgAvatar;
    }

    public void setImgAvatar(byte[] imgAvatar) {
        this.imgAvatar = imgAvatar;
    }

    public ArrayList<Integer> getListaCanciones() {
        return listaCanciones;
    }

    public void setListaCanciones(ArrayList<Integer> listaCanciones) {
        this.listaCanciones = listaCanciones;
    }

    public ArrayList<Integer> getListaPlaylist() {
        return listaPlaylist;
    }

    public void setListaPlaylist(ArrayList<Integer> listaPlaylist) {
        this.listaPlaylist = listaPlaylist;
    }

    @Override
    public String toString() {
        return "Usuario='" + username;
    }
}
