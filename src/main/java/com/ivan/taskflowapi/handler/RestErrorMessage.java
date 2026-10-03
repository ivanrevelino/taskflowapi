package com.ivan.taskflowapi.handler;

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
public class RestErrorMessage {

    private Instant timeStamp;
    private int status;
    private String error;
    private String message;
    private Map<String, String> fields;
}
