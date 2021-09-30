package leszekJadacki.phonebook.security.authorization;

import javax.persistence.*;

@Entity
public class AppUserPermission {
//    USER_READ("user:read"),
//    USER_WRITE("user:write"),
//    CONTACT_READ("contact:read"),
//    CONTACT_WRITE("contact:write");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private final String name;

    public AppUserPermission(){
        name = "default";
    }
    public AppUserPermission(String permission) {
        this.name = permission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
}
