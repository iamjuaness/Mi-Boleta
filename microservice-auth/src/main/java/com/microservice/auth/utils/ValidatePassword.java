package com.microservice.auth.utils;

import org.springframework.stereotype.Component;

@Component
public class ValidatePassword {


    public boolean validarContrasena(String password) {
        // Verifica que la contraseña tenga al menos 8 caracteres
        if (password.length() < 8) {
            return false;
        }

        // Verifica que contenga al menos un número
        boolean tieneNumero = false;
        // Verifica que contenga al menos una letra minúscula
        boolean tieneLetra = false;
        // Verifica que contenga al menos una letra mayúscula
        boolean tieneMayuscula = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                tieneNumero = true;
            } else if (Character.isLowerCase(c)) {
                tieneLetra = true;
            } else if (Character.isUpperCase(c)) {
                tieneMayuscula = true;
            }
        }

        // Verifica que cumpla con todas las condiciones
        return tieneNumero && tieneLetra && tieneMayuscula;
    }
}
