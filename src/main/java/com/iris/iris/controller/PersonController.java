package com.iris.iris.controller;

import com.iris.iris.dto.PersonDTO;
import com.iris.iris.entity.Person;
import com.iris.iris.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping("")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Person> paging = personService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "admin";
    }

    @PostMapping("/add")
    public String addPerson(PersonDTO personDTO) {
        personService.addPerson(personDTO);
        return "redirect:/admin";
    }

    @GetMapping("/delete/{id}")
    public String deletePerson(@PathVariable Long id) {
        Person person = personService.findById(id);
        personService.deletePerson(person);
        return "redirect:/admin";
    }
}
