package academy.devdojo.service;

import academy.devdojo.domain.Profile;
import academy.devdojo.domain.UserProfile;
import academy.devdojo.repository.ProfileRepository;
import academy.devdojo.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository repository;

    public List<UserProfile> findAll() {
        return repository.findAll();
    }


}
