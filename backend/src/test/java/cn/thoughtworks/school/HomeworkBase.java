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

@Ignore
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@AutoConfigureStubRunner(ids = "cn.thoughtworks.school:userCenterBackend:+:stubs:10001", repositoryRoot = "http://ec2-54-222-235-15.cn-north-1.compute.amazonaws.com.cn:8081/repository/maven-snapshots/")
@ActiveProfiles("test")

public class HomeworkBase {
    @Autowired
    WebApplicationContext wac;

    @Autowired
    private ReviewQuizRepository reviewQuizRepository;

    @Autowired
    private FlywayService flywayService;
    private static boolean initialized = false;

    @Before
    public void setup() throws Exception {
        if (!initialized) {
            flywayService.migrateDatabase();
            initReviewQuizData();
            initialized = true;
        }
        RestAssuredMockMvc.webAppContextSetup(wac);
    }

    private void initReviewQuizData() {
        Long studentId = (long) 1;
        Long tutorId = (long) 1;
        Integer grade = 1;
        Long taskId = (long) 1;
        Long sectionId = (long) 1;
        String status = "未完成";
        Boolean excellence = true;
        String createTime = "2016-09-09 00:00:00.0";
        reviewQuizRepository.save(new ReviewQuiz(studentId, tutorId, grade, taskId, sectionId, status, excellence, createTime));
    }
}

