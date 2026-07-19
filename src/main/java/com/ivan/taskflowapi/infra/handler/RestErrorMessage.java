package com.ivan.taskflowapi.infra.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestErrorMessage {

    private Instant timeStamp;
    private int status;
    private String error;
    private String message;
}
