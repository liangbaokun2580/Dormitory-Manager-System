package com.dormitory.repository;

import com.dormitory.entity.Dormitory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DormitoryRepository extends JpaRepository<Dormitory, Long> {
    Optional<Dormitory> findByName(String name);
}
