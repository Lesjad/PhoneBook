package leszekJadacki.phonebook.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import javax.persistence.NonUniqueResultException;
import java.security.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());

    @Autowired
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public List<Contact> getContacts(){
        return contactRepository.findAll();
    }

    public Contact createContact(String name,
                                 String surname,
                                 String phoneHome,
                                 String phoneWork,
                                 String email,
                                 String photo){
        if (name==null || name.isBlank())
            throw new IllegalArgumentException("First name of the contact cannot be blank");
        if (surname==null || surname.isBlank())
            throw new IllegalArgumentException("Last name of the contact cannot be blank");
        if (phoneHome==null || phoneHome.isBlank())
            throw new IllegalArgumentException("Home phone of the contact cannot be blank");
        if (phoneWork==null || phoneWork.isBlank())
            throw new IllegalArgumentException("Work phone of the contact cannot be blank");
        if (email==null || email.isBlank())
            throw new IllegalArgumentException("Email of the contact cannot be blank");

        return new Contact(name, surname, phoneHome, phoneWork, email, photo);
    }
    public Contact addContact(Contact contact) {
        return contactRepository.save(contact);
    }

    public Contact findByName(String contactName) {
        return contactRepository.findByName(contactName)
                .orElseThrow(() -> new NoSuchElementException("contact " + contactName + " not found"));
    }

    public boolean validate(Contact contact) {
        if (contact.getName()==null || contact.getName().isBlank())
            throw new IllegalArgumentException("First name of the contact cannot be blank");
        if (contact.getSurname()==null || contact.getSurname().isBlank())
            throw new IllegalArgumentException("Last name of the contact cannot be blank");
        if (contact.getPhoneHome()==null || contact.getPhoneHome().isBlank())
            throw new IllegalArgumentException("Home phone of the contact cannot be blank");
        if (contact.getPhoneWork()==null || contact.getPhoneWork().isBlank())
            throw new IllegalArgumentException("Work phone of the contact cannot be blank");
        if (contact.getEmail()==null || contact.getEmail().isBlank())
            throw new IllegalArgumentException("Email of the contact cannot be blank");
        return true;
    }

    public ResponseEntity<?> delete(List<Contact> contacts, Map<String, String> params) {
        contacts = filterContacts(contacts, params);
        if (contacts.size()==0)
            return ResponseEntity.notFound().build();

        if (contacts.size()>1){
            HashMap message = new HashMap<String, String>();
            message.put("message","found multiple contacts. Try more precise request");
            return ResponseEntity.badRequest().body(message);
        }

        contactRepository.delete(contacts.get(0));
        return ResponseEntity.ok().body("contact successfully deleted");
    }

    public Contact updateContact(List<Contact> contacts,
                                 Map<String, String> newContactDetails){
        Contact contact;
        if (contacts.size() > 1) {
            throw new NonUniqueResultException("found more than one contact match for change");
        } else if (contacts.size() == 0){
            throw new NoSuchElementException("Could not find contact pointed for update");
        } else {
            contact = contacts.get(0);
        }

        contact.setName(newContactDetails.getOrDefault("name", contact.getName()));
        contact.setSurname(newContactDetails.getOrDefault("surname", contact.getSurname()));
        contact.setPhoneHome(newContactDetails.getOrDefault("phoneHome", contact.getPhoneHome()));
        contact.setPhoneWork(newContactDetails.getOrDefault("phoneWork", contact.getPhoneWork()));
        contact.setEmail(newContactDetails.getOrDefault("email", contact.getEmail()));
        contact.setPhoto(newContactDetails.getOrDefault("photo", contact.getPhoto()));

        log.info("new Contact details: " + newContactDetails);
        log.info("updated: " + contact);

        //TODO: further business logic to safely update contact (for instance if name already exists)
        return contact;
    }

    public List<Contact> filterContacts(List<Contact> contacts, Map<String, String> params) {

        contacts = contacts.stream()
                .filter(contact -> contact.getName().equals(params.getOrDefault("name", contact.getName())))
                .filter(contact -> contact.getSurname().equals(params.getOrDefault("surname", contact.getSurname())))
                .filter(contact -> contact.getPhoneHome().equals(params.getOrDefault("phoneHome", contact.getPhoneHome())))
                .filter(contact -> contact.getPhoneWork().equals(params.getOrDefault("phoneWork", contact.getPhoneWork())))
                .filter(contact -> contact.getEmail().equals(params.getOrDefault("email", contact.getEmail())))
                .filter(contact -> contact.getPhoto().equals(params.getOrDefault("photo", contact.getPhoto())))
                .collect(Collectors.toList());

        return contacts;
    }
}
