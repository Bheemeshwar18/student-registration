package student_registration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import student_registration.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    List<Student> findByNameContainingIgnoreCase(String name);

    List<Student> findByEmailContainingIgnoreCase(String email);

    List<Student> findByDepartmentContainingIgnoreCase(String department);
}