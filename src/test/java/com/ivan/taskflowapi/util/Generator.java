package com.ivan.taskflowapi.util;

import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.UserRoles;

public class Generator {

    public static User generateUser() {
        return User.builder()
                .name("Ivan")
                .username("srmbilane")
                .password("1224")
                .role(UserRoles.ADMIN)
                .build();
    }
}
