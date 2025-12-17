package org.LunaTelecom.util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 哈希密码
    public static String hash(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    // 验证密码
    public static boolean verify(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }
}