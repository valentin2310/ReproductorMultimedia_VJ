package com.example.reproductormultimedia_vj;

import java.util.ArrayList;

public class Usuario {
    private int idUser;
    private String nombre;
    private String username;
    private String password;
    private String sexo;
    private String fechaNac;
    private byte[] avatar;
    private ArrayList<Integer> listaCanciones; // canciones que tiene en favoritos
    private ArrayList<Integer> listaPlaylist; // playlist que tiene en favoritos

    public Usuario(){

    }
    public Usuario(int idUser){
        this.idUser = idUser;
    }
    public Usuario(int idUser, String nombre, String username, String password, String sexo, String fechaNac, byte[] avatar, ArrayList<Integer> listaCancioes, ArrayList<Integer> listaPlaylist) {
        this.idUser = idUser;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.sexo = sexo;
        this.fechaNac = fechaNac;
        this.avatar = avatar;
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

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "Usuario='" + username;
    }
}
