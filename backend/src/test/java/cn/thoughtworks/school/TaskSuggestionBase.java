package cn.thoughtworks.school;

import cn.thoughtworks.school.programCenter.Application;
import cn.thoughtworks.school.programCenter.entities.TaskSuggestion;
import cn.thoughtworks.school.programCenter.repositories.TaskSuggestionRepository;
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
public class TaskSuggestionBase {


    @Autowired
    WebApplicationContext wac;

    @Autowired
    private TaskSuggestionRepository taskSuggestionRepository;

    @Autowired
    private FlywayService flywayService;

    private static boolean initialized = false;

    @Before
    public void setup() throws Exception {

        if (!initialized) {
            flywayService.migrateDatabase();
            initialized = true;
        }

        Long taskId = (long) 1;
        Long studentId = (long) 1;
        String content = "content";
        String createTime = "2018-01-23 00:00:00.0";
        taskSuggestionRepository.save(new TaskSuggestion((long) 1, taskId, studentId, content, createTime));

        RestAssuredMockMvc.webAppContextSetup(wac);

    }

}
