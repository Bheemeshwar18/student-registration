package student_registration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import student_registration.entity.ActivityLog;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Integer> {

    List<ActivityLog> findTop10ByOrderByTimestampDesc();

}