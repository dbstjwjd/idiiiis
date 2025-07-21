package com.iris.iris.repository;

import com.iris.iris.entity.Gift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftRepository extends JpaRepository<Gift,Long> {
}
