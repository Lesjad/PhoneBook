package leszekJadacki.phonebook.contact;

import javax.persistence.*;

@Entity
@Table
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false)
    private String phoneHome;
    @Column(nullable = false)
    private String phoneWork;
    @Column(nullable = false)
    private String email;
    private String photo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contact() {
    }

    public Contact(String name,
                   String surname,
                   String phoneHome,
                   String phoneWork,
                   String email,
                   String photo) {
        this.name = name;
        this.surname = surname;
        this.phoneHome = phoneHome;
        this.phoneWork = phoneWork;
        this.email = email;
        this.photo = photo;
    }

    public Contact(String name,
                   String surname,
                   String phoneHome,
                   String phoneWork,
                   String email) {
        this.name = name;
        this.surname = surname;
        this.phoneHome = phoneHome;
        this.phoneWork = phoneWork;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", phoneHome='" + phoneHome + '\'' +
                ", phoneWork='" + phoneWork + '\'' +
                ", email='" + email + '\'' +
                ", photo=" + photo +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        Contact obj = (Contact) object;
        return (this.name.equals(obj.name) &&
                this.surname.equals(obj.surname) &&
                this.phoneHome.equals(obj.phoneHome) &&
                this.phoneWork.equals(obj.phoneWork) &&
                this.email.equals(obj.email));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneHome() {
        return phoneHome;
    }

    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    public String getPhoneWork() {
        return phoneWork;
    }

    public void setPhoneWork(String phoneWork) {
        this.phoneWork = phoneWork;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
