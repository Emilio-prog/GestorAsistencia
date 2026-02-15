package com.dam.gestorasistencia.model;

/**
 * Gestiona la sesión activa del usuario en memoria durante la ejecución de la app.
 * Implementa un singleton simple para compartir el usuario autenticado entre pantallas.
 *
 * @author Equipo de Desarrollo
 */
public class UserSession {
    private static UserSession instance;
    private Usuario usuarioLogueado;

    /**
     * Crea la sesión interna. Se mantiene privado para forzar el uso de {@link #getInstance()}.
     */
    private UserSession() {}

    /**
     * Devuelve la instancia única de sesión de usuario.
     * Si todavía no existe, la crea en ese momento.
     *
     * @return instancia singleton de la sesión.
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Guarda el usuario como autenticado en la sesión actual.
     *
     * @param usuario usuario que acaba de iniciar sesión.
     */
    public void logIn(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    /**
     * Devuelve el usuario autenticado en este momento.
     *
     * @return usuario en sesión o {@code null} si no hay nadie autenticado.
     */
    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    /**
     * Cierra la sesión eliminando el usuario autenticado actual.
     */
    public void logOut() {
        this.usuarioLogueado = null;
    }
}
