package com.iris.iris.service;

import com.iris.iris.dto.HolidayDTO;
import com.iris.iris.entity.Holiday;
import com.iris.iris.entity.Person;
import com.iris.iris.repository.HolidayRepository;
import com.iris.iris.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        holiday.setAddress(holidayDTO.getAddress());
        holiday.setDetail(holidayDTO.getDetail());
        holiday.setPresent(holidayDTO.getPresent());
        holiday.setPostCode(holidayDTO.getPostCode());
        holidayRepository.save(holiday);

        person.setHoliday(holiday);
        personRepository.save(person);
    }
}
