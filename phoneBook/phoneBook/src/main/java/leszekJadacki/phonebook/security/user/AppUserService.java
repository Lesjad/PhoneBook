package leszekJadacki.phonebook.security.user;

import leszekJadacki.phonebook.contact.Contact;
import leszekJadacki.phonebook.security.authorization.AppUserPermission;
import leszekJadacki.phonebook.security.authorization.AppUserPermissionRepository;
import leszekJadacki.phonebook.security.authorization.AppUserRole;
import leszekJadacki.phonebook.security.authorization.AppUserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

@Service
@Transactional
public class AppUserService implements UserDetailsService {

    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final AppUserRepository userRepository;
    private final AppUserRoleRepository roleRepository;
    private final AppUserPermissionRepository permissionRepository;

    @Autowired
    public AppUserService(AppUserRepository userRepository,
                          AppUserRoleRepository roleRepository,
                          AppUserPermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public AppUser saveUser(AppUser user) {
        log.info(String.format("checking if user \"%s\" exist in database", user.getLogin()));

        userRepository.findUserByLogin(user.getLogin()).ifPresentOrElse(optionalUser -> {
            throw new IllegalArgumentException(String.format("login \"%s\" already taken", user.getLogin()));
        }, () -> log.info(String.format("Saving user \"%s\" to the database", user.getUserName())));

        return validate(user)? userRepository.save(user) : null;
    }

    private boolean validate(AppUser user) {
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new IllegalArgumentException("Login cannot be blank");
        }
        if (user.getUserName() == null || user.getUserName().isBlank()) {
            throw new IllegalArgumentException("User name cannot be blank");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return true;
    }

    public AppUserPermission savePermission(String permissionName) {
        log.info(String.format("checking if permission: %s exist in database", permissionName));

        permissionRepository.findByName(permissionName).ifPresentOrElse(permission -> {
                    throw new IllegalArgumentException(String.format("Permission %s already exist", permissionName));
                }, () -> log.info(String.format("Saving permission \"%s\" to the database", permissionName))
        );
        return permissionRepository.save(new AppUserPermission(permissionName));
    }

    public AppUserRole saveRole(String roleName) {
        log.info(String.format("checking if role: %s exist in database", roleName));

        roleRepository.findByName(roleName).ifPresentOrElse(role -> {
                    throw new IllegalStateException(String.format("Role \"%s\" already exist", roleName));
                }, () -> log.info(String.format("Saving role \"%s\" to the database", roleName))
        );
        return roleRepository.save(new AppUserRole(roleName));
    }


    public void addRoleToUser(String login, String roleName) {
        AppUser user = userRepository.findUserByLogin(login).
                orElseThrow(() -> new NoSuchElementException("user " + login + " could not be found"));
        AppUserRole role = roleRepository.findByName(roleName).
                orElseThrow(() -> new NoSuchElementException("role " + roleName + " could not be found"));

        log.info("Adding role " + roleName + " to the user " + user.getUserName());
        user.getRoles().add(role);
    }

    public AppUser getUser(String login) {
        log.info("Fetching user " + login + " from the database");
        return userRepository.findUserByLogin(login)
                .orElseThrow(() -> new NoSuchElementException("User with login: " + login + " does not exist in database"));
    }

    public List<AppUser> getUsers() {
        log.info("Getting all users");
        return userRepository.findAll();
    }

    public Collection<Contact> getContactsOfUser(String login) {
        log.info("reading contacts of user: " + login);
        return userRepository.findUserByLogin(login)
                .orElseThrow(() -> new NoSuchElementException("User with login " + login + " could not be found in database"))
                .getContactList();
    }

    public AppUser addContactToUser(AppUser user, Contact contact) {
        userRepository.findUserByLogin(user.getLogin())
                .orElseThrow(() -> new NoSuchElementException("Authentication failed. User not found"))
                .getContactList().add(contact);
        return user;
    }

    public boolean userAuthentication(String login, String password) {
        AppUser user = userRepository.findUserByLogin(login).orElseThrow(() -> new NoSuchElementException("Authentication failed. User not found"));
        return user.getPassword().equals(password);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findUserByLogin(login).orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", login)));
    }

    public void addPermissionToRole(String roleName, String permissionName) {
        roleRepository.findByName(roleName)
                .orElseThrow(() -> new NoSuchElementException(String.format("Role %s not found", roleName)))
                .addPermission(permissionRepository.findByName(permissionName)
                        .orElseThrow(() -> new NoSuchElementException(String.format("Permission %s not found", permissionName))));
    }
}
