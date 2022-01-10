package com.urbanoexpress.iridio.util;

import com.urbanoexpress.iridio.model.entity.Usuario;

import java.util.List;

public class Session {

    private static Usuario user;

    public static synchronized Usuario getUser() {
        if (user == null) {
            List<Usuario> users = Usuario.listAll(Usuario.class);
            if (users.size() > 0) {
                user = users.get(0);
            }
        }
        return user;
    }

    public static void clearSession() {
        user = null;
    }
}
