package pl.lodz.pas.librarianrest.security;

import io.jsonwebtoken.ExpiredJwtException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@ApplicationScoped
public class JwtAuthMechanism implements HttpAuthenticationMechanism {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER = "Bearer ";

    @Inject
    @SuppressWarnings("CdiInjectionPointsInspection")
    private IdentityStoreHandler storeHandler;

    @Inject
    private TokenProvider tokenProvider;

    @Override
    public AuthenticationStatus validateRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpMessageContext context
    ) {

        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String token = extractToken(context);

        if (login != null && password != null) {

            CredentialValidationResult result = storeHandler.validate(new UsernamePasswordCredential(login, password));

            if (result.getStatus() == CredentialValidationResult.Status.VALID) {

                return createToken(result, context);
            }

            return context.responseUnauthorized();

        } else if (token != null) {

            return validateToken(token, context);

        } else if (context.isProtected()) {

            return context.responseUnauthorized();
        }

        return context.doNothing();
    }

    private String extractToken(HttpMessageContext context) {
        String authorizationHeader = context
                .getRequest()
                .getHeader(AUTHORIZATION_HEADER);

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            return authorizationHeader.substring(BEARER.length());
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

    private AuthenticationStatus createToken(CredentialValidationResult result, HttpMessageContext context) {

        String jwt = tokenProvider.createToken(
                result.getCallerPrincipal().getName(),
                result.getCallerGroups()
        );

        context.getResponse().setHeader(AUTHORIZATION_HEADER, BEARER + jwt);

        return context.notifyContainerAboutLogin(result.getCallerPrincipal(), result.getCallerGroups());
    }
}
