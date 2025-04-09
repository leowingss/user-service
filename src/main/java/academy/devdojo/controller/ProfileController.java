package academy.devdojo.controller;

import academy.devdojo.mapper.ProfileMapper;
import academy.devdojo.request.ProfilePostRequest;
import academy.devdojo.response.ProfileGetResponse;
import academy.devdojo.response.ProfilePostResponse;
import academy.devdojo.service.ProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/profiles")
@Slf4j
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class ProfileController {

  private final ProfileService service;
  private final ProfileMapper mapper;

  @GetMapping
  public ResponseEntity<List<ProfileGetResponse>> findAll() {
    log.debug("Request received to a list all profiles");

    var profiles = service.findAll();

    var profileGetResponse = mapper.toProfileGetResponseList(profiles);

    return ResponseEntity.ok(profileGetResponse);

  }

  @GetMapping("/paginated")
  public ResponseEntity<Page<ProfileGetResponse>> findAllPaginated(Pageable pageable) {
    log.debug("Request received to a list all profiles paginated");

    var pageProfileGetResponse = service.findAllPaginated(pageable).map(mapper::toProfileGetResponse);
    return ResponseEntity.ok(pageProfileGetResponse);

  }


  @PostMapping()
  public ResponseEntity<ProfilePostResponse> save(@RequestBody @Valid ProfilePostRequest profilePostRequest) {

    log.debug("Request to save profile: {}", profilePostRequest);

    var profile = mapper.toProfile(profilePostRequest);
    var profileSaved = service.save(profile);
    var profilePostResponse = mapper.toProfilePostResponse(profileSaved);

    return ResponseEntity.status(HttpStatus.CREATED).body(profilePostResponse);

  }

}
