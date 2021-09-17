package leszekJadacki.phonebook.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class ContactController {

    private final ContactService contactService;
    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

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

    public List<Contact> getContacts() {
        return contactService.getContacts();
    }

    public boolean validate(Contact contact) {
        return contactService.validate(contact);
    }

    public ResponseEntity<?> deleteContact(Contact contact) {
        return ResponseEntity.ok().body(contactService.delete(contact));
    }

    public List<Contact> filterContacts(List<Contact> contacts,
                                        String fName,
                                        String lName,
                                        String phoneHome,
                                        String phoneWork,
                                        String email,
                                        String photo){
        return contactService.filterContacts(contacts, fName, lName, phoneHome, phoneWork, email, photo);
    }
    public List<Contact> filterContacts(List<Contact> contacts,
                                 String fName,
                                 String lName,
                                 String phoneHome,
                                 String phoneWork,
                                 String email){
        return filterContacts(contacts, fName, lName, phoneHome, phoneWork, email, null);
    }

    public Contact updateContact(List<Contact> contacts, String name, String surname, String phoneHome, String phoneWork, String email, Contact contact) {
        return contactService.updateContact(filterContacts(contacts, name, surname, phoneHome, phoneWork, email), contact);
    }
}
