package com.iris.iris.repository;

import com.iris.iris.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Page<Person> findAll(Pageable pageable);
    Page<Person> findAll(Specification<Person> spec, Pageable pageable);
}
