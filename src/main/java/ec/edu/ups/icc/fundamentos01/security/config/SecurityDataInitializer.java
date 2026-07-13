package ec.edu.ups.icc.fundamentos01.security.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ec.edu.ups.icc.fundamentos01.security.entities.RoleEntity;
import ec.edu.ups.icc.fundamentos01.security.enums.RoleName;
import ec.edu.ups.icc.fundamentos01.security.repositories.RoleRepository;

/*
 * Inicializador de datos de seguridad.
 *
 * Inserta los roles básicos si no existen.
 */
@Configuration
public class SecurityDataInitializer {

    @Bean
    CommandLineRunner initRoles(
            RoleRepository roleRepository
    ) {

        return args -> {

            if (!roleRepository.existsByName(RoleName.ROLE_USER)) {

                RoleEntity userRole =
                        new RoleEntity(
                                RoleName.ROLE_USER,
                                RoleName.ROLE_USER.getDescription()
                        );

                roleRepository.save(
                        userRole
                );
            }

            if (!roleRepository.existsByName(RoleName.ROLE_ADMIN)) {

                RoleEntity adminRole =
                        new RoleEntity(
                                RoleName.ROLE_ADMIN,
                                RoleName.ROLE_ADMIN.getDescription()
                        );

                roleRepository.save(
                        adminRole
                );
            }
        };
    }
}