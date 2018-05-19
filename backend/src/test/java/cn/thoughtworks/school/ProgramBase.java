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

public class ProgramBase {
    @Autowired
    WebApplicationContext wac;

    @Autowired
    private ProgramRepository programRepository;
    @Autowired
    private UserProgramRepository userProgramRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TutorProgramRepository tutorProgramRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private FlywayService flywayService;
    private static boolean initialized = false;

    @Before
    public void setup() throws Exception {
        if (!initialized) {
            flywayService.migrateDatabase();
            initProgramData();
            initFollowData();
            initTaskData();
            initUserProgramData();
            initTutorProgramData();
            initialized = true;
        }
        RestAssuredMockMvc.webAppContextSetup(wac);
    }

    private void initFollowData() {
        Follow follow = new Follow();
        follow.setProgramId(1L);
        follow.setStudentId(1L);
        follow.setTutorId(1L);
        followRepository.save(follow);
    }
    private void initProgramData() {
        String title = "program title";
        String createTime = "2016-09-09 00:00:00.0";
        String startTime = "2017-09-09 00:00:00.0";
        String endTime = "2017-09-09 00:00:00.0";
        Long creatorId = (long) 2;
        String introduction = "program introduction";
        String category = "付费";
        programRepository.save(new Program(title, createTime, startTime, endTime, creatorId, category, introduction));
        programRepository.save(new Program(title, createTime, startTime, endTime, creatorId, category, introduction));
    }

    private void initUserProgramData() {
        Long userId = (long) 1;
        Long programId = (long) 1;
        userProgramRepository.save(new UserProgram(userId, programId));
    }

    private void initTutorProgramData() {
        Long tutorId = (long) 1;
        Long programId = (long) 1;
        tutorProgramRepository.save(new TutorProgram(tutorId, programId));
    }

    private void initTaskData() {
        Long programId = (long) 1;
        Long paperId = (long) 1;
        Long topicId = (long) 1;
        Long orderNumber = (long) 1;
        String title = "task title";
        String content = "task content";
        String deadLine = "2017-09-09 00:00:00.0";
        String createTime = "2016-09-09 00:00:00.0";
        Boolean visible = true;
        String link = "http://www.baidu.com";
        String type = "必修";
        taskRepository.save(new Task(programId, paperId, topicId, orderNumber, title, content, deadLine, createTime, visible, link, type));
    }
}

