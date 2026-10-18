package com.ivan.taskflowapi.handler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Standard error response returned by the API.")
public class RestErrorMessage {

    @Schema(description = "Time when the error occurred.", example = "2025-01-15T09:30:00Z")
    private Instant timeStamp;
    @Schema(description = "HTTP status code.", example = "400")
    private int status;
    @Schema(description = "Short error category.", example = "Bad Request")
    private String error;
    @Schema(description = "Human-readable error detail.", example = "Request validation failed")
    private String message;
    @Schema(description = "Field-level validation errors, when available.")
    private Map<String, String> fields;
}
