package es.examplepb.findstockmanager.seguridad;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//clase para automatizar el proceso de codificar las contraseñas, generando una codificación y reemplazandola en el fichero data.sql
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String[] passwords = {"password123", "password456"}; // Lista de contraseñas originales
        for (String password : passwords) {
            System.out.println("Contraseña original: " + password);
            System.out.println("Contraseña codificada: " + encoder.encode(password));
        }
    }
}
