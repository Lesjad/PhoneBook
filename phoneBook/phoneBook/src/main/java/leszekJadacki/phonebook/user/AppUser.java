package leszekJadacki.phonebook.user;

import leszekJadacki.phonebook.contact.Contact;
import leszekJadacki.phonebook.user.role.Role;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private String userName;
    private String login;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "AppUser_id")
    private Collection<Contact> contactList = new ArrayList<>();

    public AppUser() {
    }
//Constructor without ID
    public AppUser(String userName,
                   String login,
                   String password,
                   Collection<Role> roles,
                   Collection<Contact> contactList) {
        this.userName = userName;
        this.login = login;
        this.password = password;
        this.roles = roles;
        this.contactList = contactList;
    }
//Constructor with all fields
    public AppUser(Long id,
                   String userName,
                   String login,
                   String password,
                   Collection<Role> roles,
                   Collection<Contact> contactList) {
        this.id = id;
        this.userName = userName;
        this.login = login;
        this.password = password;
        this.roles = roles;
        this.contactList = contactList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public Collection<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(Collection<Contact> contactList) {
        this.contactList = contactList;
    }

    @Override
    public String toString() {
        return "AppUser{"+
                "id='"+id+'\''+
                "userName='"+ userName+ '\''+
                "login='"+ login +'\''+
                "password='"+password+'\''+
                "roles='"+roles+'\''+
                "}";
    }
}
