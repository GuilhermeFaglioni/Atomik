package com.atomik.atomik_api.domain.service;

public interface PasswordHasherService {
    String hashPassword(String password);

    boolean verifyPassword(String password, String hashedPassword);
}
