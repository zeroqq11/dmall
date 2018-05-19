package cn.thoughtworks.school.programCenter.services;

import cn.thoughtworks.school.programCenter.entities.Assignment;
import cn.thoughtworks.school.programCenter.entities.AssignmentQuiz;
import cn.thoughtworks.school.programCenter.entities.ReviewQuiz;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AssignmentService {


    public String getAssignmentStatus(Assignment assignment, List<ReviewQuiz> submitAssignments) {
        List<AssignmentQuiz> selectedQuizzes = assignment.getSelectedQuizzes();
        if (isUnfinished(assignment.getType(), selectedQuizzes, submitAssignments)) {
            return "unfinished";
        }
        if (isReviewing(assignment.getType(),submitAssignments)) {
            return "reviewing";
        }
        if (isFinished(assignment.getType(),submitAssignments)) {
            return "finished";
        }
        return "excellent";
    }

    private boolean isFinished(String type, List<ReviewQuiz> submitAssignments) {
        if (Objects.equals(type, "BASIC_TYPE")) {
            return submitAssignments.get(0).getStatus().equals("已完成");
        }
        return submitAssignments.stream().anyMatch(item -> Objects.equals(item.getStatus(), "已完成"));
    }

    private boolean isReviewing(String type, List<ReviewQuiz> submitAssignments) {
        if (Objects.equals(type, "BASIC_TYPE")) {
            return submitAssignments.get(0).getStatus().equals("已提交");
        }

        return submitAssignments.stream().anyMatch(item -> Objects.equals(item.getStatus(), "已提交"));
    }

    private boolean isUnfinished(String type, List<AssignmentQuiz> selectedQuizzes, List<ReviewQuiz> submitAssignments) {
        if (Objects.equals(type, "BASIC_QUIZ")) {
            return submitAssignments.size() != 1;
        }
        return selectedQuizzes.size() != submitAssignments.size() || selectedQuizzes.size() == 0;

    }
}
