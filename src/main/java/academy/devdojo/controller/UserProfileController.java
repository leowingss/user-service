package academy.devdojo.controller;

import academy.devdojo.domain.UserProfile;
import academy.devdojo.mapper.UserMapper;
import academy.devdojo.mapper.UserProfileMapper;
import academy.devdojo.request.UserPostRequest;
import academy.devdojo.request.UserPutRequest;
import academy.devdojo.response.UserGetResponse;
import academy.devdojo.response.UserPostResponse;
import academy.devdojo.response.UserProfileGetResponse;
import academy.devdojo.service.UserProfileService;
import academy.devdojo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/user-profiles")
@Slf4j
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService service;
    private final UserProfileMapper mapper;

    @GetMapping
    public ResponseEntity<List<UserProfileGetResponse>> findAll() {
        log.debug("Request received to a list all users profiles");

        var userProfiles = service.findAll();
        var userProfileGetResponse = mapper.toUserProfileGetResponse(userProfiles);

        return ResponseEntity.ok(userProfileGetResponse);

    }

}
