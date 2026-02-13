package com.dam.gestorasistencia.model;

public class UserSession {
    private static UserSession instance;
    private Usuario usuarioLogueado;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void logIn(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void logOut() {
        this.usuarioLogueado = null;
    }
}