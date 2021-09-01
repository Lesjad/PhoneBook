package leszekJadacki.phonebook.person;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PersonConfig {

    @Bean
    CommandLineRunner commandLineRunner(PersonRepository repository){
        return args -> {
            Person jan = new Person("Jan",
                    "Kowalski",
                    "22 606 60 60",
                    "+48 123 456 789",
                    "janko@domena.com");
            Person zbyszek = new Person("Zbigniew",
                    "Niedbalski",
                    "22 606 60 60",
                    "+48 123 456 789",
                    "zbinie@domena.com");
            Person zbyszekDrugi = new Person("ZbigniewDrugi",
                    "NiedbalskiBardzo",
                    "22 616 61 61",
                    "+48 987 654 321",
                    "zbinie@domena1.com");

            repository.saveAll(List.of(jan, zbyszek));
        };
    }
}
