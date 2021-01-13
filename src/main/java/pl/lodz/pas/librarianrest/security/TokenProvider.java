package pl.lodz.pas.librarianrest.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TokenProvider {


    private static final String AUTHORITIES_KEY = "auth";

    private Key secretKey;

    private long tokenValidity;


    @PostConstruct
    public void init() {
        // load from config
        this.secretKey = AuthConfig.SECRET_KEY;
        this.tokenValidity = TimeUnit.SECONDS.toMillis(AuthConfig.VALIDITY_SECONDS);   //10 hourshours
    }

    public String createToken(String username, Set<String> authorities) {
        long now = (new Date()).getTime();

        return Jwts.builder()
                .setSubject(username)
                .claim(AUTHORITIES_KEY, String.join(",", authorities))
                .signWith(secretKey)
                .setExpiration(new Date(now + tokenValidity))
                .compact();
    }

    public JwtCredential getCredential(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Set<String> authorities = new HashSet<>(Arrays.asList(claims.get(AUTHORITIES_KEY).toString().split(",")));

        return new JwtCredential(claims.getSubject(), authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            return false;
        }
    }
}