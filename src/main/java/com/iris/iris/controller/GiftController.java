package com.iris.iris.controller;

import com.iris.iris.dto.GiftDTO;
import com.iris.iris.entity.Gift;
import com.iris.iris.entity.Person;
import com.iris.iris.service.PersonService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/gift")
public class GiftController {
    private final PersonService personService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Person> paging = personService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "gift";
    }

    @GetMapping("/save/{id}")
    public String setGift(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Boolean verified = (Boolean) session.getAttribute("verified");
        if (verified == null || !verified) {
            redirectAttributes.addFlashAttribute("error", "인증이 필요합니다");
            return "error";
        }
        session.removeAttribute("verified");
        Person person = personService.findById(id);
        Gift gift = personService.getGiftByPersonId(id);

        if (gift != null) {
            model.addAttribute("gift", gift);
        }
        model.addAttribute("person", person);
        return "gift_set";
    }

    @PostMapping("/save/{id}")
    public String setGift(@PathVariable Long id, GiftDTO giftDTO, RedirectAttributes redirectAttributes) {
        try {
            giftDTO.setPersonId(id);
            Gift gift = personService.getGiftByPersonId(id);
            if (gift != null) {
                personService.modifyGift(gift, giftDTO);
                redirectAttributes.addFlashAttribute("success", "수정이 완료되었습니다.");
            } else {
                personService.setGift(giftDTO);
                redirectAttributes.addFlashAttribute("success", "등록이 완료되었습니다.");
            }
            return "redirect:/gift/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "저장 중 오류가 발생했습니다.");
            return "redirect:/gift/list";
        }
    }

}
