package academy.devdojo.service;

import academy.devdojo.commons.ProfileUtils;
import academy.devdojo.domain.Profile;
import academy.devdojo.repository.ProfileRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProfileServiceTest {
    @InjectMocks
    private ProfileService service;

    @Mock
    private ProfileRepository repository;
    private List<Profile> profilesList;

    @InjectMocks
    private ProfileUtils profileUtils;

    @BeforeEach
    void init() {
        profilesList = profileUtils.newProfileList();
    }

    @Test
    @DisplayName("findAll returns a list with all profiles")
    @Order(1)
    void findAll_ReturnsAllProfiles_WhenSuccessfull() {
        BDDMockito.when(repository.findAll()).thenReturn(profilesList);
        var profiles = service.findAll();
        Assertions.assertThat(profiles).isNotNull().hasSameElementsAs(profilesList);
    }

    @Test
    @DisplayName("findAllPaginated returns a paginated list with all profiles")
    @Order(1)
    void findAllPaginated_ReturnsPaginatedProfiles_WhenSuccessfull() {
        var pageRequest = PageRequest.of(0, profilesList.size());
        var pageProfile = new PageImpl<>(profilesList, pageRequest, 1);
        BDDMockito.when(repository.findAll(BDDMockito.any(Pageable.class))).thenReturn(pageProfile);
        var profileFound = service.findAllPaginated(pageRequest);
        Assertions.assertThat(profileFound).isNotNull().hasSameElementsAs(profilesList);
    }

    @Test
    @DisplayName("save creates an profile")
    @Order(2)
    void save_CreatesProfile_WhenSuccessfull() {
        var profileToSaved = profileUtils.newProfileToSave();
        var profileSaved = profileUtils.newProfileSaved();

        BDDMockito.when(repository.save(profileToSaved)).thenReturn(profileSaved);

        var savedProfile = service.save(profileToSaved);

        Assertions.assertThat(savedProfile).isEqualTo(profileSaved).hasNoNullFieldsOrProperties();
    }


}