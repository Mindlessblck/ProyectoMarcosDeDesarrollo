package com.example.sonorus.jwt;

import com.example.sonorus.servicio.UsuarioServicio;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UsuarioServicio usuarioServicio;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(UsuarioServicio usuarioServicio, JwtUtil jwtUtil) {
        this.usuarioServicio = usuarioServicio;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Saltar el filtro JWT para rutas públicas
        if (isPublicPath(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = null;
        String email = null;
        Cookie jwtCookie = WebUtils.getCookie(request, "jwt-token");

        if (jwtCookie != null) {
            jwt = jwtCookie.getValue();
            if (jwt != null) {
                try {
                    email = jwtUtil.extractUsername(jwt);
                } catch (Exception e) {
                    // Token inválido o expirado
                }
            }
        }
        
        //Solo continúa si aún no hay un usuario autenticado en la solicitud.
        
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.usuarioServicio.loadUserByUsername(email);

            // validar el token y verificar el rol del usuario
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.equals("/") ||
                path.equals("/index") ||
               // path.equals("/login") ||
                path.equals("/registro") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.startsWith("/img/") ||
                path.startsWith("/static/") ||
                path.equals("/favicon.ico");
             
    }
}