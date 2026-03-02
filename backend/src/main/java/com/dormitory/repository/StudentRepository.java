package com.dormitory.repository;

import com.dormitory.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentId(String studentId);

    List<Student> findByRoomIsNull();

    List<Student> findByNameContainingOrStudentIdContaining(String name, String studentId);
    List<Student> findByRoom_Id(Long roomId);

    long countByRoom_Id(Long roomId);
}
