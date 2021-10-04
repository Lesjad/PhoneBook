package leszekJadacki.phonebook;

import leszekJadacki.phonebook.contact.ContactRepository;
import leszekJadacki.phonebook.security.authorization.AppUserRoleRepository;
import leszekJadacki.phonebook.security.user.AppUser;
import leszekJadacki.phonebook.security.user.AppUserRepository;
import leszekJadacki.phonebook.security.user.AppUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.management.relation.RoleNotFoundException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;

@Configuration
public class PhoneBookConfig {

    Logger log = Logger.getLogger(this.getClass().getSimpleName());

    @Bean
    CommandLineRunner runner(AppUserService userService,
                             AppUserRepository userRepository,
                             PasswordEncoder passwordEncoder,
                             ContactRepository contactRepository,
                             AppUserRoleRepository roleRepository) {
        return args -> {

            //Createing test roles

            userService.savePermission("user:read");
            userService.savePermission("contact:read");
            userService.savePermission("contact:write");

            userService.saveRole("USER");
            userService.saveRole("ADMIN");

            userService.addPermissionToRole("USER", "user:read");
            userService.addPermissionToRole("USER", "contact:read");
            userService.addPermissionToRole("USER", "contact:write");

            //Creating test users
            userService.saveUser(
                    new AppUser(
                            "Leszek",
                            "LeszekLogin",
                            passwordEncoder.encode("zxcvb"),
                            true, true, true, true,
                            Set.of(roleRepository.findByName("USER").orElseThrow(() -> new RoleNotFoundException(String.format("could not find role: %s", "USER")))),
                            new ArrayList<>()));
            userService.saveUser(
                    new AppUser(
                            "Lesio",
                            "LesioLogin",
                            "1234",
                            true, true, true, true,
                            Set.of(),
                            new ArrayList<>()));
            userService.saveUser(
                    new AppUser(
                            "Leszeczek",
                            "LeszeczekLogin",
                            "qwerty",
                            true, true, true, true,
                            Set.of(),
                            new ArrayList<>()));

            String testlogin = "LeszekLogin";
            userRepository.findUserByLogin(testlogin).ifPresentOrElse(appUser -> {
                log.info(String.format("Authorities of user \"%s\": %s", testlogin, appUser.getAuthorities().toString()));
            }, () -> {
                log.info(String.format("could not identify user %s", testlogin));
            });
        };
    }
}

