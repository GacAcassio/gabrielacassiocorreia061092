package com.project.artists.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CheckPassword {
    public static void main(String[] args) {
        String raw = "admin123";
        String encoded = "$2a$10$TdmglAKkKIDic7ZjHNG5yujhCVX6LxMImZVwGkC07V0ioykd9mTzu";
        System.out.println(new BCryptPasswordEncoder().matches(raw, encoded));
        System.out.println(new BCryptPasswordEncoder().encode("admin123"));
    }
}
