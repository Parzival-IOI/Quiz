package com.group1.quiz.model;

import com.group1.quiz.dataTransferObject.PlayDTO.PlayQuestionRequest;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Document(value="plays")
@Builder
@Data
public class PlayModel {
    @Id
    private String id;
    private double score;
    private String username;
    private String quizId;
    private String quizName;
    private List<PlayQuestionRequest> answers;
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
    @Version
    private Integer version;
}
