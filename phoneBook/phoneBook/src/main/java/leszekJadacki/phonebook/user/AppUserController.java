package leszekJadacki.phonebook.user;

import leszekJadacki.phonebook.contact.Contact;
import leszekJadacki.phonebook.contact.ContactController;
import leszekJadacki.phonebook.user.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

   // @CrossOrigin(allowCredentials = "true", origins = "http://127.0.0.1:59836")
    @CrossOrigin
    @PostMapping(path = "save-user")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user) {
        log.info("request came to save new user: " + user.getUserName());

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/save-user").toUriString());
        ResponseEntity<AppUser> responseEntity = ResponseEntity.created(uri).body(userService.saveUser(user));
        log.info(responseEntity.toString());

        return responseEntity;
    }

    @PostMapping(path = "save-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        return ResponseEntity.ok().body(userService.saveRole(role));
    }

    @PostMapping(path = "add-role-to-user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addRoleToUser(@RequestParam(name = "login") String login,
                              @RequestParam(name = "roleName") String roleName) {
        userService.addRoleToUser(login, roleName);
    }

    @GetMapping(path = "get-user/{login}")
    @PreAuthorize("hasAuthority('user:read')")
    public AppUser getUser(@PathVariable String login) {
        return userService.getUser(login);
    }

    //@CrossOrigin(allowCredentials = "false", origins = "http://127.0.0.1:59836", allowedHeaders = {"authorization", "content-type" , "login" ,"password"})
    @CrossOrigin
    @GetMapping(path = "get-users")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AppUser>> getUsers() {
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
                                              @RequestParam(name = "email", required = false) String email,
                                              @RequestParam(name = "photo", required = false) String photo) {
        boolean passwordCorrect = userService.userAuthentication(login, password);
        log.info("Authentication result: " + (passwordCorrect ? "positive" : "negative"));

        return passwordCorrect ?
                ResponseEntity.ok().body(contactController.filterContacts(
                        (List<Contact>) userService.getContactsOfUser(login), name, surname, phoneHome, phoneWork, email, photo)) :
                ResponseEntity.status(401).body("Authentication failed");
    }

    @CrossOrigin
    @GetMapping(path = "get-contacts")
    @PreAuthorize("hasAuthority('contact:read')")
    public ResponseEntity<?> getContactsOfUser(@RequestHeader(name = "login") String login,
                                               @RequestHeader(name = "password") String password) {

        boolean passwordCorrect = userService.userAuthentication(login, password);
        log.info("Authentication result: " + (passwordCorrect ? "positive" : "negative"));

        return passwordCorrect ? ResponseEntity.ok().body(userService.getContactsOfUser(login)) :
                ResponseEntity.status(401).body("Authentication failed");
    }

    @CrossOrigin
    @PostMapping(path = "add-contact-to-user")
    @PreAuthorize("hasAuthority('contact:write')")
    public ResponseEntity<?> addContactToUser(@RequestHeader(name = "login") String userLogin,
                                              @RequestHeader(name = "password") String password,
                                              @RequestBody Contact contact) {
        if (userService.userAuthentication(userLogin, password)) {
            AppUser user = userService.getUser(userLogin);
            if (user.getContactList().contains(contact)) {
                return ResponseEntity.badRequest().body(this.getClass().getSimpleName() + ": contact already on the list.");
            } else {
                contactController.validate(contact);
                contactController.addContact(contact);
                userService.addContactToUser(user, contact);
                return ResponseEntity.ok().body(user.getContactList());
            }
        }
        return ResponseEntity.status(401).body(this.getClass().getSimpleName() + "Authentication failed");
    }

    @CrossOrigin
    @Transactional
    @PutMapping(path = "update-contact")
    @PreAuthorize("hasAuthority('contact:write')")
    public ResponseEntity<?> updateContact(@RequestHeader(name = "login") String userLogin,
                                           @RequestHeader(name = "password") String password,
                                           @RequestParam(name = "name", required = false) String name,
                                           @RequestParam(name = "surname", required = false) String surname,
                                           @RequestParam(name = "phoneHome", required = false) String phoneHome,
                                           @RequestParam(name = "phoneWork", required = false) String phoneWork,
                                           @RequestParam(name = "email", required = false) String email,
                                           @RequestParam(name = "photo", required = false) String photo,
                                           @RequestBody Contact contact) {

        if (userService.userAuthentication(userLogin, password)) {
            List<Contact> contacts = (List<Contact>) userService.getContactsOfUser(userLogin);

            return ResponseEntity.ok()
                    .body(contactController
                            .updateContact(contacts, name, surname, phoneHome, phoneWork, email, photo, contact));
        }

        return ResponseEntity.status(401).body(this.getClass().getSimpleName() + "Authentication failed");

    }

    @CrossOrigin
    @DeleteMapping(path = "delete-contact")
    @PreAuthorize("hasAuthority('contact:write')")
    public ResponseEntity<?> deleteContact(@RequestHeader(name = "login") String login,
                                           @RequestHeader(name = "password") String password,
                                           @RequestParam(name = "name", required = false) String fName,
                                           @RequestParam(name = "surname", required = false) String lName,
                                           @RequestParam(name = "phoneHome", required = false) String phoneHome,
                                           @RequestParam(name = "phoneWork", required = false) String phoneWork,
                                           @RequestParam(name = "email", required = false) String email) {
        if (!userService.userAuthentication(login, password)) {
            return ResponseEntity.status(401).body(this.getClass().getSimpleName() + "Authentication failed");
        }
        List<Contact> contacts = (List<Contact>) userService.getUser(login).getContactList();

        return contactController.deleteContact(contacts,
                fName,
                lName,
                phoneHome,
                phoneWork,
                email);
    }
}
