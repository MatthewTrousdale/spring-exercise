package uk.co.cloudmatica.proxyapi.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ErrorResponseDto {

    private final String error;

    @JsonCreator
    public ErrorResponseDto(@JsonProperty("error") final String error) {
        this.error = error;
    }
}