package com.dormitory.repository;

import com.dormitory.entity.Repair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairRepository extends JpaRepository<Repair, Long> {
    List<Repair> findAllByOrderByCreatedAtDesc();
    List<Repair> findByStudentIdOrderByCreatedAtDesc(String studentId);
}
