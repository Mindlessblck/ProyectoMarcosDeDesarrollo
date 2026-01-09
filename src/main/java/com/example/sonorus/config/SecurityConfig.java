package com.example.sonorus.config;

import com.example.sonorus.jwt.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    
    public SecurityConfig(@Lazy JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    } //se usa para autenticar usuarios

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",                    // Página principal
                    "/index",               // Home alternativo  
                    "/login",               // Página de login
                    "/registro",            // Página de registro
                    "/css/**",              // Archivos CSS
                    "/js/**",               // Archivos JavaScript
                    "/images/**",           // Imágenes
                    "/img/**",              // Imágenes alternativas
                    "/static/**",           // Recursos estáticos
                    "/favicon.ico"         // Icono del sitio
                ).permitAll()
                .requestMatchers("/admin/**", "/admin/productos", "/nuevo").hasRole("ADMIN")
                .requestMatchers("/favoritos/**", "/carrito/**", "/checkout/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            //redirige al login si no está autenticado
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/login");
                })
            )
                //NO SE GUARDA LA SESION EN SERVIDOR, CADA PETICION DEBE TRAER SU JWT PARA VALIDARSE
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .formLogin(form -> form.disable())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .deleteCookies("jwt-token")
                .logoutSuccessUrl("/login?logout")
            );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}