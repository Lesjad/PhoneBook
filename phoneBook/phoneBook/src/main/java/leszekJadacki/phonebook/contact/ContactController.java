package leszekJadacki.phonebook.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "api")
public class ContactController {

    private final ContactService contactService;
    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping(path = "create-contact")
    public Contact createContact(@RequestParam(name = "name") String name,
                                   @RequestParam(name = "surname") String surname,
                                   @RequestParam(name = "phoneHome") String phoneHome,
                                   @RequestParam(name = "phoneWork") String phoneWork,
                                   @RequestParam(name = "email") String email,
                                   @RequestParam(name = "photo") String photo) {

        return contactService.createContact(name, surname, phoneHome, phoneWork, email, photo);
    }

    public ResponseEntity<Contact> addContact(Contact contact){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/post-contact").toUriString());
        return ResponseEntity.created(uri).body(contactService.addContact(contact));
    }

    @GetMapping(path = "get-contacts")
    public List<Contact> getContacts() {
        return contactService.getContacts();
    }

    public boolean validate(Contact contact) {
        return contactService.validate(contact);
    }
}
