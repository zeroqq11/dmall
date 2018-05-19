package cn.thoughtworks.school;

import cn.thoughtworks.school.programCenter.Application;
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
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureStubRunner(ids = "cn.thoughtworks.school:userCenterBackend:+:stubs:10001", repositoryRoot = "http://ec2-54-222-235-15.cn-north-1.compute.amazonaws.com.cn:8081/repository/maven-snapshots/")
@ActiveProfiles("test")
public class UserBase {

    @Autowired
    WebApplicationContext wac;

    @Before
    public void setup() throws Exception {
        RestAssuredMockMvc.webAppContextSetup(wac);
    }
}
