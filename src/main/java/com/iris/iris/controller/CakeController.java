package com.iris.iris.controller;

import com.iris.iris.dto.CakeDTO;
import com.iris.iris.entity.*;
import com.iris.iris.service.PersonService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cake")
public class CakeController {

    private final PersonService personService;
    private static final String ADMIN_PASSWORD = "IDIS2025";
    private static final String ADMIN_SESSION_KEY = "admin_authenticated";

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Person> paging = personService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "cake";
    }

    @GetMapping("/save/{id}")
    public String setCake(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Boolean verified = (Boolean) session.getAttribute("verified");
        if (verified == null || !verified) {
            redirectAttributes.addFlashAttribute("error", "인증이 필요합니다");
            return "error";
        }
        session.removeAttribute("verified");
        Person person = personService.findById(id);
        Cake cake = personService.getCakeByPersonId(id);

        if (cake != null) {
            model.addAttribute("cake", cake);
        }
        model.addAttribute("cakeTypes", CakeType.values());
        model.addAttribute("person", person);
        return "cake_set";
    }

    @PostMapping("/save/{id}")
    public String setCake(@PathVariable Long id, CakeDTO cakeDTO, RedirectAttributes redirectAttributes) {
        try {
            cakeDTO.setPersonId(id);

            Cake cake = personService.getCakeByPersonId(id);  // 메서드명 변경
            if (cake != null) {
                personService.modifyCake(cake, cakeDTO);
                redirectAttributes.addFlashAttribute("success", "수정이 완료되었습니다.");
            } else {
                personService.setCake(cakeDTO);
                redirectAttributes.addFlashAttribute("success", "등록이 완료되었습니다.");
            }
            return "redirect:/cake/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "저장 중 오류가 발생했습니다.");
            return "redirect:/cake/list";
        }
    }
}
