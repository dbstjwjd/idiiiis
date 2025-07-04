package com.iris.iris;

import com.iris.iris.entity.Person;
import com.iris.iris.repository.PersonRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class IrisApplicationTests {

    @Autowired
    private PersonRepository personRepository;

    @Test
    void contextLoads() {
    }

    @Test
    @Transactional
    @Rollback(false)  // 실제로 DB에 저장하려면 false로 설정
    void importExcelToDatabase() {
        String excelFilePath = "test.xlsx";
        
        try {
            List<Person> people = readExcelFile(excelFilePath);
            
            System.out.println("읽어온 데이터 수: " + people.size());
            
            // 각 직원 정보 출력
            for (Person person : people) {
                System.out.println("부서: " + person.getDepartment() + 
                                 ", 성명: " + person.getName() + 
                                 ", 사번: " + person.getEmployeeNumber());
            }
            
            // DB에 저장
            List<Person> savedPeople = personRepository.saveAll(people);
            System.out.println("DB에 저장된 데이터 수: " + savedPeople.size());
            
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("엑셀 파일 읽기 실패", e);
        }
    }
    
    private List<Person> readExcelFile(String filePath) throws IOException {
        List<Person> people = new ArrayList<>();
        
        try (FileInputStream fileInputStream = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            
            // 첫 번째 시트 가져오기
            Sheet sheet = workbook.getSheetAt(0);
            
            // 첫 번째 행은 헤더이므로 두 번째 행부터 읽기
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                // 각 셀의 값을 읽어오기
                String department = getCellValueAsString(row.getCell(0));
                String name = getCellValueAsString(row.getCell(1));
                String employeeNumber = getCellValueAsString(row.getCell(2));
                
                // 빈 행 건너뛰기
                if (department.isEmpty() && name.isEmpty() && employeeNumber.isEmpty()) {
                    continue;
                }
                
                // Person 객체 생성
                Person person = Person.builder()
                        .department(department)
                        .name(name)
                        .employeeNumber(employeeNumber)
                        .build();
                
                people.add(person);
            }
        }
        
        return people;
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // 숫자를 문자열로 변환 (사번이 숫자인 경우 대비)
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }
    
    @Test
    void verifyDataInDatabase() {
        // DB에 저장된 데이터 확인
        List<Person> allPeople = personRepository.findAll();
        
        System.out.println("\n=== DB에 저장된 전체 직원 목록 ===");
        System.out.println("총 직원 수: " + allPeople.size());
        
        for (Person person : allPeople) {
            System.out.println("ID: " + person.getId() + 
                             ", 부서: " + person.getDepartment() + 
                             ", 성명: " + person.getName() + 
                             ", 사번: " + person.getEmployeeNumber());
        }
    }
}
