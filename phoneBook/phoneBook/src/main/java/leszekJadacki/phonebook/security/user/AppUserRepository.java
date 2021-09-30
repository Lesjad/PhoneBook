package leszekJadacki.phonebook.security.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("postgres")
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findUserByLogin(String login);
}
