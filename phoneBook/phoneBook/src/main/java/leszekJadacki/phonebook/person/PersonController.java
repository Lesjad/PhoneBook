package leszekJadacki.phonebook.person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/persons")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public void postPerson(@RequestBody Person person){
        personService.postPerson(person);
    }
    @GetMapping(path = "all")
    public List<Person> getPersons(){
        return personService.getPersons();
    }

}
