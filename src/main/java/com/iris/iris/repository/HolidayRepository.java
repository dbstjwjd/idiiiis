package com.iris.iris.repository;

import com.iris.iris.entity.Holiday;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
}
