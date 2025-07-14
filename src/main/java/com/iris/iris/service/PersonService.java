package com.iris.iris.service;

import com.iris.iris.dto.HolidayDTO;
import com.iris.iris.entity.Holiday;
import com.iris.iris.entity.Person;
import com.iris.iris.repository.HolidayRepository;
import com.iris.iris.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final HolidayRepository holidayRepository;

    public List<Person> getAll() {
        return personRepository.findAll();
    }

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
        holidayRepository.save(holiday);

        person.setHoliday(holiday);
        personRepository.save(person);
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

    public Holiday getHolidayById(Long id) {
        return holidayRepository.findById(id).orElse(null);
    }

    public void modifyHoliday(Holiday holiday, HolidayDTO holidayDTO) {
        holiday.setReceiver(holidayDTO.getReceiver());
        holiday.setAddress(holidayDTO.getAddress());
        holiday.setDetail(holidayDTO.getDetail());
        holiday.setPresent(holidayDTO.getPresent());
        holiday.setPostCode(holidayDTO.getPostCode());
        holidayRepository.save(holiday);
    }
}
