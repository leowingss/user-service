package academy.devdojo.response;

public record CepGetResponse(String cep, String city, String neighborhood, String street, String service) {
}
