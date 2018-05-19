package cn.thoughtworks.school;

import cn.thoughtworks.school.programCenter.Application;
import cn.thoughtworks.school.programCenter.entities.TaskFelling;
import cn.thoughtworks.school.programCenter.repositories.TaskFeelingRepository;
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
public class TaskFeelingBase {

    @Autowired
    WebApplicationContext wac;
    @Autowired
    private TaskFeelingRepository taskFeelingRepository;
    @Autowired
    private FlywayService flywayService;
    private static boolean initialized = false;

    @Before
    public void setUp() throws Exception {
        if (!initialized) {
            flywayService.migrateDatabase();
            initialized = true;
        }
        initData();
        RestAssuredMockMvc.webAppContextSetup(wac);
    }

    private void initData() {
        TaskFelling taskFelling = new TaskFelling();
        taskFelling.setFeeling("good");
        taskFelling.setId(1L);
        taskFelling.setTaskId(1L);
        taskFelling.setStudentId(1L);
        taskFeelingRepository.save(taskFelling);
    }
}
