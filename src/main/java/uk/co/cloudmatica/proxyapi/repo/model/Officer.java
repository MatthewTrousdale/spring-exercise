package uk.co.cloudmatica.proxyapi.repo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonIgnoreProperties(ignoreUnknown = true)
@Jacksonized
@Builder
public class Officer {

    @JsonProperty("name")
    private String name;
    @JsonProperty("officer_role")
    private String officerRole;
    @JsonProperty("appointed_on")
    private String appointedOn;
    @JsonProperty("address")
    private Address address;
    @Getter
    @JsonInclude(NON_NULL)
    private String resigned_on;
}
