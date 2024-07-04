package com.group1.quiz.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "login")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoginModel {
    @Id
    private String id;

    @Indexed(unique = true)
    private String userName;

    private String refreshToken;

    @CreatedDate
    @Indexed(name="createdAt", expireAfterSeconds = 3600)
    private Date createdAt;
}
