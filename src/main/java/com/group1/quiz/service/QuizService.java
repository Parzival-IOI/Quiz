package com.group1.quiz.service;


import com.group1.quiz.dataTransferObject.AnswerRequest;
import com.group1.quiz.dataTransferObject.CreateQuizRequest;
import com.group1.quiz.dataTransferObject.QuestionRequest;
import com.group1.quiz.model.AnswerModel;
import com.group1.quiz.model.QuestionModel;
import com.group1.quiz.model.QuizModel;
import com.group1.quiz.repository.AnswerRepository;
import com.group1.quiz.repository.QuestionRepository;
import com.group1.quiz.repository.QuizRepository;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public void createQuiz(CreateQuizRequest createQuizRequest) throws Exception {
        QuizModel quizModel = new QuizModel();
        quizModel.setName(createQuizRequest.getName());
        quizModel.setDescription(createQuizRequest.getDescription());
        quizModel.setVisibility(createQuizRequest.getVisibility());
        quizModel.setUser_id(createQuizRequest.getUser_id());
        quizModel.setCreated_at(Date.from(Instant.now()));
        quizModel.setUpdated_at(Date.from(Instant.now()));
        quizRepository.save(quizModel);

        List<QuestionRequest> questionModelList = createQuizRequest.getQuestions();
        for (QuestionRequest questionRequest : questionModelList) {
            QuestionModel questionModel = new QuestionModel();
            questionModel.setQuiz_id(quizModel.getId());
            questionModel.setQuestion(questionRequest.getQuestion());
            questionModel.setType(questionRequest.getType());
            questionModel.setCreated_at(Date.from(Instant.now()));
            questionModel.setUpdated_at(Date.from(Instant.now()));

            questionRepository.save(questionModel);

            List<AnswerRequest> answerRequests = questionRequest.getAnswers();
            for (AnswerRequest answerRequest : answerRequests) {
                AnswerModel answerModel = new AnswerModel();
                answerModel.setAnswer(answerRequest.getAnswer());
                answerModel.setQuestion_id(questionModel.getId());
                answerModel.set_correct(answerRequest.is_correct());
                answerModel.setCreated_at(Date.from(Instant.now()));
                answerModel.setUpdated_at(Date.from(Instant.now()));

                answerRepository.save(answerModel);

            }

        }

    }
}
