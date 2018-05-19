package cn.thoughtworks.school.programCenter.repositories;

import cn.thoughtworks.school.programCenter.entities.ExcellentQuizComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExcellentQuizCommentRepository extends JpaRepository<ExcellentQuizComment, Long> {
    List<ExcellentQuizComment> findByAssignmentIdAndQuizIdAndStudentIdOrderByCreateTimeDesc(Long assignmentId, Long quizId,Long studentId);
}
