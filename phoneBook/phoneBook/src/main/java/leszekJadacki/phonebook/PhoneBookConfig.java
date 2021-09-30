package leszekJadacki.phonebook;

import leszekJadacki.phonebook.contact.ContactRepository;
import leszekJadacki.phonebook.security.user.AppUser;
import leszekJadacki.phonebook.security.user.AppUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

@Configuration
public class PhoneBookConfig {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
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

            userService.savePermission("user:read");
            userService.savePermission("contact:read");

            userService.saveRole("USER");//, Sets.newHashSet(new AppUserPermission("user:read"))));
            userService.saveRole("ADMIN");//, Sets.newHashSet(new AppUserPermission("contact:read"))));

            userService.addPermissionToRole("USER", "user:read");

            //Creating test users
            userService.saveUser(
                    new AppUser(
                            "Leszek",
                            "LeszekLogin",
                            passwordEncoder.encode("zxcvb"),
                            true, true, true, true,
                            new ArrayList<>(),
                            new ArrayList<>()));
            userService.saveUser(
                    new AppUser(
                            "Lesio",
                            "LesioLogin",
                            "1234",
                            true, true, true, true,
                            new ArrayList<>(),
                            new ArrayList<>()));
            userService.saveUser(
                    new AppUser(
                            "Leszeczek",
                            "LeszeczekLogin",
                            "qwerty",
                            true, true, true, true,
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

