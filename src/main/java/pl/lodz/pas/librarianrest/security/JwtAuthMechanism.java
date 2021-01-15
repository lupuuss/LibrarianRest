package pl.lodz.pas.librarianrest.security;

import io.jsonwebtoken.ExpiredJwtException;
import pl.lodz.pas.librarianrest.security.objects.JwtCredential;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@ApplicationScoped
public class JwtAuthMechanism implements HttpAuthenticationMechanism {

    @Inject
    private TokenProvider tokenProvider;

    @Override
    public AuthenticationStatus validateRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpMessageContext context
    ) {

        String token = extractToken(context);

        if (token != null) {

            return validateToken(token, context);

        } else if (context.isProtected()) {

            return context.responseUnauthorized();
        }

        return context.doNothing();
    }

    private String extractToken(HttpMessageContext context) {
        String authorizationHeader = context
                .getRequest()
                .getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.split(" ")[1];
        }
        return null;
    }

    private AuthenticationStatus validateToken(String token, HttpMessageContext context) {

        try {

            if (tokenProvider.validateToken(token)) {

                JwtCredential credential = tokenProvider.getCredential(token);
                return context.notifyContainerAboutLogin(credential.getPrincipal(), credential.getAuthorities());
            }

            return context.responseUnauthorized();

        } catch (ExpiredJwtException eje) {

            return context.responseUnauthorized();
        }
    }
}
