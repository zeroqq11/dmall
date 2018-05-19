package cn.thoughtworks.school.programCenter.controllers;

import cn.thoughtworks.school.programCenter.annotations.Auth;
import cn.thoughtworks.school.programCenter.entities.*;
import cn.thoughtworks.school.programCenter.repositories.*;
import cn.thoughtworks.school.programCenter.services.QuizCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

@Controller
@RequestMapping(value = "/api")
public class BasicBlankQuizController {

    @Autowired
    private QuizCenterService quizCenterService;
    @Autowired
    private ReviewQuizRepository reviewQuizRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;

    @RequestMapping(value = "/v2/assignments/{assignmentId}/answers", method = RequestMethod.POST)
    public ResponseEntity submitBasicQuizAnswer(@PathVariable Long assignmentId,
                                                @RequestBody List<Map> submitAnswers,
                                                @Auth User current) throws Exception {
        Long studentId = current.getId();
        List quizSubmissionList = quizCenterService.submitBasicQuizAnswer(assignmentId, submitAnswers, studentId);
        int correctCount = quizSubmissionList.stream().filter(quizSubmissionStatus -> quizSubmissionStatus.equals(true)).toArray().length;
        int grade = (int) Math.rint((correctCount / (double) quizSubmissionList.size()) * 100);
        Long taskId = assignmentRepository.findOne(assignmentId).getTaskId();
        String status = grade > 85 ? "优秀" : "已完成";
        ReviewQuiz reviewQuiz = new ReviewQuiz(studentId, assignmentId, taskId, grade, status);
        reviewQuizRepository.save(reviewQuiz);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
