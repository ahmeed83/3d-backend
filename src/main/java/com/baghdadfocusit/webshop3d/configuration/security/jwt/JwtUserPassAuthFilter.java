package com.baghdadfocusit.webshop3d.configuration.security.jwt;

import com.baghdadfocusit.webshop3d.configuration.security.JwtConfig;
import com.baghdadfocusit.webshop3d.exception.security.UserNotAuthenticatedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * This filter will be executed when the user try to log in. It will generate a token and send it back to the user.
 */
public class JwtUserPassAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    public JwtUserPassAuthFilter(AuthenticationManager authenticationManager,
                                 JwtConfig jwtConfig,
                                 SecretKey secretKey) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
    }

    /**
     * Attempt to authenticate the user.
     *
     * @param request  req
     * @param response res
     * @return Authentication
     * @throws AuthenticationException AuthenticationException
     */
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            final UserPassAuthRequest authenticationRequest = new ObjectMapper().readValue(request.getInputStream(),
                                                                                           UserPassAuthRequest.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(authenticationRequest.getUserName(),
                                                                                    authenticationRequest.getPassword());
            return authenticationManager.authenticate(authentication);
        } catch (IOException e) {
            throw new UserNotAuthenticatedException(e);
        }
    }

    /**
     * This method will be executed only if the attempt method successfully end. This method will build the token and
     * send
     * it back to the customer.
     *
     * @param request    req
     * @param response   res
     * @param chain      chain
     * @param authResult authResult
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        final var authorizationHeader = jwtConfig.getAuthorizationHeader();
        final var tokenPrefix = jwtConfig.getTokenPrefix();
        final var expirationAfterDays = jwtConfig.getTokenExpirationAfterDays();
        final String token = Jwts.builder()
                .setSubject(authResult.getName())
                .claim("authorities", authResult.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDateTime.now().toLocalDate().plusWeeks(expirationAfterDays)))
                .signWith(secretKey)
                .compact();
        authResult.getAuthorities()
                .stream()
                .filter(x -> x.getAuthority() != null && x.getAuthority().length() > 5 && x.getAuthority()
                        .startsWith("ROLE_"))
                .findFirst()
                .ifPresent((role -> response.addHeader("user", role.getAuthority().substring(5).toLowerCase())));
        response.addHeader(authorizationHeader, tokenPrefix + token);
    }
}
