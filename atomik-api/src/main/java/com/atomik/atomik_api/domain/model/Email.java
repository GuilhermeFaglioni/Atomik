package com.atomik.atomik_api.domain.model;

import com.atomik.atomik_api.domain.exception.UnauthorizedException;

public record Email(String value) {
    public Email {
        if (value == null || !value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new UnauthorizedException("Invalid email format");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
