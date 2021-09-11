package leszekJadacki.phonebook.user;

import leszekJadacki.phonebook.contact.Contact;
import leszekJadacki.phonebook.contact.ContactService;
import leszekJadacki.phonebook.user.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "api")
public class AppUserController {
    private final AppUserService userService;
    private final ContactService contactService;
    Logger log = Logger.getLogger(this.getClass().getSimpleName());

    @Autowired
    public AppUserController(AppUserService userService, ContactService contactService) {
        this.userService = userService;
        this.contactService = contactService;
    }

    @PostMapping(path = "save-user")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/save-user").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @PostMapping(path = "save-role")
    public ResponseEntity<Role> saveRole(@RequestBody Role role){
        return ResponseEntity.ok().body(userService.saveRole(role));
    }

    @PostMapping(path = "add-role-to-user")
    public void addRoleToUser(String userName, String roleName){
        userService.addRoleToUser(userName, roleName);
    }

    @GetMapping(path = "get-user")
    public AppUser getUser(@RequestBody String userName){
        return userService.getUser(userName);
    }

    @GetMapping(path = "get-users")
    public ResponseEntity<List<AppUser>>getUsers(){
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @PostMapping(path = "add-contact-to-user")
    public ResponseEntity<?> addContactToUser(String userName, String contactName){
        AppUser user = userService.getUser(userName);
        Contact contact = contactService.findByName(contactName);
        if (!user.getContactList().contains(contact)){
            userService.addContactToUser(user, contact);
            return ResponseEntity.ok().body(user);
        } else {
            return ResponseEntity.badRequest().body("contact already on the list.");
        }
    }
}
