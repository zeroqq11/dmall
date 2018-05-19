package cn.thoughtworks.school;

import cn.thoughtworks.school.programCenter.Application;
import cn.thoughtworks.school.programCenter.entities.Follow;
import cn.thoughtworks.school.programCenter.entities.Program;
import cn.thoughtworks.school.programCenter.entities.UserProgram;
import cn.thoughtworks.school.programCenter.repositories.FollowRepository;
import cn.thoughtworks.school.programCenter.repositories.ProgramRepository;
import cn.thoughtworks.school.programCenter.repositories.UserProgramRepository;
import cn.thoughtworks.school.programCenter.services.FlywayService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureStubRunner(ids = "cn.thoughtworks.school:backend:+:stubs:10002",
        repositoryRoot = "http://ec2-54-222-235-15.cn-north-1.compute.amazonaws.com.cn:8081/repository/maven-snapshots/")
@ActiveProfiles("test")
public class MyStudentBase {
    @Autowired
    WebApplicationContext wac;

    @Autowired
    private FlywayService flywayService;

    @Autowired
    private UserProgramRepository userProgramRepository;

    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private ProgramRepository programRepository;

    private static boolean cleanFlag = true;

    @Before
    public void setup() throws Exception {
        if (cleanFlag) {
            flywayService.migrateDatabase();
            initData();
            UserProgram userProgram = new UserProgram((long) 1, (long) 1);
            userProgramRepository.save(userProgram);
        }

        RestAssuredMockMvc.webAppContextSetup(wac);
    }

    private void initData() {
        Program program = new Program();
        program.setId(1L);
        programRepository.save(program);
        Follow follow = new Follow();
        follow.setTutorId(1L);
        follow.setStudentId(2L);
        follow.setProgramId(1L);
        followRepository.save(follow);
    }
}
