package academy.devdojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserPutRequest {
    @NotNull(message = "The field 'id' cannot be null")
    @Schema(description = "User's id", example = "1")
    private Long id;
    @NotBlank(message = "The field 'firstName' is required")
    @Schema(description = "User's first name", example = "Fulano")
    private String firstName; // null, ""
    @NotBlank(message = "The field 'lastName' is required")
    @Schema(description = "User's last name", example = "Santos")
    private String lastName;
    @NotBlank(message = "The field 'email' is required")
    @Schema(description = "User's email. Must be unique", example = "fulano@gmail.com")
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email is not valid")
    private String email;
}
