package leszekJadacki.phonebook.user;

import leszekJadacki.phonebook.contact.Contact;
import leszekJadacki.phonebook.user.role.Role;
import leszekJadacki.phonebook.user.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@Transactional
public class AppUserService {

    private Logger log = Logger.getLogger(this.getClass().getName());
    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AppUserService(AppUserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public AppUser saveUser(AppUser user) {
        log.info("checking if user with login: " + user.getLogin() + "exist in database");
        Optional<AppUser> optionalAppUser = userRepository.findByLogin(user.getLogin());
        if (optionalAppUser.isPresent()) {
            throw new IllegalStateException("login taken");
        } else if (validate(user)) {
            log.info("Saving user " + user.getUserName() + " to the database");
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("given data is invalid");
        }
    }

    private boolean validate(AppUser user) {
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new IllegalArgumentException("Login cannot be blank");
        }
        if (user.getUserName()==null || user.getUserName().isBlank()) {
            throw new IllegalArgumentException("User name cannot be blank");
        }
        if (user.getPassword()==null || user.getPassword().isBlank()){
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return true;
    }

    public Role saveRole(Role role) {
        log.info("checking if role with name: " + role.getName() + "exist in database");
        Optional<Role> optionalRole = roleRepository.findByName(role.getName());
        if (optionalRole.isPresent()) {
            throw new IllegalStateException("Role already exist");
        } else {
            log.info("Saving role " + role.getName() + " to the database");
            return roleRepository.save(role);
        }
    }

    public void addRoleToUser(String login, String roleName) {
        AppUser user = userRepository.findByLogin(login).
                orElseThrow(() -> new NoSuchElementException("user "+ login + " could not be found"));
        Role role = roleRepository.findByName(roleName).
                orElseThrow(() -> new NoSuchElementException("role " + roleName + " could not be found"));

        log.info("Adding role " + roleName + " to the user " + user.getUserName());
        user.getRoles().add(role);
    }

    public AppUser getUser(String login) {
        log.info("Fetching user " + login + " from the database");
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new NoSuchElementException("User with login: " + login + " does not exist in database"));
    }

    public List<AppUser> getUsers() {
        log.info("Getting all users");
        return userRepository.findAll();
    }

    public Collection<Contact> getContactsOfUser(String login){
        log.info("reading contacts of user: "+ login);
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new NoSuchElementException("User with login "+login + " could not be found in database"))
                .getContactList();
    }

    public AppUser addContactToUser(AppUser user, Contact contact) {
        userRepository.findByLogin(user.getLogin())
                .orElseThrow(() -> new NoSuchElementException("User not found"))
                .getContactList().add(contact);
        return user;
    }

    @Transactional
    public void updateContactData(String userLogin, Contact oldContact, Contact newContact) {
        AppUser user = userRepository.findByLogin(userLogin).orElseThrow(() -> new NoSuchElementException("user not found"));

        if (user.getContactList().contains(oldContact)){
            user.getContactList().remove(oldContact);
            user.getContactList().add(newContact);
        }
    }
}
