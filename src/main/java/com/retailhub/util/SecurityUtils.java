package com.retailhub.util;

import org.mindrot.jbcrypt.BCrypt;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SecurityUtils {

    /**
     * Gera um hash BCrypt para a senha.
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Verifica se a senha corresponde ao hash salvo.
     */
    public static boolean checkPassword(String password, String hashed) {
        try {
            return BCrypt.checkpw(password, hashed);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica se a senha expirou (mais de 90 dias).
     */
    public static boolean isPasswordExpired(String lastChangeDate) {
        if (lastChangeDate == null || lastChangeDate.isEmpty()) return true;
        try {
            LocalDate lastChange = LocalDate.parse(lastChangeDate);
            long days = ChronoUnit.DAYS.between(lastChange, LocalDate.now());
            return days >= 90;
        } catch (Exception e) {
            return true;
        }
    }
}
