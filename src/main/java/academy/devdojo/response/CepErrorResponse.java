package academy.devdojo.response;

import java.util.List;

public record CepErrorResponse(String name, String message, String type, List<ErroResponse> errors) {
}

record ErroResponse(String name, String message, String service) {
}