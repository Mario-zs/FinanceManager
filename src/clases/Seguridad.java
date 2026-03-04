package clases;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class Seguridad {

    private static final String SALT = "FM_2024_SALT";

    public static String generarHash(String pass) {
        if (pass == null || pass.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest((pass + SALT).getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash", e);
        }
    }
}
