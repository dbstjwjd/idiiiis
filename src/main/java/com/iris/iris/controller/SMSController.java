package com.iris.iris.controller;

import com.iris.iris.service.SMSService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class SMSController {

    private final SMSService smsService;

    @PostMapping("/sendSMS")
    @ResponseBody
    public String sendSMS(@RequestBody String phoneNum, HttpSession session) {
        String verKey = smsService.createRandomNum();
        session.setAttribute("verKey", verKey);
        //smsService.sendSms(phoneNum.replaceAll("[^0-9]", ""), verKey);
        return "인증번호가 발송되었습니다.";
    }

    @PostMapping("/checkVerKey")
    @ResponseBody
    public String checkVerKey(@RequestBody String verKey, HttpSession session) {
        String savedVerKey = (String) session.getAttribute("verKey");
        System.out.println(savedVerKey);
        if (savedVerKey != null && savedVerKey.equals(verKey.replaceAll("^\"|\"$", ""))) {
            session.removeAttribute(verKey);
            return "인증이 완료되었습니다";
        } else return "인증번호가 일치하지 않습니다.";
    }

}
