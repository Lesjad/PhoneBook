package leszekJadacki.phonebook.security.authorization;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class AppUserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private final String name;
    @ManyToMany
    private final Collection<AppUserPermission> permissions = new ArrayList<>();

    public AppUserRole(){
        name = "default";
    }
    public AppUserRole(String name) {
        this.name = name;
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

    public Collection<AppUserPermission> getPermissions() {
        return permissions;
    }

    public void addPermission(AppUserPermission permission){
        permissions.add(permission);
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> grantedAuthorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toSet());
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + name));

        return grantedAuthorities;
    }
}
