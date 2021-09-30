package leszekJadacki.phonebook.security.user;

import leszekJadacki.phonebook.contact.Contact;
import leszekJadacki.phonebook.security.authorization.AppUserRole;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    //fields for UserDetails implementation
    @Column(unique = true)
    private String login; //unique user identifier
    private String password;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    @Transient
    private Collection<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();

    //custom fields
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<AppUserRole> roles = new ArrayList<>();

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "AppUser_id")
    private Collection<Contact> contactList = new ArrayList<>();
    private String userName; //non unique user identifier (for instance his dashboard name)

    public AppUser() {
    }

    //Constructor without ID
    public AppUser(String userName,
                   String login,
                   String password,
                   boolean isAccountNonExpired,
                   boolean isAccountNonLocked,
                   boolean isCredentialsNonExpired,
                   boolean isEnabled,
                   Collection<AppUserRole> roles,
                   Collection<Contact> contactList) {
        this.userName = userName;
        this.login = login;
        this.password = password;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
        this.roles = roles;
        this.contactList = contactList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override //returns unique user identifier
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<AppUserRole> getRoles() {
        return roles;
    }

    public void setRoles(Collection<AppUserRole> roles) {
        this.roles = roles;
        setGrantedAuthorities();
    }

    public Collection<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(Collection<Contact> contactList) {
        this.contactList = contactList;
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id='" + id + '\'' +
                "userName='" + userName + '\'' +
                "login='" + login + '\'' +
                "password='" + password + '\'' +
                "roles='" + roles + '\'' +
                "}";
    }

    private void setGrantedAuthorities(){
        this.getRoles().forEach(appUserRole ->
                grantedAuthorities.addAll(appUserRole.getGrantedAuthorities()));
    }
}
