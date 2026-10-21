package ru.mirea.postamat.util;

import ru.mirea.postamat.model.Role;
import ru.mirea.postamat.model.User;

public final class Session {
    private static User current;
    public static User get() { return current; }
    public static void set(User u) { current = u; }
    public static boolean isAdmin() {
        return current != null && current.getRole() == Role.ADMIN;
    }
    private Session() {}
}