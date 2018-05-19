package cn.thoughtworks.school.programCenter.controllers;

import cn.thoughtworks.school.programCenter.annotations.Auth;
import cn.thoughtworks.school.programCenter.entities.SectionSuggestion;
import cn.thoughtworks.school.programCenter.entities.User;
import cn.thoughtworks.school.programCenter.exceptions.BusinessException;
import cn.thoughtworks.school.programCenter.repositories.QuizSuggestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/v2")
public class QuizSuggestionController {
  @Autowired
  private QuizSuggestionRepository quizSuggestionRepository;

  @RequestMapping(value = "/suggestions", method = RequestMethod.POST)
  public ResponseEntity addTutorSuggestionToQuiz(@RequestBody SectionSuggestion suggestion) {
    suggestion.setCreateTime(new SimpleDateFormat("YYYY-MM-dd HH-mm-ss").format(new Date()));
    suggestion = quizSuggestionRepository.save(suggestion);

    Map<String, Long> result = new HashMap<>();
    result.put("id", suggestion.getId());

    return new ResponseEntity<>(result, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/students/{studentId}/assignments/{assignmentId}/quizzes/{quizId}/suggestions", method = RequestMethod.GET)
  public ResponseEntity getSuggestionsByStudentIdAndAssignmentsAndQuizzes(@PathVariable Long studentId, @PathVariable Long assignmentId, @PathVariable Long quizId) {
    List<SectionSuggestion> suggestions = quizSuggestionRepository.findByAssignmentIdAndQuizIdAndStudentIdOrderByCreateTimeDesc(assignmentId, quizId, studentId);

    return new ResponseEntity<>(suggestions, HttpStatus.OK);
  }

  @RequestMapping(value = "/assignments/{assignmentId}/quizzes/{quizId}/suggestions", method = RequestMethod.GET)
  public ResponseEntity studentGetSuggestionsByStudentIdAndAssignmentsAndQuizzes(@Auth User user, @PathVariable Long assignmentId, @PathVariable Long quizId) {
    List<SectionSuggestion> suggestions = quizSuggestionRepository.findByAssignmentIdAndQuizIdAndStudentIdOrderByCreateTimeDesc(assignmentId, quizId, user.getId());

    return new ResponseEntity<>(suggestions, HttpStatus.OK);
  }

  @RequestMapping(value = "/suggestions/{id}", method = RequestMethod.PUT)
  public ResponseEntity updateSuggestion(@PathVariable Long id, @RequestBody Map date) throws BusinessException {
    SectionSuggestion suggestion = quizSuggestionRepository.findOne(id);
    if (suggestion == null) {
      throw new BusinessException("该评论不存在");
    }
    suggestion.setContent((String) date.get("content"));
    quizSuggestionRepository.save(suggestion);

    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }

  @RequestMapping(value = "/suggestions/{id}", method = RequestMethod.DELETE)
  public ResponseEntity deleteSuggestion(@PathVariable Long id) throws BusinessException {
    SectionSuggestion suggestion = quizSuggestionRepository.findOne(id);
    if (suggestion == null) {
      throw new BusinessException("该评论不存在");
    }

    quizSuggestionRepository.delete(id);

    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }
}
