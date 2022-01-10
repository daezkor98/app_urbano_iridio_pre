package com.urbanoexpress.iridio.model.helpers;

import com.urbanoexpress.iridio.model.entity.Usuario;

import java.util.List;

/**
 * Created by mick on 09/09/16.
 */

public class UsuarioHelper {

    public static Usuario getCurrentUser() {
        List<Usuario> usuario = Usuario.listAll(Usuario.class);
        if (usuario.size() > 0) {
            return usuario.get(0);
        }
        return null;
    }

    public static boolean isUserLogIn() {
        if (getCurrentUser() != null) {
            return true;
        }
        return false;
    }

}
