package ru.mirea.postamat.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LEN = 256;
    private static final SecureRandom RND = new SecureRandom();

    public static String hash(String password) {
        byte[] salt = new byte[16];
        RND.nextBytes(salt);
        byte[] dk = pbkdf2(password.toCharArray(), salt);
        return "pbkdf2$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(dk);
    }

    public static boolean verify(String password, String stored) {
        try {
            String[] parts = stored.split("\\$");
            if (parts.length != 4 || !"pbkdf2".equals(parts[0])) return false;
            int iters = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            byte[] actual = pbkdf2(password.toCharArray(), salt, iters);
            return java.util.Arrays.equals(expected, actual);
        } catch (Exception e) { return false; }
    }

    private static byte[] pbkdf2(char[] pwd, byte[] salt) {
        return pbkdf2(pwd, salt, ITERATIONS);
    }

    private static byte[] pbkdf2(char[] pwd, byte[] salt, int iters) {
        try {
            PBEKeySpec spec = new PBEKeySpec(pwd, salt, iters, KEY_LEN);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) { throw new IllegalStateException(e); }
    }

    private PasswordUtil() {}
}