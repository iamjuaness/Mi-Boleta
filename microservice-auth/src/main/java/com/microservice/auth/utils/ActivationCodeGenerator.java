package com.microservice.auth.utils;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ActivationCodeGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // Letras mayúsculas

    // Método para generar un código de activación con 4 números y 2 letras
    public  String generateActivationCode() {
        StringBuilder code = new StringBuilder();

        // Generar los 4 números
        for (int i = 0; i < 4; i++) {
            int randomNumber = random.nextInt(10); // Números entre 0 y 9
            code.append(randomNumber);
        }

        // Generar las 2 letras
        for (int i = 0; i < 2; i++) {
            int randomLetterIndex = random.nextInt(LETTERS.length()); // Índice aleatorio para las letras
            code.append(LETTERS.charAt(randomLetterIndex));
        }

        return code.toString();  // Retorna el código como una cadena de texto
    }
}
