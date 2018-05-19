package cn.thoughtworks.school.programCenter.controllers;

import cn.thoughtworks.school.programCenter.annotations.Auth;
import cn.thoughtworks.school.programCenter.entities.*;
import cn.thoughtworks.school.programCenter.exceptions.BusinessException;
import cn.thoughtworks.school.programCenter.repositories.*;
import cn.thoughtworks.school.programCenter.services.UserCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/api")
public class MyStudentController {

  @Autowired
  private TaskRepository taskRepository;
  @Autowired
  private ReviewQuizRepository reviewQuizRepository;
  @Autowired
  private UserCenterService userCenterService;
  @Autowired
  private TopicRepository topicRepository;
  @Autowired
  private UserProgramRepository userProgramRepository;
  @Autowired
  private FollowRepository followRepository;

  @PostMapping("/myStudents/programs/{programId}/followers/{followerId}")
  public ResponseEntity follow(@PathVariable Long programId, @PathVariable Long followerId, @Auth User current) throws BusinessException {
    Follow follow = followRepository.findByProgramIdAndTutorIdAndStudentId(programId, current.getId(), followerId);
    if (Objects.nonNull(follow)) {
      throw new BusinessException("已关注该学员");
    }
    follow = new Follow();
    follow.setProgramId(programId);
    follow.setStudentId(followerId);
    follow.setTutorId(current.getId());
    followRepository.save(follow);

    return new ResponseEntity(HttpStatus.CREATED);
  }

  @GetMapping("/v2/students/{studentId}")
  public ResponseEntity getStudent(@PathVariable Long studentId) throws BusinessException {
    ResponseEntity student = userCenterService.getUserInfo(studentId);
    return new ResponseEntity<>(student.getBody(), HttpStatus.OK);
  }

  @DeleteMapping("/myStudents/programs/{programId}/followers/{followerId}")
  public ResponseEntity unFollow(@PathVariable Long programId, @PathVariable Long followerId, @Auth User current) throws BusinessException {
    Follow follow = followRepository.findByProgramIdAndTutorIdAndStudentId(programId, current.getId(), followerId);
    if (Objects.isNull(follow)) {
      throw new BusinessException("未关注该学员");
    }
    followRepository.delete(follow);
    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }

  @RequestMapping(value = "/myStudents/programs/{programId}", method = RequestMethod.GET)
  public ResponseEntity getMyStudents(@Auth User current, @PathVariable Long programId) {
    ArrayList result = new ArrayList();
    List<Follow> myFollowStudentIds = followRepository.findByProgramIdAndTutorId(programId, current.getId());
    List<Map> currentProgramFollowStudents = getCurrentProgramFollowStudents(myFollowStudentIds);

    for (Map myStudent : currentProgramFollowStudents) {
      HashMap studentGrade = new HashMap();
      Integer sumGrade = getAllTaskGrade(programId, ((Integer) myStudent.get("id")).longValue());
      String name;
      if (Objects.isNull(myStudent.get("name"))) {
        name = (String) myStudent.get("username");
      } else {
        name = myStudent.get("name").toString();
      }
      studentGrade.put("name", name);
      studentGrade.put("userId", myStudent.get("id"));
      studentGrade.put("sumPoints", sumGrade);
      result.add(studentGrade);
    }
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  private List getCurrentProgramFollowStudents(List<Follow> myFollowStudentIds) {
    String ids = myFollowStudentIds.stream().map(follow -> follow.getStudentId().toString()).collect(Collectors.joining(","));
    return userCenterService.getUsersByIds(ids);
  }

  private Integer getAllTaskGrade(Long programId, Long studentId) {
    List<Task> tasks = taskRepository.findTaskByProgramIdAndVisible(programId, true);
    Long scores = reviewQuizRepository.getScores(studentId, getTaskIds(tasks));
    return Objects.isNull(scores) ? 0 : Integer.parseInt(scores.toString());
  }

  private List<Long> getTaskIds(List<Task> tasks) {
    List<Long> result = new ArrayList<>();
    for (Task task : tasks) {
      result.add(task.getId());
    }
    return result;
  }


  @GetMapping("/myStudents/programs/{programId}/{query}")
  public ResponseEntity getUserByNameOrEmail(@PathVariable String query, @PathVariable Long programId, @Auth User current) {
    Map result = new HashMap();
    List<Map> searchStudents = (List<Map>) userCenterService.getUserByNameOrEmail(query).getBody();
    List<UserProgram> programStudents = userProgramRepository.findByProgramId(programId);
    searchStudents = searchStudents.stream().filter(student -> {
          Optional first = programStudents.stream().filter(programStudent ->
              Objects.equals(Long.valueOf(student.get("id").toString()), programStudent.getUserId())
          ).findFirst();
          return first.isPresent();
        }
    ).collect(Collectors.toList());
    result.put("searchUsers", searchStudents);
    result.put("followers", followRepository.findByProgramIdAndTutorId(programId, current.getId()));

    return new ResponseEntity<>(result, HttpStatus.OK);
  }


  @GetMapping("/v2/myStudents/programs/{programId}/students/assignments")
  public ResponseEntity getMyStudentsSectionsStatus(@PathVariable Long programId, @Auth User current) {
    List<Map> result = new ArrayList<>();
    List<Follow> myFollowStudentIds = followRepository.findByProgramIdAndTutorId(programId, current.getId());
    List<Map> currentProgramFollowStudents = getCurrentProgramFollowStudents(myFollowStudentIds);
    List<Topic> topics = topicRepository.findByProgramIdAndVisibleOrderByOrderNumberAsc(programId, true);
    List<Task> tasks = taskRepository.findByProgramIdAndVisibleOrderByOrderNumberAsc(programId, true);

    topics.forEach(topic -> tasks.stream().filter(task -> Objects.equals(topic.getId(), task.getTopicId())).forEach(task -> {
      List<Assignment> assignments = task.getAssignments();
      List<Map> taskAssignmentsInfo = getStudentAssignmentStatus(topic, task, assignments, currentProgramFollowStudents);
      taskAssignmentsInfo.stream().forEach(taskAssignment -> result.add(taskAssignment));
    }));
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  private List<Map> getStudentAssignmentStatus(Topic topic, Task task, List<Assignment> assignments, List<Map> myStudents) {
    List<Map> taskAssignmentInfo = assignments.stream().map(assignment -> {

      Map<String, Object> assignmentInfo = new HashMap();
      Map<String, Object> studentsInfo = new HashMap();
      List<Map> excellentStudents = new ArrayList<>();
      List<Map> finishedStudents = new ArrayList<>();
      List<Map> reviewingStudents = new ArrayList<>();
      List<Map> unfinishedStudents = new ArrayList<>();

      myStudents.stream().forEach(myStudent -> {
        List<ReviewQuiz> reviewQuizzes = reviewQuizRepository.findAllByAssignmentIdAndStudentId(assignment.getId(), Long.valueOf(myStudent.get("id").toString()));
        if (reviewQuizzes.size() == 0) {
          unfinishedStudents.add(myStudent);
        } else {
          List finished = reviewQuizzes.stream().filter(reviewQuiz -> reviewQuiz.getStatus().equals("已完成")).collect(Collectors.toList());
          List excellent = reviewQuizzes.stream().filter(reviewQuiz -> reviewQuiz.getStatus().equals("优秀")).collect(Collectors.toList());
          if (finished.size() == reviewQuizzes.size()) {
            finishedStudents.add(myStudent);
          } else if (excellent.size() == reviewQuizzes.size()) {
            excellentStudents.add(myStudent);
          } else if (finished.size() + excellent.size() == reviewQuizzes.size()) {
            finishedStudents.add(myStudent);
          } else {
            reviewingStudents.add(myStudent);
          }
        }
      });
      studentsInfo.put("excellentStudents", excellentStudents);
      studentsInfo.put("finishedStudents", finishedStudents);
      studentsInfo.put("reviewingStudents", reviewingStudents);
      studentsInfo.put("unfinishedStudents", unfinishedStudents);

      assignmentInfo.put("topic", topic);
      assignmentInfo.put("task", task);
      assignmentInfo.put("assignment", assignment);
      assignmentInfo.put("students", studentsInfo);
      return assignmentInfo;
    }).collect(Collectors.toList());
    return taskAssignmentInfo;
  }
}
