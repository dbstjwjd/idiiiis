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

    public byte[] generateHolidayExcel() throws IOException {
        // 모든 Person 데이터 조회 (Holiday 정보 포함)
        List<Person> personList = personRepository.findAllWithHoliday();

        // 엑셀 워크북 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("명절선물신청목록");

        // 헤더 스타일 설정
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        // 헤더 행 생성
        Row headerRow = sheet.createRow(0);
        String[] headers = {"NO", "신청자", "사번", "선물 선택", "우편번호", "배송 주소", "연락처", "수령자"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 데이터 행 생성
        int rowIndex = 1;
        for (Person person : personList) {
            Row row = sheet.createRow(rowIndex);

            // NO
            Cell noCell = row.createCell(0);
            noCell.setCellValue(rowIndex);
            noCell.setCellStyle(dataStyle);

            // 신청자
            Cell nameCell = row.createCell(1);
            nameCell.setCellValue(person.getName());
            nameCell.setCellStyle(dataStyle);

            // 사번
            Cell employeeNumberCell = row.createCell(2);
            employeeNumberCell.setCellValue(person.getEmployeeNumber());
            employeeNumberCell.setCellStyle(dataStyle);

            // Holiday 정보가 있는 경우
            if (person.getHoliday() != null) {
                // 선물 선택 (enum의 getValue() 사용)
                Cell presentCell = row.createCell(3);
                String presentName = "";
                if (person.getHoliday().getPresent() != null) {
                    presentName = person.getHoliday().getPresent().getValue();
                }
                presentCell.setCellValue(presentName);
                presentCell.setCellStyle(dataStyle);

                // 우편번호
                Cell postCodeCell = row.createCell(4);
                postCodeCell.setCellValue(person.getHoliday().getPostCode() != null ?
                        person.getHoliday().getPostCode() : "");
                postCodeCell.setCellStyle(dataStyle);

                // 배송 주소 (주소 + 상세주소 합쳐서)
                Cell addressCell = row.createCell(5);
                String fullAddress = buildFullAddress(person.getHoliday());
                addressCell.setCellValue(fullAddress);
                addressCell.setCellStyle(dataStyle);

                // 연락처
                Cell phoneCell = row.createCell(6);
                phoneCell.setCellValue(person.getPhone());
                phoneCell.setCellStyle(dataStyle);

                // 수령자
                Cell receiverCell = row.createCell(7);
                receiverCell.setCellValue(person.getHoliday().getReceiver() != null ?
                        person.getHoliday().getReceiver() : "");
                receiverCell.setCellStyle(dataStyle);
            } else {
                // Holiday 정보가 없는 경우 빈 셀 생성
                for (int i = 3; i <= 7; i++) {
                    Cell emptyCell = row.createCell(i);
                    emptyCell.setCellValue("");
                    emptyCell.setCellStyle(dataStyle);
                }
            }

            rowIndex++;
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
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

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    private String buildFullAddress(Holiday holiday) {
        StringBuilder address = new StringBuilder();

        if (holiday.getAddress() != null && !holiday.getAddress().isEmpty()) {
            address.append(holiday.getAddress());
        }

        if (holiday.getDetail() != null && !holiday.getDetail().isEmpty()) {
            if (address.length() > 0) {
                address.append(" ");
            }
            address.append(holiday.getDetail());
        }

        return address.toString();
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
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
        return style;
    }

    public byte[] generateCakeExcel() throws IOException {
        // 모든 Person 데이터 조회 (Cake 정보 포함)
        List<Person> personList = personRepository.findAllWithCake();

        // 엑셀 워크북 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("생일케이크신청목록");

        // 헤더 스타일 설정
        CellStyle headerStyle = createCakeHeaderStyle(workbook);
        CellStyle dataStyle = createCakeDataStyle(workbook);
        CellStyle centerStyle = createCakeCenterStyle(workbook);

        // 헤더 행 생성 (상세주소 제거, 배송주소로 통합)
        Row headerRow = sheet.createRow(0);
        String[] headers = {"NO", "신청자", "사번", "부서", "케이크 종류", "배송일", "우편번호", "배송 주소", "연락처", "수령자"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 데이터 행 생성
        int rowIndex = 1;
        for (Person person : personList) {
            Row row = sheet.createRow(rowIndex);

            // NO
            Cell noCell = row.createCell(0);
            noCell.setCellValue(rowIndex);
            noCell.setCellStyle(centerStyle);

            // 신청자
            Cell nameCell = row.createCell(1);
            nameCell.setCellValue(person.getName() != null ? person.getName() : "");
            nameCell.setCellStyle(centerStyle);

            // 사번
            Cell employeeNumberCell = row.createCell(2);
            employeeNumberCell.setCellValue(person.getEmployeeNumber() != null ? person.getEmployeeNumber() : "");
            employeeNumberCell.setCellStyle(centerStyle);

            // 부서
            Cell departmentCell = row.createCell(3);
            departmentCell.setCellValue(person.getDepartment() != null ? person.getDepartment() : "");
            departmentCell.setCellStyle(centerStyle);

            // Cake 정보가 있는 경우
            if (person.getCake() != null) {
                Cake cake = person.getCake();

                // 케이크 종류
                Cell cakeTypeCell = row.createCell(4);
                cakeTypeCell.setCellValue(cake.getCakeType().getValue() != null ? cake.getCakeType().getValue().toString() : "");
                cakeTypeCell.setCellStyle(centerStyle);

                // 배송일
                Cell deliveryDateCell = row.createCell(5);
                if (cake.getDeliveryDate() != null) {
                    deliveryDateCell.setCellValue(cake.getDeliveryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                } else {
                    deliveryDateCell.setCellValue("");
                }
                deliveryDateCell.setCellStyle(centerStyle);

                // 우편번호
                Cell postCodeCell = row.createCell(6);
                postCodeCell.setCellValue(cake.getPostCode() != null ? cake.getPostCode() : "");
                postCodeCell.setCellStyle(centerStyle);

                // 배송 주소 (주소 + 상세주소 합친 것)
                Cell addressCell = row.createCell(7);
                String fullAddress = buildCakeFullAddress(cake);
                addressCell.setCellValue(fullAddress);
                addressCell.setCellStyle(dataStyle);

                Cell phonCell = row.createCell(8);
                phonCell.setCellValue(cake.getPerson().getPhone() != null ? cake.getPerson().getPhone() : "");
                phonCell.setCellStyle(centerStyle);

                // 수령자
                Cell receiverCell = row.createCell(9);
                receiverCell.setCellValue(cake.getReceiver() != null ? cake.getReceiver() : "");
                receiverCell.setCellStyle(centerStyle);

            } else {
                // Cake 정보가 없는 경우 빈 셀 생성 (컬럼이 하나 줄어들어서 4~8번까지)
                for (int i = 4; i <= 9; i++) {
                    Cell emptyCell = row.createCell(i);
                    emptyCell.setCellValue("");
                    emptyCell.setCellStyle(dataStyle);
                }
            }

            rowIndex++;
        }

        // 열 너비 설정
        setCakeColumnWidths(sheet, headers.length);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    private String buildCakeFullAddress(Cake cake) {
        StringBuilder address = new StringBuilder();

        if (cake.getAddress() != null && !cake.getAddress().isEmpty()) {
            address.append(cake.getAddress());
        }

        if (cake.getDetail() != null && !cake.getDetail().isEmpty()) {
            if (address.length() > 0) {
                address.append(" ");
            }
            address.append(cake.getDetail());
        }

        return address.toString();
    }

    private CellStyle createCakeHeaderStyle(Workbook workbook) {
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

    private CellStyle createCakeDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true); // 텍스트 줄바꿈
        return style;
    }

    private CellStyle createCakeCenterStyle(Workbook workbook) {
        CellStyle style = createCakeDataStyle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private void setCakeColumnWidths(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);

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
                case 7: // 배송 주소 (합쳐진 주소)
                    sheet.setColumnWidth(i, 10000);
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

    public byte[] generateGiftExcel() throws IOException {
        // 모든 Person 데이터 조회 (Cake 정보 포함)
        List<Person> personList = personRepository.findAllWithGift();

        // 엑셀 워크북 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("격려품신청목록");

        // 헤더 스타일 설정
        CellStyle headerStyle = createGiftHeaderStyle(workbook);
        CellStyle dataStyle = createGiftDataStyle(workbook);
        CellStyle centerStyle = createGiftCenterStyle(workbook);

        // 헤더 행 생성 (상세주소 제거, 배송주소로 통합)
        Row headerRow = sheet.createRow(0);
        String[] headers = {"NO", "신청자", "사번", "부서", "우편번호", "배송 주소", "연락처", "수령자"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 데이터 행 생성
        int rowIndex = 1;
        for (Person person : personList) {
            Row row = sheet.createRow(rowIndex);

            // NO
            Cell noCell = row.createCell(0);
            noCell.setCellValue(rowIndex);
            noCell.setCellStyle(centerStyle);

            // 신청자
            Cell nameCell = row.createCell(1);
            nameCell.setCellValue(person.getName() != null ? person.getName() : "");
            nameCell.setCellStyle(centerStyle);

            // 사번
            Cell employeeNumberCell = row.createCell(2);
            employeeNumberCell.setCellValue(person.getEmployeeNumber() != null ? person.getEmployeeNumber() : "");
            employeeNumberCell.setCellStyle(centerStyle);

            // 부서
            Cell departmentCell = row.createCell(3);
            departmentCell.setCellValue(person.getDepartment() != null ? person.getDepartment() : "");
            departmentCell.setCellStyle(centerStyle);

            // Cake 정보가 있는 경우
            if (person.getGift() != null) {
                Gift gift = person.getGift();
                // 우편번호
                Cell postCodeCell = row.createCell(4);
                postCodeCell.setCellValue(gift.getPostCode() != null ? gift.getPostCode() : "");
                postCodeCell.setCellStyle(centerStyle);

                // 배송 주소 (주소 + 상세주소 합친 것)
                Cell addressCell = row.createCell(5);
                String fullAddress = buildGiftFullAddress(gift);
                addressCell.setCellValue(fullAddress);
                addressCell.setCellStyle(dataStyle);

                Cell phonCell = row.createCell(6);
                phonCell.setCellValue(gift.getPerson().getPhone() != null ? gift.getPerson().getPhone() : "");
                phonCell.setCellStyle(centerStyle);

                // 수령자
                Cell receiverCell = row.createCell(7);
                receiverCell.setCellValue(gift.getReceiver() != null ? gift.getReceiver() : "");
                receiverCell.setCellStyle(centerStyle);

            } else {
                for (int i = 4; i <= 9; i++) {
                    Cell emptyCell = row.createCell(i);
                    emptyCell.setCellValue("");
                    emptyCell.setCellStyle(dataStyle);
                }
            }

            rowIndex++;
        }

        // 열 너비 설정
        setCakeColumnWidths(sheet, headers.length);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    private String buildGiftFullAddress(Gift gift) {
        StringBuilder address = new StringBuilder();

        if (gift.getAddress() != null && !gift.getAddress().isEmpty()) {
            address.append(gift.getAddress());
        }

        if (gift.getDetail() != null && !gift.getDetail().isEmpty()) {
            if (address.length() > 0) {
                address.append(" ");
            }
            address.append(gift.getDetail());
        }

        return address.toString();
    }

    private CellStyle createGiftHeaderStyle(Workbook workbook) {
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

    private CellStyle createGiftDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true); // 텍스트 줄바꿈
        return style;
    }

    private CellStyle createGiftCenterStyle(Workbook workbook) {
        CellStyle style = createCakeDataStyle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private void setGiftColumnWidths(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);

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
                case 7: // 배송 주소 (합쳐진 주소)
                    sheet.setColumnWidth(i, 10000);
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
}