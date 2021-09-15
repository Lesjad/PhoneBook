package leszekJadacki.phonebook.user;

import leszekJadacki.phonebook.contact.Contact;
import leszekJadacki.phonebook.contact.ContactController;
import leszekJadacki.phonebook.user.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "api")
public class AppUserController {
    private final AppUserService userService;
    private final ContactController contactController;
    Logger log = Logger.getLogger(this.getClass().getSimpleName());

    @Autowired
    public AppUserController(AppUserService userService, ContactController contactController) {
        this.userService = userService;
        this.contactController = contactController;
    }

    @CrossOrigin
    @PostMapping(path = "save-user")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user){
        log.info("request came to save new user: " + user.getUserName());
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/save-user").toUriString());
        ResponseEntity<AppUser> responseEntity = ResponseEntity.created(uri).body(userService.saveUser(user));
        System.out.println(responseEntity);
        return responseEntity;
    }

    @PostMapping(path = "save-role")
    public ResponseEntity<Role> saveRole(@RequestBody Role role){
        return ResponseEntity.ok().body(userService.saveRole(role));
    }

    @PostMapping(path = "add-role-to-user")
    public void addRoleToUser(@RequestParam(name = "login") String login,
                              @RequestParam(name = "roleName") String roleName){
        userService.addRoleToUser(login, roleName);
    }

    @GetMapping(path = "get-user")
    public AppUser getUser(@RequestBody String login){
        return userService.getUser(login);
    }

    @CrossOrigin
    @GetMapping(path = "get-users")
    public ResponseEntity<List<AppUser>>getUsers(){
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @CrossOrigin
    @GetMapping(path = "get-contacts/{login}")
    public Collection<Contact> getContactsOfUser(@PathVariable(required = true) String login){
        return userService.getContactsOfUser(login);
    }

    @CrossOrigin
    @PostMapping(path = "add-contact-to-user/{userLogin}")
    public ResponseEntity<?> addContactToUser(@PathVariable("userLogin") String userLogin, @RequestBody Contact contact) {
        AppUser user = userService.getUser(userLogin);
        if (user.getContactList().contains(contact)){
            return ResponseEntity.badRequest().body(this.getClass().getSimpleName()+ ": contact already on the list.");
        } else {
            contactController.validate(contact);
            contactController.addContact(contact);
            userService.addContactToUser(user, contact);
            return ResponseEntity.ok().body(user.getContactList());
        }
    }
}
