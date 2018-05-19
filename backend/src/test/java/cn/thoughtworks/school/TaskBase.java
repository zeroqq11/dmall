package cn.thoughtworks.school;

import cn.thoughtworks.school.programCenter.Application;
import cn.thoughtworks.school.programCenter.entities.Task;
import cn.thoughtworks.school.programCenter.entities.Topic;
import cn.thoughtworks.school.programCenter.repositories.ReviewQuizRepository;
import cn.thoughtworks.school.programCenter.repositories.TaskRepository;
import cn.thoughtworks.school.programCenter.repositories.TopicRepository;
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
public class TaskBase {
    @Autowired
    WebApplicationContext wac;

    @Autowired
    private TopicRepository topicRepository;
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
        }

        Long programId = (long) 1;
        String title = "title";
        String createTime = "2018-01-01 00:00:00.0";
        Long orderNumber = (long) 1;
        topicRepository.save(new Topic(programId, title, createTime, orderNumber, true));
        Long paperId = (long) 1;
        Long topicId = (long) 1;
        String content = "content";
        String deadLine = "2018-01-17 00:00:00.0";
        String link = "link";
        String type = "type";
        taskRepository.save(new Task(programId, paperId, topicId, orderNumber, title, content, deadLine, createTime, true, link, type));
        taskRepository.save(new Task(programId, paperId, topicId, orderNumber, title, content, deadLine, createTime, true, link, type));

        RestAssuredMockMvc.webAppContextSetup(wac);

    }
}
