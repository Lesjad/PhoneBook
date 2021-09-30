package leszekJadacki.phonebook.security.authorization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRoleRepository extends JpaRepository<AppUserRole, Long> {
    Optional<AppUserRole> findByName(String name);
}
