package cn.thoughtworks.school.programCenter.controllers;

import cn.thoughtworks.school.programCenter.annotations.Auth;
import cn.thoughtworks.school.programCenter.entities.*;
import cn.thoughtworks.school.programCenter.exceptions.BusinessException;
import cn.thoughtworks.school.programCenter.repositories.*;
import cn.thoughtworks.school.programCenter.services.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/api/programs")
public class ProgramController {
  @Autowired
  private ProgramRepository programRepository;
  @Autowired
  private UserProgramRepository userProgramRepository;
  @Autowired
  private TaskRepository taskRepository;
  @Autowired
  private TopicRepository topicRepository;
  @Autowired
  private ReviewQuizRepository reviewQuizRepository;
  @Autowired
  private UserCenterService userCenterService;
  @Autowired
  private TutorProgramRepository tutorProgramRepository;
  @Autowired
  private FollowRepository followRepository;
  @Autowired
  private AssignmentRepository assignmentRepository;
  @Autowired
  private AssignmentService assignmentService;

  @Autowired
  private ProgramService programService;

  private ObjectMapper oMapper = new ObjectMapper();

  @PostMapping(value = "/{programId}/tutor/{tutorId}")
  public ResponseEntity tutorJoinProgram(@RequestBody TutorProgram tutorProgram) throws BusinessException {
    TutorProgram current = tutorProgramRepository.findByTutorIdAndProgramId(tutorProgram.getTutorId(), tutorProgram.getProgramId());
    if (Objects.nonNull(current)) {
      throw new BusinessException("用户已加入该训练营");
    }
    tutorProgramRepository.save(tutorProgram);
    userCenterService.addTutorRole(tutorProgram.getTutorId());

    return new ResponseEntity(HttpStatus.CREATED);
  }

  @PostMapping(value = "/{programId}/student/{studentId}")
  public ResponseEntity studentJoinProgram(@RequestBody UserProgram userProgram) throws BusinessException {
    UserProgram current = userProgramRepository.findByProgramIdAndUserId(userProgram.getProgramId(), userProgram.getUserId());
    if (Objects.nonNull(current)) {
      throw new BusinessException("用户已加入该训练营");
    }
    userProgramRepository.save(userProgram);

    return new ResponseEntity(HttpStatus.CREATED);
  }

  @PostMapping(value = "/{programId}/tutors")
  public ResponseEntity tutorsJoinProgram(@PathVariable Long programId, @RequestBody Map usernames) throws BusinessException {
    verifyParams(programId, usernames);
    Map result = new HashMap();
    List notExistNames = new ArrayList();
    String[] userNames = usernames.get("usernames").toString().split(",,");
    for (String name : userNames) {
      Map user = userCenterService.getUserByName(name);
      if (Objects.isNull(user)) {
        notExistNames.add(name);
        continue;
      }
      TutorProgram tutor = tutorProgramRepository.findByTutorIdAndProgramId(Long.valueOf(user.get("id").toString()), programId);
      if (Objects.isNull(tutor)) {
        TutorProgram temp = new TutorProgram();
        temp.setProgramId(programId);
        temp.setTutorId(Long.valueOf(user.get("id").toString()));
        tutorProgramRepository.save(temp);
        userCenterService.addTutorRole(temp.getTutorId());
      }
    }
    result.put("用户名不存在的用户：", notExistNames);
    return new ResponseEntity(result, HttpStatus.CREATED);
  }

  @PostMapping(value = "/{programId}/students")
  public ResponseEntity studentsJoinProgram(@PathVariable Long programId, @RequestBody Map usernames) throws BusinessException {
    verifyParams(programId, usernames);
    Map result = new HashMap();
    List notExistNames = new ArrayList();
    String[] userNames = usernames.get("usernames").toString().split(",,");
    for (String name : userNames) {
      Map user = userCenterService.getUserByName(name);
      if (Objects.isNull(user)) {
        notExistNames.add(name);
        continue;
      }
      UserProgram userProgram = userProgramRepository.findByProgramIdAndUserId(programId, Long.valueOf(user.get("id").toString()));
      if (Objects.isNull(userProgram)) {
        UserProgram temp = new UserProgram();
        temp.setProgramId(programId);
        temp.setUserId(Long.valueOf(user.get("id").toString()));
        userProgramRepository.save(temp);
      }
    }
    result.put("用户名不存在的用户：", notExistNames);
    return new ResponseEntity(result, HttpStatus.CREATED);
  }

  private void verifyParams(@PathVariable Long programId, @RequestBody Map usernames) throws BusinessException {
    if (Objects.isNull(usernames.get("usernames"))) {
      throw new BusinessException("未接收到数据");
    }
    if (Objects.isNull(programRepository.findOne(programId))) {
      throw new BusinessException("训练营不存在");
    }
  }


  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity getProgramList(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "pageSize", defaultValue = "10") Integer size) {
    Pageable pageable = new PageRequest(page - 1, size, Sort.Direction.DESC, "id");
    Page programs = programRepository.findAll(pageable);
    return new ResponseEntity<>(programs, HttpStatus.OK);
  }

  @RequestMapping(value = "", method = RequestMethod.POST)
  public ResponseEntity addProgramList(@Auth User current, @RequestBody Program newProgram) {
    newProgram.setCreatorId(current.getId());
    Program program = programRepository.save(newProgram);
    Map<String, String> body = new HashMap<>();
    body.put("uri", "/api/programs/" + program.getId());
    return new ResponseEntity<>(body, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/{programId}", method = RequestMethod.DELETE)
  public ResponseEntity deleteProgramList(@PathVariable Long programId) throws Exception {
    if (!programRepository.exists(programId)) {
      throw new BusinessException("删除的program 不存在！");
    }
    programRepository.delete(programId);
    topicRepository.deleteByProgramId(programId);
    taskRepository.deleteByProgramId(programId);
    userProgramRepository.deleteByProgramId(programId);
    tutorProgramRepository.deleteByProgramId(programId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);

  }

  @RequestMapping(value = "{programId}/leaderBoard", method = RequestMethod.GET)
  public ResponseEntity getLeaderBoard(@PathVariable Long programId, @Auth User current) throws Exception {
    Map result = new HashMap();
    List<Map> leaderBoards = programService.getStudentLeaderBoard();
    List currentLeaderBoard = (List) leaderBoards.stream().filter(item ->
        Objects.equals(item.get("programId").toString(), programId.toString()))
        .findFirst().orElse(new HashMap()).get("currentProgramLeaderBoard");
    if (Objects.isNull(currentLeaderBoard)) {
      currentLeaderBoard = new ArrayList();
    }
    result.put("leaderBoard", currentLeaderBoard.size() > 10 ?
        currentLeaderBoard.subList(0, 10) : currentLeaderBoard);
    result.put("myRank", getRankInfo(currentLeaderBoard, current));

    return new ResponseEntity<>(result, HttpStatus.OK);

  }

  private Map getRankInfo(List currentLeaderBoard, User current) {
    for (int i = 0; i < currentLeaderBoard.size(); ++i) {
      Map temp = (Map) currentLeaderBoard.get(i);
      if (Objects.equals(temp.get("studentId").toString(), current.getId().toString())) {
        temp.put("rank", i + 1);
        return temp;
      }
    }
    Map notFoundRankInfo = new HashMap();
    notFoundRankInfo.put("name", current.getUsername());
    notFoundRankInfo.put("finishHomeworkCount", 0);
    notFoundRankInfo.put("finishHomeworkPoint", 0);
    notFoundRankInfo.put("rank", 0);
    notFoundRankInfo.put("studentId", current.getId());
    return notFoundRankInfo;
  }


  @RequestMapping(value = "/{programId}", method = RequestMethod.PUT)
  public ResponseEntity editProgram(@PathVariable Long programId, @RequestBody Program program) throws Exception {
    if (!programRepository.exists(programId)) {
      throw new BusinessException("修改的program 不存在！");
    }
    programRepository.save(program);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }


  @RequestMapping(value = "/my", method = RequestMethod.GET)
  public ResponseEntity getMyProgramList(@Auth User current) throws BusinessException {
    List<UserProgram> userPrograms = userProgramRepository.findByUserId(current.getId());
    List myPrograms = userPrograms.stream().map(userProgram -> {
      Map temp = new HashMap();
      Program program = programRepository.findProgramByCategoryAndId("付费", userProgram.getProgramId());
      if (program == null) {
        return new BusinessException("请检查课程的类型");
      }
      List<Task> allTasks = taskRepository.findByProgramId(program.getId());
      temp.put("program", program);
      temp.put("totalTasks", allTasks);
      temp.put("finishedTasks", getFinishedTasks(allTasks, current.getId()));
      return temp;
    }).collect(Collectors.toList());
    return new ResponseEntity<>(myPrograms, HttpStatus.OK);
  }

  private List getFinishedTasks(List<Task> tasks, Long userId) {
    List taskIds = tasks.stream().map(task -> task.getId()).collect(Collectors.toList());
    List<ReviewQuiz> reviews = reviewQuizRepository.findAllByTaskIdInAndStudentId(taskIds, userId);
    List<ReviewQuiz> unique = reviews.stream().collect(
        collectingAndThen(
            toCollection(() -> new TreeSet<>(comparingLong(ReviewQuiz::getTaskId))), ArrayList::new)
    );
    return unique;
  }

  @RequestMapping(value = "/tutored", method = RequestMethod.GET)
  public ResponseEntity getTutoredProgramList(@Auth User current) {
    ArrayList<Map> tutoredProgramInfo = new ArrayList<>();
    List<TutorProgram> tutoredPrograms = tutorProgramRepository.findByTutorId(current.getId());

    for (TutorProgram tutorProgram : tutoredPrograms) {
      Map temp = new HashMap();
      Program program = programRepository.findProgramByCategoryAndId("付费", tutorProgram.getProgramId());
      if (Objects.isNull(program)) {
        continue;
      }
      List<UserProgram> programStudents = userProgramRepository.findByProgramId(program.getId());
      List<Follow> currentProgramFollowStudents = followRepository.findByProgramIdAndTutorId(program.getId(), current.getId());
      temp.put("program", program);
      temp.put("totalStudents", programStudents);
      temp.put("followStudents", currentProgramFollowStudents);
      tutoredProgramInfo.add(temp);
    }
    return new ResponseEntity<>(tutoredProgramInfo, HttpStatus.OK);
  }

  @RequestMapping(value = "/{programId}/introductions", method = RequestMethod.GET)
  public ResponseEntity getProgramIntroduction(@PathVariable Long programId) throws Exception {
    Program program = programRepository.findOne(programId);
    if (program == null) {
      throw new BusinessException("该课程不存在");
    }
    return new ResponseEntity<>(program, HttpStatus.OK);
  }

  @RequestMapping(value = "/{programId}/tasks", method = RequestMethod.GET)
  public ResponseEntity getProgramTasks(@PathVariable Long programId, @Auth User current) throws Exception {
    List result = new ArrayList();

    List<Topic> topics = topicRepository.findByProgramIdAndVisibleOrderByOrderNumberAsc(programId, true);
    List<Task> tasks = getAllTasks(topics);
    for (Topic topic : topics) {
      Map item = new HashMap();
      item.put("title", topic.getTitle());
      List<Task> currentTopicTasks = tasks.stream()
          .filter(task -> Objects.equals(task.getTopicId(), topic.getId()))
          .collect(Collectors.toList());
      if (currentTopicTasks.size() == 0) {
        continue;
      }
      List<Map> tasksAssignments = getTasksAssignment(currentTopicTasks, current);
      item.put("all", tasksAssignments);
      item.put("finished", getStatusTasks(tasksAssignments, "finished"));
      item.put("reviewing", getStatusTasks(tasksAssignments, "reviewing"));
      item.put("unfinished", getStatusTasks(tasksAssignments, "unfinished"));
      item.put("excellent", getStatusTasks(tasksAssignments, "excellent"));
      result.add(item);
    }

    return new ResponseEntity(result, HttpStatus.OK);
  }

  private List<Map> getStatusTasks(List<Map> tasksAssignments, String status) {
    return tasksAssignments.stream()
        .filter(task -> Objects.equals(task.get("status"), status)).collect(Collectors.toList());
  }

  private List getTasksAssignment(List<Task> tasks, User user) {
    List result = new ArrayList();
    List<Assignment> assignments = getAllAssignments(tasks);
    for (Task task : tasks) {
      Map item = new HashMap();
      List<Assignment> currentTaskAssignments = assignments.stream()
          .filter(assignment -> Objects.equals(assignment.getTaskId(), task.getId()))
          .collect(Collectors.toList());
      List<Map> currentTaskAssignmentsStatus = getAssignmentsStatus(currentTaskAssignments, user);
      item.put("title", task.getTitle());
      item.put("id", task.getId());
      item.put("type", task.getType());
      item.put("assignments", currentTaskAssignmentsStatus);
      item.put("status", getTaskStatus(currentTaskAssignmentsStatus));
      result.add(item);
    }
    return result;
  }

  private String getTaskStatus(List<Map> assignments) {
    if (assignments.size() == 0 ||
        assignments.stream().anyMatch(assignment -> Objects.equals(assignment.get("status"), "unfinished"))) {
      return "unfinished";
    }
    if (assignments.stream().allMatch(assignment -> Objects.equals(assignment.get("status"), "reviewing"))) {
      return "reviewing";
    }
    if (assignments.stream().anyMatch(assignment -> Objects.equals(assignment.get("status"), "finished"))) {
      return "finished";
    }
    return "excellent";
  }

  private List<Map> getAssignmentsStatus(List<Assignment> currentTaskAssignment, User user) {
    List result = new ArrayList();
    for (Assignment assignment : currentTaskAssignment) {
      List<ReviewQuiz> submitAssignments = reviewQuizRepository.findByAssignmentIdAndStudentId(assignment.getId(), user.getId());
      Map map = oMapper.convertValue(assignment, Map.class);
      map.put("status", assignmentService.getAssignmentStatus(assignment, submitAssignments));
      result.add(map);
    }
    return result;
  }

  private List<Task> getAllTasks(List<Topic> topics) {
    List<Long> topicIds = topics.stream().map(Topic::getId).collect(Collectors.toList());
    return taskRepository.findByTopicIds(topicIds);
  }

  private List<Assignment> getAllAssignments(List<Task> tasks) {
    List<Long> taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());
    return assignmentRepository.findByTaskIds(taskIds);
  }
}
