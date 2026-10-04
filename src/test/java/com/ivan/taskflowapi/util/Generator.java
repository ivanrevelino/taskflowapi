package com.ivan.taskflowapi.util;

import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.UserRoles;

import java.time.LocalDateTime;

public class Generator {

    public static User generateUser() {
        return User.builder()
                .name("Ivan")
                .username("srmbilane")
                .password("1224")
                .role(UserRoles.ADMIN)
                .build();
    }

    public static User generateTestUser(Long id, String name, String username) {
        return User.builder()
                .id(id)
                .name(name)
                .username(username)
                .password("123456")
                .role(UserRoles.USER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Project generateTestProject(Long id, String name, String description, User user) {
        return Project.builder()
                .id(id)
                .name(name)
                .description(description)
                .owner(user)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
