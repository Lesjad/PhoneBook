package leszekJadacki.phonebook.user;

import leszekJadacki.phonebook.contact.Contact;
import leszekJadacki.phonebook.contact.ContactController;
import leszekJadacki.phonebook.user.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    @GetMapping(path = "search-for-contact")
    public ResponseEntity<?> searchForContact(@RequestHeader(name = "login") String login,
                                              @RequestHeader(name = "password") String password,
                                              @RequestParam(name = "name", required = false) String name,
                                              @RequestParam(name = "surname", required = false) String surname,
                                              @RequestParam(name = "phoneHome", required = false) String phoneHome,
                                              @RequestParam(name = "phoneWork", required = false) String phoneWork,
                                              @RequestParam(name = "email", required = false) String email){
        boolean passwordCorrect = userService.userAuthentication(login, password);
        log.info("Authentication result: " + (passwordCorrect? "positive" : "negative"));

        return passwordCorrect? ResponseEntity.ok().body(contactController
                .filterContacts((List<Contact>)userService.getContactsOfUser(login), name, surname, phoneHome, phoneWork, email)) :
                ResponseEntity.status(401).body("Authentication failed");
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
    @PutMapping(path = "update-contact")
    public ResponseEntity<?> updateContact(@RequestHeader(name = "login") String userLogin,
                                           @RequestHeader(name = "password") String password,
                                           @RequestParam(name = "name", required = false) String name,
                                           @RequestParam(name = "surname", required = false) String surname,
                                           @RequestParam(name = "phoneHome", required = false) String phoneHome,
                                           @RequestParam(name = "phoneWork", required = false) String phoneWork,
                                           @RequestParam(name = "email", required = false) String email,
                                           @RequestBody Contact contact){
        List<Contact> contacts = (List<Contact>) userService.getContactsOfUser(userLogin);

        return ResponseEntity.ok()
                .body(contactController
                        .updateContact(contacts, name, surname, phoneHome, phoneWork, email, contact));
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

        contacts = contactController
                .filterContacts(contacts,
                fName,
                lName,
                null,
                null,
                email,
                null);
        if (contacts.size()==0)
            return ResponseEntity.notFound().build();

        if (contacts.size()>1)
            return ResponseEntity.badRequest().body(this.getClass().getSimpleName()+": found multiple contacts. Try more precise request");

        return ResponseEntity.ok().body(contactController.deleteContact(contacts.get(0)));}
}
