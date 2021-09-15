package leszekJadacki.phonebook.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ContactService {
    private ContactRepository contactRepository;

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
}
