package pl.lodz.pas.librarianrest.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class AuthConfig {

    public static final int VALIDITY_SECONDS = 10 * 60 * 60; // 10 hours
    public static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
}
