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

        userRepository.findUserByLogin(user.getLogin()).ifPresentOrElse(optionalUser -> {
            log.info(String.format("user \"%s\" already exist in database", user.getLogin()));
            throw new IllegalArgumentException(String.format("login \"%s\" already taken", user.getLogin()));
        }, () -> log.info(String.format("Saving user \"%s\" to the database", user.getUserName())));

        return validate(user) ? userRepository.save(user) : null;
    }

    private boolean validate(AppUser user) {
        if (user.getLogin() == null || user.getLogin().isBlank())
            throw new IllegalArgumentException("Login cannot be blank");
        if (user.getUserName() == null || user.getUserName().isBlank())
            throw new IllegalArgumentException("User name cannot be blank");
        if (user.getPassword() == null || user.getPassword().isBlank())
            throw new IllegalArgumentException("Password cannot be empty");

        return true;
    }

    public AppUserPermission savePermission(String permissionName) {

        permissionRepository.findByName(permissionName).ifPresentOrElse(permission -> {
                    log.info(String.format("permission: \"%s\" already exist in database", permissionName));
                    throw new IllegalArgumentException(String.format("Permission %s already exist", permissionName));
                }, () -> log.info(String.format("Saving permission \"%s\" to the database", permissionName))
        );
        return permissionRepository.save(new AppUserPermission(permissionName));
    }

    public AppUserRole saveRole(String roleName) {

        roleRepository.findByName(roleName).ifPresentOrElse(role -> {
            log.info(String.format("role: \"%s\" exist in database", roleName));
                    throw new IllegalStateException(String.format("Role \"%s\" already exist", roleName));
                }, () -> log.info(String.format("Saving role \"%s\" to the database", roleName))
        );
        return roleRepository.save(new AppUserRole(roleName));
    }


    public void addRoleToUser(String login, String roleName) {
        AppUser user = userRepository.findUserByLogin(login).
                orElseThrow(() -> new NoSuchElementException(String.format("user \"%s\" could not be found", login)));
        AppUserRole role = roleRepository.findByName(roleName).
                orElseThrow(() -> new NoSuchElementException(String.format("role \"%s\" could not be found", roleName)));

        log.info(String.format("Adding role \"%s\" to user \"%s\"", roleName, user.getLogin()));
        user.getRoles().add(role);
    }

    public AppUser getUser(String login) {
        log.info(String.format("Fetching user %s from the database", login));
        return userRepository.findUserByLogin(login)
                .orElseThrow(() -> new NoSuchElementException(String.format("User with login \"%s\" does not exist in database", login)));
    }

    public List<AppUser> getUsers() {
        return userRepository.findAll();
    }

    public Collection<Contact> getContactsOfUser(String login) {
        log.info(String.format("reading contacts of user \"%s\"", login));
        return userRepository.findUserByLogin(login)
                .orElseThrow(() -> new NoSuchElementException(String.format("User with login \"%s\" could not be found in database", login)))
                .getContactList();
    }

    public AppUser addContactToUser(AppUser user, Contact contact) {
        userRepository.findUserByLogin(user.getLogin())
                .orElseThrow(() -> new NoSuchElementException("User not found"))
                .getContactList().add(contact);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findUserByLogin(login).orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", login)));
    }

    public void addPermissionToRole(String roleName, String permissionName) {
        log.info(String.format("saving permission: \"%s\" to role: \"%s\"", permissionName, roleName));
        roleRepository.findByName(roleName)
                .orElseThrow(() -> new NoSuchElementException(String.format("Role \"%s\" not found", roleName)))
                .addPermission(permissionRepository.findByName(permissionName)
                        .orElseThrow(() -> new NoSuchElementException(String.format("Permission %s not found", permissionName))));
    }
}
