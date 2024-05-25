package com.group1.quiz.dataTransferObject.UserDTO;

import com.group1.quiz.enums.UserRoleEnum;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserResponse {
    private String name;
    private String email;
    private UserRoleEnum role;
    private Date createdAt;
    private Date updatedAt;
}
