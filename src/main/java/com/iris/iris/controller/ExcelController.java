package com.iris.iris.controller;

import com.iris.iris.service.PersonService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Controller
public class ExcelController {
    private final PersonService personService;
    private static final String ADMIN_PASSWORD = "IDIS2025";
    private static final String ADMIN_SESSION_KEY = "admin_authenticated";
    @GetMapping("/excel")
    public String adminLoginPage(HttpSession session) {
        // 이미 인증된 경우 바로 엑셀 페이지로
        if (Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_KEY))) {
            return "excel";
        }
        return "admin_login";
    }

    @PostMapping("/excel/auth")
    @ResponseBody
    public ResponseEntity<String> authenticateAdmin(@RequestParam("password") String password, HttpSession session) {
        if (ADMIN_PASSWORD.equals(password.trim())) {
            session.setAttribute(ADMIN_SESSION_KEY, true);
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.badRequest().body("비밀번호가 올바르지 않습니다.");
        }
    }

    @GetMapping("/excel/page")
    public String excelPage(HttpSession session) {
        // 인증 확인
        if (!Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_KEY))) {
            return "redirect:/excel";
        }
        session.removeAttribute(ADMIN_SESSION_KEY);
        return "excel";
    }

    @GetMapping("/excel/holiday")
    @ResponseBody
    public ResponseEntity<?> excelDownload(HttpSession session) {
        try {
            // 엑셀 파일 생성
            byte[] excelData = personService.generateHolidayExcel();
            String fileName = "명절선물신청목록_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";

            // 파일명 URL 인코딩 (한글 파일명 지원)
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");

            // 응답 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename*=UTF-8''" + encodedFileName);
            headers.add(HttpHeaders.CONTENT_TYPE,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            ByteArrayResource resource = new ByteArrayResource(excelData);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(excelData.length)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("엑셀 파일 생성 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/excel/cake")
    @ResponseBody
    public ResponseEntity<?> downloadCakeExcel(HttpSession session) {
        try {
            // 엑셀 파일 생성 (PersonService의 generateCakeExcel 메서드 사용)
            byte[] excelData = personService.generateCakeExcel();
            String fileName = "생일케이크신청목록_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";

            // 파일명 URL 인코딩 (한글 파일명 지원)
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");

            // 응답 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename*=UTF-8''" + encodedFileName);
            headers.add(HttpHeaders.CONTENT_TYPE,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            ByteArrayResource resource = new ByteArrayResource(excelData);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(excelData.length)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("엑셀 파일 생성 중 오류가 발생했습니다.");
        }
    }
}
