package com.iris.iris.controller;

import com.iris.iris.dto.HolidayDTO;
import com.iris.iris.entity.Holiday;
import com.iris.iris.entity.Person;
import com.iris.iris.entity.Present;
import com.iris.iris.service.PersonService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("kw", kw);
        return "holiday";
    }

    @GetMapping("/save/{id}")
    public String setHoliday(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Boolean verified = (Boolean) session.getAttribute("verified");
        if (verified == null || !verified) {
            redirectAttributes.addFlashAttribute("error", "인증이 필요합니다");
            return "error";
        }
        session.removeAttribute("verified");
        Person person = personService.findById(id);

        Holiday holiday = personService.getHolidayById(id);
        if (holiday != null) {
            model.addAttribute("holiday", holiday);
        }
        model.addAttribute("presents", Present.values());
        model.addAttribute("person", person);
        return "holiday_set";
    }

    @PostMapping("/save/{id}")
    public String setHoliday(@PathVariable Long id, HolidayDTO holidayDTO) {
        Holiday holiday = personService.getHolidayById(id);
        if (holiday != null) {
            personService.modifyHoliday(holiday, holidayDTO);
            return "redirect:/holiday/list";
        } else {
            personService.setHoliday(holidayDTO);
            return "redirect:/holiday/list";
        }
    }
}
