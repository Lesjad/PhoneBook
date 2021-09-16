package leszekJadacki.phonebook.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByName(String name);

    @Query("SELECT c FROM Contact c WHERE c.name=?1")
    Optional<Contact> findByNameAndUser(String name, Long userId);
}
