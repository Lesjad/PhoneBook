package leszekJadacki.phonebook.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping(path = "api/contacts")
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public void addContact(@RequestBody Contact contact){
        contactService.addContact(contact);
    }
    @GetMapping(path = "all")
    public List<Contact> getContacts(){
        return contactService.getContacts();
    }
    @GetMapping
    public String welcomePage(){
        return "Welcome to the show!";
    }

    @Transactional
    public void updateContactData(Contact contact){
        contactService.updateContactData(contact);
    }

}