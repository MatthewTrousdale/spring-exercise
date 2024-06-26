package uk.co.cloudmatica.proxyapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uk.co.cloudmatica.proxyapi.repo.model.Company;

import java.util.List;

@Builder
public class CompanyDto {

    @JsonProperty("total_results")
    @Setter
    private Integer totalResults;
    @Getter
    @JsonProperty("items")
    private List<Company> companies;
}
