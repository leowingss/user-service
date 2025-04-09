package academy.devdojo.service;

import academy.devdojo.domain.User;
import academy.devdojo.domain.UserProfile;
import academy.devdojo.repository.UserProfileRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {

  private final UserProfileRepository repository;

  public List<UserProfile> findAll() {
    return repository.findAll();
  }


  public List<User> findAllUsersByProfileId(Long id) {
    return repository.findAllUserByProfileId(id);
  }
}
