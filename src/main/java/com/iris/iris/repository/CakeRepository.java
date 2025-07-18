package com.iris.iris.repository;

import com.iris.iris.entity.Cake;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CakeRepository extends JpaRepository<Cake,Long> {
}
