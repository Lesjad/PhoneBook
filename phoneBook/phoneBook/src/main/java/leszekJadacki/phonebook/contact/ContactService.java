package leszekJadacki.phonebook.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void addContact(Contact contact) {
        contactRepository.save(contact);
        System.out.println(contact);
    }

    public void updateContactData(Contact contact) {
        //TODO: create logic for updateContactData(Contact contact);
    }
}
