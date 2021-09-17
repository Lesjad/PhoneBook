package leszekJadacki.phonebook.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NonUniqueResultException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

    public String delete(Contact contact) {
        contactRepository.delete(contact);
        return "Success";
    }

    public Contact updateContact(List<Contact> contacts,
                                 Contact contact){
        Contact oldContact;
        if (contacts.size() > 1) {
            throw new NonUniqueResultException("found more than one contact match for change");
        } else if (contacts.size() == 0){
            throw new NoSuchElementException("Could not find contact pointed for update");
        } else {
            oldContact = contacts.get(0);
        }

        if (contact.getName()!=null && !contact.getName().equals(oldContact.getName()) && !contact.getName().isBlank())
            oldContact.setName(contact.getName());
        if (contact.getSurname()!=null && !contact.getSurname().equals(oldContact.getSurname()) && !contact.getSurname().isBlank())
            oldContact.setSurname(contact.getSurname());
        if (contact.getPhoneHome()!=null && !contact.getPhoneHome().equals(oldContact.getPhoneHome()) && !contact.getPhoneHome().isBlank())
            oldContact.setPhoneHome(contact.getPhoneHome());
        if (contact.getPhoneWork()!=null && !contact.getPhoneWork().equals(oldContact.getPhoneWork()) && !contact.getPhoneWork().isBlank())
            oldContact.setPhoneWork(contact.getPhoneWork());
        if (contact.getEmail()!=null && !contact.getEmail().equals(oldContact.getEmail()) && !contact.getEmail().isBlank())
            oldContact.setEmail(contact.getEmail());
        if (contact.getPhoto()!=null && !contact.getPhoto().equals(oldContact.getPhoto()) && !contact.getPhoto().isBlank())
            oldContact.setPhoto(contact.getPhoto());

        //TODO: further business logic to safely update contact (for instance if name already exists)
        return oldContact;
    }

    public List<Contact> filterContacts(List<Contact> contacts,
                                        String fName,
                                        String lName,
                                        String phoneHome,
                                        String phoneWork,
                                        String email,
                                        String photo) {
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
        if (phoneHome!=null && !phoneHome.isBlank()){
            contacts = contacts.stream()
                    .filter(contact -> contact.getPhoneHome().equals(lName))
                    .collect(Collectors.toList());
        }
        if (phoneWork!=null && !phoneWork.isBlank()){
            contacts = contacts.stream()
                    .filter(contact -> contact.getPhoneWork().equals(lName))
                    .collect(Collectors.toList());
        }
        if (email!=null && !email.isBlank()){
            contacts = contacts.stream()
                    .filter(contact -> contact.getEmail().equals(email))
                    .collect(Collectors.toList());
        }
        if (photo!=null && !photo.isBlank()){
            contacts = contacts.stream()
                    .filter(contact -> contact.getPhoto().equals(lName))
                    .collect(Collectors.toList());
        }
        return contacts;
    }
}
