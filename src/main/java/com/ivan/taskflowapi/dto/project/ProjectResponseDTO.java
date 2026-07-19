package com.ivan.taskflowapi.dto.project;

import com.ivan.taskflowapi.models.User;

public record ProjectResponseDTO (String name, String description, User user){}
