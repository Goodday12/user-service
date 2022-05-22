package com.master.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
class UserServiceApplicationTests {

    private final TestRestTemplate testRestTemplate = new TestRestTemplate();

    @Test
    void contextLoads() {
//        testRestTemplate.postForEntity("/")
    }

}
