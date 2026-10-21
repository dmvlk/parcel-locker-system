package ru.mirea.postamat.util;

public class HashGen {
    public static void main(String[] args) {
        String password = args.length > 0 ? args[0] : "admin123";
        System.out.println(PasswordUtil.hash(password));
    }
}