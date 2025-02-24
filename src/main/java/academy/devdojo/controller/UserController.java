package academy.devdojo.controller;

import academy.devdojo.response.UserGetResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    @GetMapping
    public ResponseEntity<List<UserGetResponse>> findAll(@RequestParam(required = false) String firstName){
        log.debug("Request received to a list all users, param first name '{}'", firstName);

        var users = service.findAll(firstName);

        var userGetResponse = mapper.toUserGetResponseList(users);

        return ResponseEntity.ok(userGetResponse);


    }
}
