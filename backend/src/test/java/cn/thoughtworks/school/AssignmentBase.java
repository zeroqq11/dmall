package cn.thoughtworks.school;

import cn.thoughtworks.school.programCenter.Application;
import cn.thoughtworks.school.programCenter.entities.*;
import cn.thoughtworks.school.programCenter.repositories.*;
import cn.thoughtworks.school.programCenter.services.FlywayService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

@Ignore
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@AutoConfigureStubRunner(ids = "cn.thoughtworks.school:quizCenterBackend:+:stubs:10003",
        repositoryRoot = "http://ec2-54-222-235-15.cn-north-1.compute.amazonaws.com.cn:8081/repository/maven-snapshots/")
@ActiveProfiles("test")
public class AssignmentBase {
    @Autowired
    WebApplicationContext wac;
    @Autowired
    private FlywayService flywayService;
    @Autowired
    private ReviewQuizRepository reviewQuizRepository;
    @Autowired
    private ProgramRepository programRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;
    private static boolean initialized = false;
    private static Long TASK_ID;
    private static Long ASSIGNMENT_ID;
    private static Long PROGRAM_ID;
    private static Long TOPIC_ID;
    private static Long USER_ID = 21L;


    @Before
    public void setup() throws Exception {
        if (!initialized) {
            flywayService.migrateDatabase();
            initialized = true;
            initData();
        }
        RestAssuredMockMvc.webAppContextSetup(wac);
    }

    private void initData() {
        initProgram();
        initTopic();
        initTask();
        initAssignments();
        initAssignmentQuizzes();
        initReviewQuizzes();
    }

    private void initTopic() {
        Topic topic = new Topic();
        TOPIC_ID = topicRepository.save(topic).getId();
    }

    private void initProgram() {
        Program program = new Program();
        PROGRAM_ID = programRepository.save(program).getId();
    }

    private void initReviewQuizzes() {
        ReviewQuiz reviewQuiz = new ReviewQuiz();
        reviewQuiz.setAssignmentId(ASSIGNMENT_ID);
        reviewQuiz.setStudentId(USER_ID);
        reviewQuiz.setStatus("已提交");
        reviewQuizRepository.save(reviewQuiz);

        reviewQuiz.setId(null);
        reviewQuiz.setStatus("完成");
        reviewQuizRepository.save(reviewQuiz);

        reviewQuiz.setId(null);
        reviewQuiz.setStatus("优秀");
        reviewQuizRepository.save(reviewQuiz);
    }

    private void initAssignmentQuizzes() {
        List data = new ArrayList();
        for (int i = 1; i < 5; ++i) {
            AssignmentQuiz assignmentQuiz = new AssignmentQuiz();
            assignmentQuiz.setQuizId((long) i);
            assignmentQuiz.setAssignmentId(ASSIGNMENT_ID);
            data.add(assignmentQuiz);
        }
        assignmentRepository.save(data);
    }

    private void initAssignments() {
        Assignment assignment = new Assignment();
        assignment.setId(ASSIGNMENT_ID);
        assignment.setTitle("assignment");
        assignment.setType("SUBJECTIVE_QUIZ");
        assignment.setTaskId(TASK_ID);
        assignment.setCreatorId(USER_ID);
        ASSIGNMENT_ID = assignmentRepository.save(assignment).getId();
        assignment.setId(null);
        assignmentRepository.save(assignment);
    }

    private void initTask() {
        Task task = new Task();
        task.setId(TASK_ID);
        task.setVisible(true);
        task.setProgramId(PROGRAM_ID);
        task.setTopicId(1L);
        TASK_ID = taskRepository.save(task).getId();
    }

}

