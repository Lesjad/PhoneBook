package leszekJadacki.phonebook.security.authorization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserPermissionRepository extends JpaRepository<AppUserPermission, Long> {
    Optional<AppUserPermission> findByName(String permissionName);
}
