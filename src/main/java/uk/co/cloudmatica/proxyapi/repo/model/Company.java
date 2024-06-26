package uk.co.cloudmatica.proxyapi.repo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Jacksonized
@Builder
@Getter
@Setter
@Document
public class Company {

    @Id
    @JsonProperty("company_number")
    private String companyNumber;
    @JsonProperty("company_type")
    private String companyType;
    private String title;
    @JsonProperty("company_status")
    private String companyStatus;
    @JsonProperty("date_of_creation")
    private String dateOfCreation;
    @JsonProperty("address")
    private Address address;
    @JsonProperty("officers")
    private List<Officer> officers;
}

