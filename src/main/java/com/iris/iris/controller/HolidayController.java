package com.iris.iris.controller;

import com.iris.iris.dto.HolidayDTO;
import com.iris.iris.entity.Person;
import com.iris.iris.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/holiday")
public class HolidayController {

    private final PersonService personService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Person> people = personService.getAll();
        model.addAttribute("people", people);
        return "holiday";
    }

    @GetMapping("/set/{id}")
    public String setHoliday(@PathVariable Long id, Model model) {
        model.addAttribute("personId", id);
        return "holiday_set";
    }

    @PostMapping("/set")
    public String setHoliday(HolidayDTO holidayDTO) {
        personService.setHoliday(holidayDTO);
        return "redirect:/holiday/list";
    }
}
