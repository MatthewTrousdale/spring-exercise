package uk.co.cloudmatica.proxyapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uk.co.cloudmatica.proxyapi.repo.model.Company;

import java.util.List;

import static com.fasterxml.jackson.annotation.Nulls.AS_EMPTY;

@Builder
public class CompanyDto {

    @JsonProperty("total_results")
    @Setter
    @JsonSetter(nulls = AS_EMPTY)
    private Integer totalResults;
    @Getter
    @JsonProperty("items")
    private List<Company> companies;
}
