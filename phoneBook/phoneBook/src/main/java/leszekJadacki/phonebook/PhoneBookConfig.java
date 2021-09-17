package leszekJadacki.phonebook;

import leszekJadacki.phonebook.contact.ContactRepository;
import leszekJadacki.phonebook.user.AppUser;
import leszekJadacki.phonebook.user.AppUserService;
import leszekJadacki.phonebook.user.role.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

@Configuration
public class PhoneBookConfig {
    @Bean
    CommandLineRunner runner(AppUserService userService, ContactRepository repository){
        return args -> {

//Creating test Contacts
/*            Contact jan = new Contact("Jan",
                    "Kowalski",
                    "22 606 60 60",
                    "+48 123 456 789",
                    "janko@domena.com");

            Contact zbyszek = new Contact("Zbigniew",
                    "Niedbalski",
                    "22 606 60 60",
                    "+48 123 456 789",
                    "zbinie@domena.com");

            Contact zbyszekDrugi = new Contact("ZbigniewDrugi",
                    "NiedbalskiBardzo",
                    "22 616 61 61",
                    "+48 987 654 321",
                    "zbinie@domena1.com");*/

//            repository.saveAll(List.of(jan, zbyszek, zbyszekDrugi));

            //Createing test roles
            userService.saveRole(new Role(null, "ROLE_USER"));
            userService.saveRole(new Role(null, "ROLE_MANAGER"));
            userService.saveRole(new Role(null, "ROLE_ADMIN"));

            //Creating test users
            userService.saveUser(
                    new AppUser(
                            null,
                            "Leszek",
                            "LeszekLogin",
                            "zxcvb",
                            new ArrayList<>(),
                            new ArrayList<>()));
            userService.saveUser(
                    new AppUser(
                            null,
                            "Lesio",
                            "LesioLogin",
                            "1234",
                            new ArrayList<>(),
                            new ArrayList<>()));
            userService.saveUser(
                    new AppUser(
                            null,
                            "Leszeczek",
                            "LeszeczekLogin",
                            "qwerty",
                            new ArrayList<>(),
                            new ArrayList<>()));

            //Adding contacts to users
            /*userService.addContactToUser(
                    userService.getUser("LesioLogin"),
                    repository.findByName("Jan").orElseThrow(() -> new NoSuchElementException()));
            userService.addContactToUser(
                    userService.getUser("LeszeczekLogin"),
                    repository.findByName("Jan").orElseThrow(() -> new NoSuchElementException()));*/

            /*System.out.println("Application started ... launching browser now");
            browse("http://localhost:8080/api/post-contact");*/
        };
    }
    public static void browse(String url) {
        if(Desktop.isDesktopSupported()){
            System.out.println("desktop supported");
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("desktop NOT supported");
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

