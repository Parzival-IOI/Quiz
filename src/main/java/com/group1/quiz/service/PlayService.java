package com.group1.quiz.service;

import com.group1.quiz.dataTransferObject.PlayDTO.PlayAnswerResponse;
import com.group1.quiz.dataTransferObject.PlayDTO.PlayQuestionResponse;
import com.group1.quiz.dataTransferObject.PlayDTO.PlayQuizRequest;
import com.group1.quiz.dataTransferObject.PlayDTO.PlayQuizResponse;
import com.group1.quiz.enums.QuizVisibilityEnum;
import com.group1.quiz.model.AnswerModel;
import com.group1.quiz.model.QuestionModel;
import com.group1.quiz.model.QuizModel;
import com.group1.quiz.repository.AnswerRepository;
import com.group1.quiz.repository.PlayRepository;
import com.group1.quiz.repository.QuestionRepository;
import com.group1.quiz.repository.QuizRepository;
import com.group1.quiz.util.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayService {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final PlayRepository playRepository;
    private final MongoTemplate mongoTemplate;

    public PlayQuizResponse playQuiz(String id) throws Exception, ResponseStatusException {
        Optional<QuizModel> quizModel = quizRepository.findById(id);
        if(quizModel.isPresent()) {
            if(quizModel.get().getVisibility().equals(QuizVisibilityEnum.PRIVATE)) {
                throw new ResponseStatusException("Question is Private", HttpStatus.FORBIDDEN);
            }
            List<QuestionModel> questionModels = questionRepository.findByQuizId(quizModel.get().getId());
            List<PlayQuestionResponse> playQuestionResponses = questionModels.stream().map(this::playQuestionResponseMapping).toList();
            return playQuizResponseMapping(quizModel.get(), playQuestionResponses);
        }
        throw new ResponseStatusException("Quiz Not Found", HttpStatus.NOT_FOUND);
    }

    private PlayQuizResponse playQuizResponseMapping(QuizModel quizModel, List<PlayQuestionResponse> playQuestionResponses) {
        return PlayQuizResponse.builder()
                .id(quizModel.getId())
                .name(quizModel.getName())
                .description(quizModel.getDescription())
                .questions(playQuestionResponses)
                .build();
    }

    private PlayQuestionResponse playQuestionResponseMapping(QuestionModel questionModel) {
        List<AnswerModel> answerModels = answerRepository.findByQuestionId(questionModel.getId());
        List<PlayAnswerResponse> playAnswerResponses = answerModels.stream().map(this::playAnswerResponseMapping).toList();
        return PlayQuestionResponse.builder()
                .id(questionModel.getId())
                .question(questionModel.getQuestion())
                .type(questionModel.getType())
                .answers(playAnswerResponses)
                .build();
    }

    private PlayAnswerResponse playAnswerResponseMapping(AnswerModel answerModel) {
        return PlayAnswerResponse.builder()
                .id(answerModel.getId())
                .answer(answerModel.getAnswer())
                .build();
    }

    public void playQuizSummit(PlayQuizRequest playQuizRequest) throws Exception, ResponseStatusException {
        if (quizRepository.existsById(playQuizRequest.getId())) {

        }
        else {
            throw new ResponseStatusException("Quiz Not Found", HttpStatus.NOT_FOUND);
        }
    }
}
