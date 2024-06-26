package uk.co.cloudmatica.proxyapi.repo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Jacksonized
@Builder
public class OfficeWrapper {

    @JsonProperty("items")
    List<Officer> officers;
}
