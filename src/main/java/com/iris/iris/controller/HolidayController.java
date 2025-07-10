package com.iris.iris.controller;

import com.iris.iris.dto.HolidayDTO;
import com.iris.iris.entity.Person;
import com.iris.iris.entity.Present;
import com.iris.iris.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "")  String kw) {
        Page<Person> paging = personService.getList(page, kw);
        model.addAttribute("paging", paging);
        return "holiday";
    }

    @GetMapping("/set/{id}")
    public String setHoliday(@PathVariable Long id, Model model) {
        Person person = personService.findById(id);
        model.addAttribute("presents", Present.values());
        model.addAttribute("person", person);
        return "holiday_set";
    }

    @PostMapping("/set/{id}")
    public String setHoliday(HolidayDTO holidayDTO) {
        System.out.println(holidayDTO.getReceiver());
        personService.setHoliday(holidayDTO);
        return "redirect:/holiday/list";
    }
}
