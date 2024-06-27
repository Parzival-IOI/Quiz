package com.group1.quiz.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "register")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RegisterModel {
    @Id
    private String id;
    private UserModel user;
    private String otp;
    private byte attempt;
    private byte resend;
    @Indexed(name="createdAt", expireAfterSeconds = 7200)
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
}
