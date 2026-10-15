package com.project.auripa.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Ambil header "Authorization" dari request
        String header = request.getHeader("Authorization");

        // 2. Cek apakah headernya ada dan berawalan "Bearer "
        if (header == null || !header.startsWith("Bearer ")) {
            // Kalau tidak ada, biarkan lewat (nanti bakal dicegat oleh SecurityConfig)
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Potong string "Bearer " untuk mengambil token aslinya saja
        String token = header.substring(7);

        try {
            // 4. Verifikasi keaslian Token pakai Secret Key kita
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("AuripaStore")
                    .build();

            DecodedJWT jwt = verifier.verify(token);

            // 5. Ekstrak data dari dalam token
            String username = jwt.getSubject();
            String role = jwt.getClaim("role").asString();

            // Spring Security punya standar: nama role harus diawali "ROLE_"
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

            // 6. Daftarkan user ini ke dalam "Sistem Keamanan" Spring bahwa dia sudah login
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JWTVerificationException exception) {
            // Kalau token palsu, diubah isinya, atau expired -> TENDANG! (Error 401)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Token tidak valid atau sudah kedaluwarsa!\"}");
            return;
        }

        // Lanjut ke proses berikutnya
        filterChain.doFilter(request, response);
    }
}