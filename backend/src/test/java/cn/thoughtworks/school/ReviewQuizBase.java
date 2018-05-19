package cn.thoughtworks.school;

import cn.thoughtworks.school.programCenter.Application;
import cn.thoughtworks.school.programCenter.entities.Assignment;
import cn.thoughtworks.school.programCenter.repositories.AssignmentRepository;
import cn.thoughtworks.school.programCenter.services.FlywayService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

@Ignore
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class ReviewQuizBase {
    @Autowired
    WebApplicationContext wac;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private static Long USER_ID = 21L;
    @Autowired
    private static Long ASSIGNMENT_ID = 1L;
    @Autowired
    private static Long QUIZ_ID = 1L;
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
        assignmentRepository.save(new Assignment());
    }

}

