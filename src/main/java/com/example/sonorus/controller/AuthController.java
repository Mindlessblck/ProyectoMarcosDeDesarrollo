package com.example.sonorus.controller;

import com.example.sonorus.model.Usuario;
import com.example.sonorus.servicio.UsuarioServicio;
import com.example.sonorus.jwt.JwtUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UsuarioServicio usuarioServicio;
    
    @Autowired
    private JwtUtil jwtUtil;

    private List<Usuario> usuarios = new ArrayList<>();
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private Long nextId = 1L;

    public AuthController() {
        // Crear admin por defecto
        Usuario admin = new Usuario();
        admin.setId(nextId++);
        admin.setNombre("Administrador");
        admin.setEmail("admin@sonorus.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRol(true); // true = admin
        usuarios.add(admin);
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario, Model model) {
        try {
            usuario.setRol(false); // Usuario normal
            usuarioServicio.registrar(usuario);
            model.addAttribute("mensaje", "Usuario registrado correctamente");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "registro";
        }
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute Usuario usuario, Model model, HttpSession session, HttpServletResponse response) {
        try {
            // Intentar autenticar usando UsuarioServicio
            Usuario autenticado = usuarioServicio.login(usuario.getEmail(), usuario.getPassword());
            
            if (autenticado != null) {

                // Generar JWT
                UserDetails userDetails = usuarioServicio.loadUserByUsername(autenticado.getEmail());
                String jwt = jwtUtil.generateToken(userDetails);
                
                // Crear cookie con JWT, todas las peticiones del usuario incluirán el JWT automáticamente
                Cookie jwtCookie = new Cookie("jwt-token", jwt);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(36000); // 10 horas
                response.addCookie(jwtCookie);
                
                // También mantener sesión para compatibilidad con templates
                session.setAttribute("usuario", autenticado);

                if (autenticado.isRol()) {
                    return "redirect:/admin/productos";
                }

                return "redirect:/";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error en el sistema de autenticación");
            return "login";
        }
        
        model.addAttribute("error", "Credenciales incorrectas");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();
        
        // Eliminar cookie JWT
        Cookie jwtCookie = new Cookie("jwt-token", null);
        jwtCookie.setMaxAge(0);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);
        
        return "redirect:/login";
    }
}
