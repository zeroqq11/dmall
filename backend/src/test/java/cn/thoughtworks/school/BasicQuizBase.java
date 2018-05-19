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
public class BasicQuizBase {
    @Autowired
    WebApplicationContext wac;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private FlywayService flywayService;
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
        Task task = new Task();
        taskRepository.save(task);
        Assignment assignment = new Assignment();
        assignment.setTaskId(task.getId());
        assignmentRepository.save(assignment);

    }

}

