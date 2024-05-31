package com.group1.quiz.dataTransferObject.UserDTO;

import com.group1.quiz.enums.RegisterRoleEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRegisterRequest {
    private String username;
    private String password;
    private String email;
    private RegisterRoleEnum role;
}
