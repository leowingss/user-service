package academy.devdojo.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProfileControllerIt {

    private static final String URL = "/v1/profiles";

    @Autowired
    private TestRestTemplate restTemplate;

//    @Test
//    @DisplayName("GET v1/profiles returns a list with all profiles when argument is null")
//    @Order(1)
//    void findAll_ReturnsAllProfiles_WhenArgumentIsNull() throws Exception {
//
//
//    }
}
