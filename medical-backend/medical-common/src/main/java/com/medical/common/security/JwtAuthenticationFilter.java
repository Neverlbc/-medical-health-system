package com.medical.common.security;

import com.medical.common.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * Minimal JWT authentication filter that reads the Authorization header,
 * validates the token and sets Authentication with principal=userId.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Value("${jwt.header:Authorization}")
    private String headerName;

    @Value("${jwt.prefix:Bearer}")
    private String tokenPrefixConfig;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader(headerName);
            if (StringUtils.hasText(header)) {
                String prefix = tokenPrefixConfig == null ? "Bearer" : tokenPrefixConfig.trim();
                String token = header;
                if (header.regionMatches(true, 0, prefix, 0, prefix.length())) {
                    token = header.substring(prefix.length()).trim();
                }

                if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
                    Long userId = jwtUtils.getUserIdFromToken(token);
                    String username = jwtUtils.getUsernameFromToken(token);
                    String role = jwtUtils.getRoleFromToken(token);
                    // Build authority as ROLE_*
                    SimpleGrantedAuthority authority =
                            StringUtils.hasText(role) ? new SimpleGrantedAuthority("ROLE_" + role) : null;
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId, null,
                                    authority != null ? Collections.singletonList(authority) : Collections.emptyList());
                    authentication.setDetails(username);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            log.warn("JWT filter error: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}

