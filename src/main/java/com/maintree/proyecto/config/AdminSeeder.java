package com.maintree.proyecto.config;

import com.maintree.proyecto.dao.RolRepository;
import com.maintree.proyecto.dao.UsuarioRepository;
import com.maintree.proyecto.model.Rol;
import com.maintree.proyecto.model.Usuario;
import com.maintree.proyecto.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Collections;

@Configuration
public class AdminSeeder implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private Environment env;

    @Override
    public void run(String... args) throws Exception {
        boolean create = Boolean.parseBoolean(env.getProperty("admin.create", "false"));
        if (!create) return;

        String adminEmail = env.getProperty("admin.email", "admin@local");
        String adminPassword = env.getProperty("admin.password", "AdminPass123!");

        // Normalizar/crear rol ADMIN
        Rol adminRole = rolRepository.findByNombre("ADMIN");
        if (adminRole == null) {
            adminRole = new Rol();
            adminRole.setNombre("ADMIN");
            adminRole.setDescripcion("Rol administrador creado por AdminSeeder");
            rolRepository.save(adminRole);
            System.out.println("AdminSeeder: rol ADMIN creado");
        }

        // Crear usuario admin si no existe
        Usuario existing = usuarioRepository.findByEmail(adminEmail);
        if (existing == null) {
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setApellido("Root");
            admin.setEmail(adminEmail);
            admin.setPassword(PasswordHasher.hashPassword(adminPassword));
            admin.setActive(Boolean.TRUE);
            admin.setRoles(Collections.singleton(adminRole));
            usuarioRepository.save(admin);
            System.out.println("AdminSeeder: usuario admin creado -> " + adminEmail);
        } else {
            // Ensure role and active
            existing.getRoles().add(adminRole);
            existing.setActive(Boolean.TRUE);
            usuarioRepository.save(existing);
            System.out.println("AdminSeeder: usuario admin ya existía, se aseguró rol y estado activo");
        }
    }
}
