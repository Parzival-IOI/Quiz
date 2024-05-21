package com.group1.quiz.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
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
    private String user_id;
    private String quiz_id;
    private Date created_at;
    private Date updated_at;
}
