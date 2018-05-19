package cn.thoughtworks.school.programCenter.controllers;

import cn.thoughtworks.school.programCenter.annotations.Auth;
import cn.thoughtworks.school.programCenter.entities.Assignment;
import cn.thoughtworks.school.programCenter.entities.ReviewQuiz;
import cn.thoughtworks.school.programCenter.entities.User;
import cn.thoughtworks.school.programCenter.exceptions.BusinessException;
import cn.thoughtworks.school.programCenter.repositories.AssignmentRepository;
import cn.thoughtworks.school.programCenter.repositories.ReviewQuizRepository;
import cn.thoughtworks.school.programCenter.services.QuizCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = "/api/v2")
public class ReviewQuizController {
    @Autowired
    private ReviewQuizRepository reviewQuizRepository;
    @Autowired
    private QuizCenterService quizCenterService;
    @Autowired
    private AssignmentRepository assignmentRepository;

    @RequestMapping(value = "/assignments/{assignmentId}/quizzes/{quizId}/review", method = RequestMethod.GET)
    public ResponseEntity getReviewQuizByAssignmentIdAndQuizId(@PathVariable Long assignmentId, @PathVariable Long quizId, @Auth User user) {
        // 2018-5-17 该方法用于数据迁移，添加已提交的reviewQuiz数据，用户点击作业详情页面会先检测该作业的状态，判断是否是已提交。该方法在数据迁移结束后可删除
        dealAssignmentStatus(assignmentId,quizId,user);

        ReviewQuiz reviewQuiz = getReviewQuiz(assignmentId, quizId, user.getId());
        return new ResponseEntity<>(Objects.isNull(reviewQuiz) ? new ReviewQuiz() : reviewQuiz, HttpStatus.OK);
    }

    private void dealAssignmentStatus(Long assignmentId, Long quizId, User user) {
        ReviewQuiz reviewQuiz = getReviewQuiz(assignmentId, quizId, user.getId());
        if (Objects.nonNull(reviewQuiz)) {
            return;
        }
        Assignment assignment = assignmentRepository.findOne(assignmentId);
        List<Map> quizzes = quizCenterService.getQuizzesAndAnswerByQuizIds(quizId.toString(), assignmentId, user.getId(), assignment.getType());
        if (quizzes.size() != 0 && !Objects.equals("",quizzes.get(0).get("userAnswer").toString())) {
            reviewQuizRepository.save(new ReviewQuiz(user.getId(),quizId,assignmentId,"已提交"));
        }
    }

    @RequestMapping(value = "/students/{studentId}/assignments/{assignmentId}/quizzes/{quizId}/review", method = RequestMethod.GET)
    public ResponseEntity tutorGetReviewQuiz(@PathVariable Long assignmentId, @PathVariable Long quizId, @PathVariable Long studentId) {
        ReviewQuiz reviewQuiz = getReviewQuiz(assignmentId, quizId, studentId);
        if (reviewQuiz == null) {
            reviewQuiz = new ReviewQuiz();
        }
        return new ResponseEntity<>(reviewQuiz, HttpStatus.OK);
    }

    private ReviewQuiz getReviewQuiz(Long assignmentId, Long quizId, Long studentId) {
        Assignment assignment = assignmentRepository.findOne(assignmentId);
        if (Objects.equals(assignment.getType(), "BASIC_QUIZ")) {
            return reviewQuizRepository.findByAssignmentIdAndStudentId(assignmentId, studentId).stream().findFirst().orElse(new ReviewQuiz());
        } else {
            return reviewQuizRepository.findByAssignmentIdAndQuizIdAndStudentId(assignmentId, quizId, studentId);
        }
    }

    @RequestMapping(value = "/review/{id}", method = RequestMethod.GET)
    public ResponseEntity getReviewQuizById(@PathVariable Long id) throws BusinessException {
        if (id == null) {
            throw new BusinessException("review quiz 参数异常");
        }
        ReviewQuiz reviewQuiz = reviewQuizRepository.findOne(id);

        return new ResponseEntity<>(reviewQuiz, HttpStatus.OK);
    }


    @RequestMapping(value = "/review", method = RequestMethod.POST)
    public ResponseEntity addReviewQuiz(@RequestBody ReviewQuiz reviewQuiz, @Auth User user) {
        if (reviewQuiz.getId() == null) {
            reviewQuiz = reviewQuizRepository.save(reviewQuiz);
        } else {
            ReviewQuiz oldReview = reviewQuizRepository.findOne(reviewQuiz.getId());
            if (oldReview == null) {
                reviewQuiz.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()));
                reviewQuiz = reviewQuizRepository.save(reviewQuiz);
            } else {
                oldReview.setStatus(reviewQuiz.getStatus());
                oldReview.setGrade(reviewQuiz.getGrade());
                oldReview.setTutorId(user.getId());
                reviewQuiz = reviewQuizRepository.save(oldReview);
            }
        }

        Map<String, Long> result = new HashMap<>();
        result.put("id", reviewQuiz.getId());

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}
