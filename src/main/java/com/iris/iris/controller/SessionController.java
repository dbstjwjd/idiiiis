package com.iris.iris.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class SessionController {

    @PostMapping("/giveSession")
    @ResponseBody
    public String giveSession(@RequestBody Map<String, Object> request, HttpSession session) {
        try {
            Boolean verified = (Boolean) request.get("verified");
            if (verified != null && verified) {
                session.setAttribute("verified", true);
                return "인증이 완료되었습니다.";
            } else {
                return "인증 처리에 실패했습니다.";
            }
        } catch (Exception e) {
            return "인증 처리 중 오류가 발생했습니다.";
        }
    }
}
