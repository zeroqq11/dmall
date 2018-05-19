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

import java.util.Date;

@Ignore
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@AutoConfigureStubRunner(ids = "cn.thoughtworks.school:userCenterBackend:+:stubs:10001",
        repositoryRoot = "http://ec2-54-222-235-15.cn-north-1.compute.amazonaws.com.cn:8081/repository/maven-snapshots/")
@ActiveProfiles("test")
public class ExcellentQuizCommentBase {
    @Autowired
    WebApplicationContext wac;
    @Autowired
    private FlywayService flywayService;
    @Autowired
    private ReviewQuizRepository reviewQuizRepository;
    @Autowired
    private ExcellentQuizCommentRepository sectionCommentRepository;
    @Autowired
    private SectionVideoAddressRepository sectionVideoAddressRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserProgramRepository userProgramRepository;

    private static boolean initialized = false;

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
        Long studentId = 1L;
        ReviewQuiz reviewQuiz = new ReviewQuiz();
        reviewQuiz.setExcellence(true);
        reviewQuiz.setAssignmentId(1L);
        reviewQuiz.setAssignmentId(1L);
        reviewQuiz.setQuizId(1L);
        reviewQuiz.setCreateTime("2017-12-22 01:07:59");
        reviewQuiz.setStudentId(studentId);
        reviewQuizRepository.save(reviewQuiz);

        ExcellentQuizComment excellentQuizComment = new ExcellentQuizComment();
        excellentQuizComment.setAssignmentId(1L);
        excellentQuizComment.setQuizId(1L);
        excellentQuizComment.setCommenterId(studentId);
        excellentQuizComment.setContent("test");
        excellentQuizComment.setStudentId(studentId);
        sectionCommentRepository.save(excellentQuizComment);

        SectionVideoAddress sectionVideoAddress = new SectionVideoAddress(1L,1L, new Date(),"https://media.w3.org/2010/05/sintel/trailer.mp4");
        sectionVideoAddressRepository.save(sectionVideoAddress);

        Task task = new Task();
        task.setId(1L);
        task.setProgramId(2L);
        taskRepository.save(task);

        UserProgram userProgram = new UserProgram();
        userProgram.setProgramId(2L);
        userProgram.setUserId(studentId);
        userProgramRepository.save(userProgram);
    }

}

