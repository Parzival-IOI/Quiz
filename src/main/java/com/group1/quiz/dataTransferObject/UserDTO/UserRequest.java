package com.group1.quiz.dataTransferObject.UserDTO;

import com.group1.quiz.enums.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRequest {
    private String username;
    private String password;
    private String email;
    private UserRoleEnum role;
}
