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
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "") String kw) {
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
        Holiday holiday = personService.getHolidayByPersonId(id);  // 메서드명 변경

        if (holiday != null) {
            model.addAttribute("holiday", holiday);
        }
        model.addAttribute("presents", Present.values());
        model.addAttribute("person", person);
        return "holiday_set";
    }

    @PostMapping("/save/{id}")
    public String setHoliday(@PathVariable Long id, HolidayDTO holidayDTO, RedirectAttributes redirectAttributes) {
        try {
            holidayDTO.setPersonId(id);

            Holiday holiday = personService.getHolidayByPersonId(id);  // 메서드명 변경
            if (holiday != null) {
                personService.modifyHoliday(holiday, holidayDTO);
                redirectAttributes.addFlashAttribute("success", "수정이 완료되었습니다.");
            } else {
                personService.setHoliday(holidayDTO);
                redirectAttributes.addFlashAttribute("success", "등록이 완료되었습니다.");
            }
            return "redirect:/holiday/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "저장 중 오류가 발생했습니다.");
            return "redirect:/holiday/list";
        }
    }
}
