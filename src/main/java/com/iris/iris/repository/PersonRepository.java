package com.iris.iris.repository;

import com.iris.iris.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    
    // 사번으로 직원 찾기
    Optional<Person> findByEmployeeNumber(String employeeNumber);
    
    // 사번이 존재하는지 확인
    boolean existsByEmployeeNumber(String employeeNumber);
}
