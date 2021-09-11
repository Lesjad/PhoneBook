package leszekJadacki.phonebook.user;

import leszekJadacki.phonebook.contact.Contact;
import leszekJadacki.phonebook.user.role.Role;
import leszekJadacki.phonebook.user.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public AppUser saveUser(AppUser user){
        log.info("Saving user " + user.getUserName() + " to the database");
        return userRepository.save(user);
    }

    public Role saveRole(Role role){
        log.info("Saving role " + role.getName() + " to the database");
        return roleRepository.save(role);
    }

    public void addRoleToUser(String userName, String roleName){
        log.info("Adding role " + roleName + " to the user " + userName);
        AppUser user = userRepository.findByUserName(userName);
        Role role=roleRepository.findByName(roleName);
        user.getRoles().add(role);
    }

    public AppUser getUser(String userName){
        log.info("Fetching user " + userName + " from the database");
        return userRepository.findByUserName(userName);
    }

    public List<AppUser> getUsers(){
        log.info("Getting all users");
        return userRepository.findAll();
    }

    public AppUser addContactToUser(AppUser user, Contact contact) {
        userRepository.findByUserName(user.getUserName())
                .getContactList().add(contact);
        return user;
    }
}
