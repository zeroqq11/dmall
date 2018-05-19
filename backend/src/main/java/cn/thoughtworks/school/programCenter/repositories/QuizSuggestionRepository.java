package cn.thoughtworks.school.programCenter.repositories;

import cn.thoughtworks.school.programCenter.entities.SectionSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizSuggestionRepository extends JpaRepository<SectionSuggestion, Long> {
    List<SectionSuggestion> findByAssignmentIdAndQuizIdAndStudentIdOrderByCreateTimeDesc(Long assignmentId, Long quizId, Long studentId);
}
