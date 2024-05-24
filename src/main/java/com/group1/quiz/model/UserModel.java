package com.group1.quiz.model;

import com.group1.quiz.dataTransferObject.UserDto;
import com.group1.quiz.enums.UserRoleEnum;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(value = "Users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserModel {
    @Transient
    public static final String SEQUENCE_NAME = "projects_sequence";
    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    private String password;
    @Field(name="role")
    private UserRoleEnum role;
    private Date createdAt;
    private Date updatedAt;

    public UserModel(UserDto userDto) {
        this.username = userDto.getUsername();
        this.password = userDto.getPassword();
        this.role = userDto.getRole();
    }
}
