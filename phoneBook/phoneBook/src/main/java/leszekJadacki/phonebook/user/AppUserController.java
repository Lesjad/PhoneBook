package leszekJadacki.phonebook.user;

import leszekJadacki.phonebook.contact.Contact;
import leszekJadacki.phonebook.contact.ContactController;
import leszekJadacki.phonebook.user.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    @GetMapping(path = "get-contacts")
    public ResponseEntity<?> getContactsOfUser(@RequestHeader(name = "login") String login,
                                               @RequestHeader(name = "password") String password){

        boolean passwordCorrect = userService.userAuthentication(login, password);
        log.info("Authentication result: " + (passwordCorrect? "positive" : "negative"));

        return passwordCorrect? ResponseEntity.ok().body(userService.getContactsOfUser(login)) :
                ResponseEntity.status(401).body("Authentication failed");
    }

    @CrossOrigin
    @PostMapping(path = "add-contact-to-user")
    public ResponseEntity<?> addContactToUser(@RequestHeader(name = "login") String userLogin,
                                              @RequestHeader(name = "password") String password,
                                              @RequestBody Contact contact) {
        if (userService.userAuthentication(userLogin, password)) {
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
        return ResponseEntity.status(401).body(this.getClass().getSimpleName()+ "Authentication failed");
    }

    @CrossOrigin
    @Transactional
    @PutMapping(path = "update-contact/{contact-name}")
    public ResponseEntity<?> updateContact(@RequestHeader(name = "login") String userLogin,
                                           @RequestHeader(name = "password") String password,
                                           @PathVariable(name = "contact-name") String oldName,
                                           @RequestBody Contact contact){
        Long userId = userService.getUser(userLogin).getId();
        contactController.updateContact(
                userId,
                oldName,
                contact);
        return ResponseEntity.ok().body("success?");
    }

    @CrossOrigin
    @DeleteMapping(path = "delete-contact")
    public ResponseEntity<?> deleteContact(@RequestHeader(name = "login") String login,
                                           @RequestHeader(name = "password") String password,
                                           @RequestParam(name = "name", required = false) String fName,
                                           @RequestParam(name = "surname", required = false) String lName,
                                           @RequestParam(name = "email", required = false) String email){
        if (!userService.userAuthentication(login, password)) {
            return ResponseEntity.status(401).body(this.getClass().getSimpleName()+ "Authentication failed");
        }
        List<Contact> contacts = (List<Contact>) userService.getUser(login).getContactList();

        if (fName!=null && !fName.isBlank()){
            contacts = contacts.stream()
                    .filter(contact -> contact.getName().equals(fName))
                    .collect(Collectors.toList());
        }
        if (lName!=null && !lName.isBlank()){
            contacts = contacts.stream()
                    .filter(contact -> contact.getSurname().equals(lName))
                    .collect(Collectors.toList());
        }
        if (email!=null && !email.isBlank()){
            contacts = contacts.stream()
                    .filter(contact -> contact.getEmail().equals(email))
                    .collect(Collectors.toList());
        }

        log.info("after filtration: " + contacts);
        if (contacts.size()==0)
            return ResponseEntity.notFound().build();

        if (contacts.size()>1)
            return ResponseEntity.badRequest().body(this.getClass().getSimpleName()+": found multiple contacts. Try more precise request");

        return ResponseEntity.ok().body(contactController.deleteContact(contacts.get(0)));
    }
}
