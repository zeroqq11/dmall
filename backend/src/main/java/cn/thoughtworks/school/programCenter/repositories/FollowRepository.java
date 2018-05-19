package cn.thoughtworks.school.programCenter.repositories;

import cn.thoughtworks.school.programCenter.entities.Follow;
import cn.thoughtworks.school.programCenter.entities.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow,Long>{
    List<Follow> findByProgramIdAndTutorId(Long programId, Long tutorId);
    Follow findByProgramIdAndTutorIdAndStudentId(Long programId, Long tutorId, Long studentId);
}
