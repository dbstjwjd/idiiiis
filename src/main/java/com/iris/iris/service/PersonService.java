package com.iris.iris.service;

import com.iris.iris.dto.CakeDTO;
import com.iris.iris.dto.GiftDTO;
import com.iris.iris.dto.HolidayDTO;
import com.iris.iris.entity.Cake;
import com.iris.iris.entity.Gift;
import com.iris.iris.entity.Holiday;
import com.iris.iris.entity.Person;
import com.iris.iris.repository.CakeRepository;
import com.iris.iris.repository.GiftRepository;
import com.iris.iris.repository.HolidayRepository;
import com.iris.iris.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final HolidayRepository holidayRepository;
    private final CakeRepository cakeRepository;
    private final GiftRepository giftRepository;

    public Person findById(Long id) {
        return personRepository.findById(id).orElse(null);
    }

    public void setHoliday(HolidayDTO holidayDTO) {
        Person person = personRepository.findById(holidayDTO.getPersonId()).orElse(null);
        if (person == null) {
            return;
        }

        Holiday holiday = new Holiday();
        holiday.setReceiver(holidayDTO.getReceiver());
        holiday.setAddress(holidayDTO.getAddress());
        holiday.setDetail(holidayDTO.getDetail());
        holiday.setPresent(holidayDTO.getPresent());
        holiday.setPostCode(holidayDTO.getPostCode());
        holiday.setPerson(person);

        person.setHoliday(holiday);
        personRepository.save(person);
    }

    public void setCake(CakeDTO cakeDTO) {
        Person person = personRepository.findById(cakeDTO.getPersonId()).orElse(null);
        if (person == null) {
            return;
        }

        Cake cake = new Cake();
        cake.setReceiver(cakeDTO.getReceiver());
        cake.setAddress(cakeDTO.getAddress());
        cake.setDetail(cakeDTO.getDetail());
        cake.setCakeType(cakeDTO.getCakeType());
        cake.setPostCode(cakeDTO.getPostCode());
        cake.setDivision(cakeDTO.getDivision());
        cake.setDeliveryDate(cakeDTO.getDeliveryDate());
        cake.setPerson(person);

        person.setCake(cake);
        personRepository.save(person);
    }

    public void setGift(GiftDTO giftDTO) {
        Person person = personRepository.findById(giftDTO.getPersonId()).orElse(null);
        if (person == null) {
            return;
        }

        Gift gift = new Gift();
        gift.setReceiver(giftDTO.getReceiver());
        gift.setAddress(giftDTO.getAddress());
        gift.setDetail(giftDTO.getDetail());
        gift.setPostCode(giftDTO.getPostCode());
        gift.setPerson(person);
        person.setGift(gift);
        personRepository.save(person);
    }

    public Holiday getHolidayByPersonId(Long personId) {
        Person person = findById(personId);
        return person != null ? person.getHoliday() : null;
    }

    public Cake getCakeByPersonId(Long personId) {
        Person person = findById(personId);
        return person != null ? person.getCake() : null;
    }

    public Gift getGiftByPersonId(Long personId) {
        Person person = findById(personId);
        return person != null ? person.getGift() : null;
    }

    private Specification<Person> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Person> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                return cb.or(cb.like(q.get("employeeNumber"), "%" + kw + "%"),
                        cb.like(q.get("name"), "%" + kw + "%"),
                        cb.like(q.get("department"), "%" + kw + "%")
                );
            }
        };
    }

    public Page<Person> getList(int page, String kw) {
        Pageable pageable = PageRequest.of(page, 10);
        Specification<Person> spec = search(kw);
        return personRepository.findAll(spec, pageable);
    }

    public void modifyHoliday(Holiday holiday, HolidayDTO holidayDTO) {
        holiday.setReceiver(holidayDTO.getReceiver());
        holiday.setAddress(holidayDTO.getAddress());
        holiday.setDetail(holidayDTO.getDetail());
        holiday.setPresent(holidayDTO.getPresent());
        holiday.setPostCode(holidayDTO.getPostCode());
        holidayRepository.save(holiday);
    }

    public void modifyCake(Cake cake, CakeDTO cakeDTO) {
        cake.setReceiver(cakeDTO.getReceiver());
        cake.setAddress(cakeDTO.getAddress());
        cake.setDetail(cakeDTO.getDetail());
        cake.setCakeType(cakeDTO.getCakeType());
        cake.setPostCode(cakeDTO.getPostCode());
        cake.setDivision(cakeDTO.getDivision());
        cake.setDeliveryDate(cakeDTO.getDeliveryDate());
        cakeRepository.save(cake);
    }

    public void modifyGift(Gift gift, GiftDTO giftDTO) {
        gift.setReceiver(giftDTO.getReceiver());
        gift.setAddress(giftDTO.getAddress());
        gift.setDetail(giftDTO.getDetail());
        gift.setPostCode(giftDTO.getPostCode());
        giftRepository.save(gift);
    }

    // Excel 생성 공통 메서드들
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createCenterStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private Cell createCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
        return cell;
    }

    private Cell createNumberCell(Row row, int columnIndex, int value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        return cell;
    }

    private void setColumnWidths(Sheet sheet, ExcelType type, int columnCount) {
        // 먼저 모든 컬럼에 대해 autoSizeColumn 실행
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }

        // 타입별 컬럼 너비 설정
        switch (type) {
            case HOLIDAY:
                setHolidayColumnWidths(sheet, columnCount);
                break;
            case CAKE:
                setCakeColumnWidths(sheet, columnCount);
                break;
            case GIFT:
                setGiftColumnWidths(sheet, columnCount);
                break;
        }
    }

    private void setHolidayColumnWidths(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            if (sheet.getColumnWidth(i) < 3000) {
                sheet.setColumnWidth(i, 3000);
            }
            switch (i) {
                case 0: // NO
                    sheet.setColumnWidth(i, 2000);
                    break;
                case 2: // 사번
                    sheet.setColumnWidth(i, 3500);
                    break;
                case 3: // 선물 선택
                    sheet.setColumnWidth(i, 5000);
                    break;
                case 4: // 우편번호
                    sheet.setColumnWidth(i, 3000);
                    break;
                case 5: // 배송 주소
                    sheet.setColumnWidth(i, 15000);
                    break;
                case 6: // 연락처
                    sheet.setColumnWidth(i, 4500);
                    break;
            }
        }
    }

    private void setCakeColumnWidths(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            if (sheet.getColumnWidth(i) < 2000) {
                sheet.setColumnWidth(i, 2000);
            }
            switch (i) {
                case 0: // NO
                    sheet.setColumnWidth(i, 1500);
                    break;
                case 1: // 신청자
                    sheet.setColumnWidth(i, 3000);
                    break;
                case 2: // 사번
                    sheet.setColumnWidth(i, 3500);
                    break;
                case 3: // 부서
                    sheet.setColumnWidth(i, 4000);
                    break;
                case 4: // 케이크 종류
                    sheet.setColumnWidth(i, 5500);
                    break;
                case 5: // 배송일
                    sheet.setColumnWidth(i, 3500);
                    break;
                case 6: // 우편번호
                    sheet.setColumnWidth(i, 3000);
                    break;
                case 7: // 배송 주소
                    sheet.setColumnWidth(i, 15000);
                    break;
                case 8: // 연락처
                    sheet.setColumnWidth(i, 4000);
                    break;
                case 9: // 수령자
                    sheet.setColumnWidth(i, 3000);
                    break;
            }
        }
    }

    private void setGiftColumnWidths(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            switch (i) {
                case 0: // NO
                    sheet.setColumnWidth(i, 1500);
                    break;
                case 1: // 신청자
                    sheet.setColumnWidth(i, 3000);
                    break;
                case 2: // 사번
                    sheet.setColumnWidth(i, 3500);
                    break;
                case 3: // 부서
                    sheet.setColumnWidth(i, 4000);
                    break;
                case 4: // 우편번호
                    sheet.setColumnWidth(i, 2800);
                    break;
                case 5: // 배송 주소
                    sheet.setColumnWidth(i, 15000);
                    break;
                case 6: // 연락처
                    sheet.setColumnWidth(i, 4000);
                    break;
                case 7: // 수령자
                    sheet.setColumnWidth(i, 3000);
                    break;
            }
        }
    }

    private String buildFullAddress(String address, String detail) {
        StringBuilder fullAddress = new StringBuilder();

        if (address != null && !address.isEmpty()) {
            fullAddress.append(address);
        }

        if (detail != null && !detail.isEmpty()) {
            if (fullAddress.length() > 0) {
                fullAddress.append(" ");
            }
            fullAddress.append(detail);
        }

        return fullAddress.toString();
    }

    private enum ExcelType {
        HOLIDAY, CAKE, GIFT
    }

    // Excel 생성 메서드들
    public byte[] generateHolidayExcel() throws IOException {
        List<Person> personList = personRepository.findAllWithHoliday();
        String[] headers = {"NO", "신청자", "사번", "선물 선택", "우편번호", "배송 주소", "연락처", "수령자"};

        return generateExcel(personList, "명절선물신청목록", headers, ExcelType.HOLIDAY, this::fillHolidayRow);
    }

    public byte[] generateCakeExcel() throws IOException {
        List<Person> personList = personRepository.findAllWithCake();
        String[] headers = {"NO", "신청자", "사번", "부서", "케이크 종류", "배송일", "우편번호", "배송 주소", "연락처", "수령자"};

        return generateExcel(personList, "생일케이크신청목록", headers, ExcelType.CAKE, this::fillCakeRow);
    }

    public byte[] generateGiftExcel() throws IOException {
        List<Person> personList = personRepository.findAllWithGift();
        String[] headers = {"NO", "신청자", "사번", "부서", "우편번호", "배송 주소", "연락처", "수령자"};

        return generateExcel(personList, "격려품신청목록", headers, ExcelType.GIFT, this::fillGiftRow);
    }

    private byte[] generateExcel(List<Person> personList, String sheetName, String[] headers,
                                 ExcelType type, RowFiller rowFiller) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle centerStyle = createCenterStyle(workbook);

        // 헤더 행 생성
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            createCell(headerRow, i, headers[i], headerStyle);
        }

        // 데이터 행 생성
        int rowIndex = 1;
        for (Person person : personList) {
            Row row = sheet.createRow(rowIndex);
            rowFiller.fillRow(row, person, rowIndex, dataStyle, centerStyle);
            rowIndex++;
        }

        // 열 너비 설정
        setColumnWidths(sheet, type, headers.length);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    @FunctionalInterface
    private interface RowFiller {
        void fillRow(Row row, Person person, int rowIndex, CellStyle dataStyle, CellStyle centerStyle);
    }

    private void fillHolidayRow(Row row, Person person, int rowIndex, CellStyle dataStyle, CellStyle centerStyle) {
        // 기본 정보
        createNumberCell(row, 0, rowIndex, centerStyle);
        createCell(row, 1, person.getName(), centerStyle);
        createCell(row, 2, person.getEmployeeNumber(), centerStyle);

        if (person.getHoliday() != null) {
            Holiday holiday = person.getHoliday();
            String presentName = holiday.getPresent() != null ? holiday.getPresent().getValue() : "";
            createCell(row, 3, presentName, centerStyle);
            createCell(row, 4, holiday.getPostCode(), centerStyle);
            createCell(row, 5, buildFullAddress(holiday.getAddress(), holiday.getDetail()), dataStyle);
            createCell(row, 6, person.getPhone(), centerStyle);
            createCell(row, 7, holiday.getReceiver(), centerStyle);
        } else {
            for (int i = 3; i <= 7; i++) {
                createCell(row, i, "", dataStyle);
            }
        }
    }

    private void fillCakeRow(Row row, Person person, int rowIndex, CellStyle dataStyle, CellStyle centerStyle) {
        // 기본 정보
        createNumberCell(row, 0, rowIndex, centerStyle);
        createCell(row, 1, person.getName(), centerStyle);
        createCell(row, 2, person.getEmployeeNumber(), centerStyle);
        createCell(row, 3, person.getDepartment(), centerStyle);

        if (person.getCake() != null) {
            Cake cake = person.getCake();
            String cakeType = cake.getCakeType() != null && cake.getCakeType().getValue() != null
                    ? cake.getCakeType().getValue().toString() : "";
            createCell(row, 4, cakeType, centerStyle);

            String deliveryDate = cake.getDeliveryDate() != null
                    ? cake.getDeliveryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
            createCell(row, 5, deliveryDate, centerStyle);
            createCell(row, 6, cake.getPostCode(), centerStyle);
            createCell(row, 7, buildFullAddress(cake.getAddress(), cake.getDetail()), dataStyle);
            createCell(row, 8, person.getPhone(), centerStyle);
            createCell(row, 9, cake.getReceiver(), centerStyle);
        } else {
            for (int i = 4; i <= 9; i++) {
                createCell(row, i, "", dataStyle);
            }
        }
    }

    private void fillGiftRow(Row row, Person person, int rowIndex, CellStyle dataStyle, CellStyle centerStyle) {
        // 기본 정보
        createNumberCell(row, 0, rowIndex, centerStyle);
        createCell(row, 1, person.getName(), centerStyle);
        createCell(row, 2, person.getEmployeeNumber(), centerStyle);
        createCell(row, 3, person.getDepartment(), centerStyle);

        if (person.getGift() != null) {
            Gift gift = person.getGift();
            createCell(row, 4, gift.getPostCode(), centerStyle);
            createCell(row, 5, buildFullAddress(gift.getAddress(), gift.getDetail()), dataStyle);
            createCell(row, 6, person.getPhone(), centerStyle);
            createCell(row, 7, gift.getReceiver(), centerStyle);
        } else {
            for (int i = 4; i <= 7; i++) {
                createCell(row, i, "", dataStyle);
            }
        }
    }
}