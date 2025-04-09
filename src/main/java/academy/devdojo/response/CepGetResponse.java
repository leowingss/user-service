package academy.devdojo.response;

import lombok.Builder;

@Builder
public record CepGetResponse(String cep, String city, String neighborhood, String street, String service) {

}
