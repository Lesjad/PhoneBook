package leszekJadacki.phonebook.security.user;

import leszekJadacki.phonebook.AppResponseBody;
import leszekJadacki.phonebook.contact.Contact;
import leszekJadacki.phonebook.contact.ContactController;
import leszekJadacki.phonebook.security.authorization.AppUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
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
//TODO: customResponseBody <- apply to all ResponseEntities
    // @CrossOrigin(allowCredentials = "true", origins = "http://127.0.0.1:59836")
    @CrossOrigin
    @PostMapping(path = "user")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user) {
        log.info("saving new user...: " + user.getUserName());

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/user").toUriString());
        ResponseEntity<AppUser> responseEntity = ResponseEntity.created(uri).body(userService.saveUser(user));
        log.info(responseEntity.toString());

        return responseEntity;
    }

    @PostMapping(path = "role")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<AppUserRole> saveRole(@RequestBody String roleName) {
        return ResponseEntity.ok().body(userService.saveRole(roleName));
    }

    @PostMapping(path = "user/role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addRoleToUser(@RequestParam(name = "login") String login,
                              @RequestParam(name = "roleName") String roleName) {
        userService.addRoleToUser(login, roleName);
    }

    @GetMapping(path = "user/{login}")
    @PreAuthorize("hasAuthority('user:read')")
    public AppUser getUser(@PathVariable String login) {
        return userService.getUser(login);
    }

    //@CrossOrigin(allowCredentials = "false", origins = "http://127.0.0.1:59836", allowedHeaders = {"authorization", "content-type" , "login" ,"password"})
    @CrossOrigin
    @GetMapping(path = "users")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }


    @CrossOrigin
    @GetMapping(path = "contact")
    public ResponseEntity<?> searchForContact(@RequestHeader(name = "login") String login,
                                              @RequestHeader(name = "password") String password,
                                              @RequestParam Map<String, String> params) {
        boolean passwordCorrect = userService.userAuthentication(login, password);
        log.info("Authentication result: " + (passwordCorrect ? "positive" : "negative"));

        return passwordCorrect ?
                ResponseEntity.ok()
                        .body(contactController.filterContacts(
                                (List<Contact>) userService.getContactsOfUser(login),
                                params)) :
                ResponseEntity.status(401).body("Authentication failed");
    }

    @CrossOrigin
    @GetMapping(path = "contacts")
    @PreAuthorize("hasAuthority('contact:read')")
    public ResponseEntity<?> getContactsOfUser(@RequestHeader(name = "login") String login,
                                               @RequestHeader(name = "password") String password) {

        boolean passwordCorrect = userService.userAuthentication(login, password);
        log.info("Authentication result: " + (passwordCorrect ? "positive" : "negative"));

        return passwordCorrect ? ResponseEntity.ok().body(userService.getContactsOfUser(login)) :
                ResponseEntity.status(401).body("Authentication failed");
    }

    @CrossOrigin
    @PostMapping(path = "user/contact", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('contact:write')")
    public ResponseEntity<?> addContactToUser(@RequestHeader(name = "login") String userLogin,
                                              @RequestHeader(name = "password") String password,
                                              @RequestBody Contact contact) {
        if (userService.userAuthentication(userLogin, password)) {
            AppUser user = userService.getUser(userLogin);
            if (user.getContactList().contains(contact)) {
                return ResponseEntity.badRequest().body(new AppResponseBody
                        .AppResponseBodyBuilder("contact already on the list")
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build());
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
    @PutMapping(path = "contact")
    @PreAuthorize("hasAuthority('contact:write')")
    public ResponseEntity<?> updateContact(@RequestHeader(name = "login") String userLogin,
                                           @RequestHeader(name = "password") String password,
                                           @RequestParam Map<String, String> searchForUpdate,
                                           @RequestBody Map<String, String> newContactDetails) {

        if (userService.userAuthentication(userLogin, password)) {
            List<Contact> contacts = (List<Contact>) userService.getContactsOfUser(userLogin);

            return ResponseEntity.ok()
                    .body(contactController
                            .updateContact(contacts, searchForUpdate, newContactDetails));
        }

        return ResponseEntity.status(401).body(this.getClass().getSimpleName() + "Authentication failed");

    }

    @CrossOrigin
    @DeleteMapping(path = "contact")
    @PreAuthorize("hasAuthority('contact:write')")
    public ResponseEntity<?> deleteContact(@RequestHeader(name = "login") String login,
                                           @RequestHeader(name = "password") String password,
                                           @RequestParam Map<String, String> params) {
        if (!userService.userAuthentication(login, password)) {
            return ResponseEntity.status(401).body(this.getClass().getSimpleName() + "Authentication failed");
        }
        List<Contact> contacts = (List<Contact>) userService.getUser(login).getContactList();

        return contactController.deleteContact(contacts, params);
    }
}
