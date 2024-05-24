package com.group1.quiz.service;


import com.group1.quiz.dataTransferObject.answerDTO.AnswerRequest;
import com.group1.quiz.dataTransferObject.answerDTO.AnswerResponse;
import com.group1.quiz.dataTransferObject.questionDTO.QuestionResponse;
import com.group1.quiz.dataTransferObject.quizDTO.CreateQuizRequest;
import com.group1.quiz.dataTransferObject.questionDTO.QuestionRequest;
import com.group1.quiz.enums.QuizOrderEnum;
import com.group1.quiz.dataTransferObject.quizDTO.QuizResponse;
import com.group1.quiz.dataTransferObject.quizDTO.QuizTableResponse;
import com.group1.quiz.dataTransferObject.quizDTO.QuizzesResponse;
import com.group1.quiz.model.AnswerModel;
import com.group1.quiz.model.QuestionModel;
import com.group1.quiz.model.QuizModel;
import com.group1.quiz.enums.QuizVisibilityEnum;
import com.group1.quiz.model.UserModel;
import com.group1.quiz.repository.AnswerRepository;
import com.group1.quiz.repository.QuestionRepository;
import com.group1.quiz.repository.QuizRepository;
import com.group1.quiz.repository.UserRepository;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final MongoTemplate mongoTemplate;

    public List<QuizzesResponse> getSelfQuiz(Principal principal) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        if(userModel.isPresent()) {
            List<QuizModel> quizModels = quizRepository.findByUserId(userModel.get().getId());
            List<QuizzesResponse> quizzesResponses = new ArrayList<>();
            for(QuizModel quizModel : quizModels) {
                quizzesResponses.add(quizResponseMapping(quizModel));
            }
            return quizzesResponses;
        } else {
            return null;
        }
    }

    public QuizTableResponse getQuizzes(QuizOrderEnum orderBy, int page, int size, String search) throws Exception {
        Query query = new Query();
        long count;
        if(!StringUtils.isEmpty(search)) {
            query.addCriteria(Criteria.where("name").is(search));
        }

        query.with(Sort.by(Sort.Direction.ASC, orderBy.getValue()));

        query.with(PageRequest.of(page, size));

        List<QuizModel> quizModels = mongoTemplate.find(query, QuizModel.class);

        if (!StringUtils.isEmpty(search)) {
            count = quizModels.size();
        } else {
            count = quizRepository.countAllDocuments();
        }
        return QuizTableResponse.builder()
                .quizzes(quizModels.stream().map(this::quizResponseMapping).toList())
                .columns(count)
                .build();
    }

    private QuizzesResponse quizResponseMapping(QuizModel quizModel) {
        return QuizzesResponse.builder()
                .id(quizModel.getId())
                .name(quizModel.getName())
                .description(quizModel.getDescription())
                .visibility(String.valueOf(quizModel.getVisibility()))
                .createdAt(quizModel.getCreatedAt())
                .updatedAt(quizModel.getUpdatedAt())
                .build();
    }

    public QuizResponse getQuizById(String id) throws Exception {
        Optional<QuizModel> quizModel = quizRepository.findById(id);
        if(quizModel.isPresent()) {
            List<QuestionModel> questionModels = questionRepository.findByQuizId(quizModel.get().getId());
            List<QuestionResponse> questionResponses = new ArrayList<>();
            for(QuestionModel questionModel : questionModels) {
                List<AnswerModel> answerModels = answerRepository.findByQuestionId(questionModel.getId());
                List<AnswerResponse> answerResponses = answerModels.stream().map(this::answerResponseMapping).toList();
                questionResponses.add(questionResponseMapping(questionModel, answerResponses));
            }
            return quizResponseMapping(quizModel.get(), questionResponses);
        }
        else {
            throw new Exception("Quiz Not Found");
        }
    }

    private AnswerResponse answerResponseMapping(AnswerModel answerModel) {
        return AnswerResponse.builder()
                .id(answerModel.getId())
                .answer(answerModel.getAnswer())
                .isCorrect(answerModel.isCorrect())
                .createdAt(answerModel.getCreatedAt())
                .updatedAt(answerModel.getUpdatedAt())
                .build();
    }

    private QuestionResponse questionResponseMapping(QuestionModel questionModel, List<AnswerResponse> answerResponses) {
        return QuestionResponse.builder()
                .id(questionModel.getId())
                .question(questionModel.getQuestion())
                .type(questionModel.getType())
                .answers(answerResponses)
                .createdAt(questionModel.getCreatedAt())
                .updatedAt(questionModel.getUpdatedAt())
                .build();
    }

    private QuizResponse quizResponseMapping(QuizModel quizModel, List<QuestionResponse> questionResponses) {
        return QuizResponse.builder()
                .id(quizModel.getId())
                .name(quizModel.getName())
                .description(quizModel.getDescription())
                .visibility(quizModel.getVisibility())
                .questions(questionResponses)
                .createdAt(quizModel.getCreatedAt())
                .updatedAt(quizModel.getUpdatedAt())
                .build();
    }

    public void createQuiz(CreateQuizRequest createQuizRequest, Principal principal) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        if (userModel.isPresent()) {
            QuizModel quizModel = new QuizModel();
            quizModel.setName(createQuizRequest.getName());
            quizModel.setDescription(createQuizRequest.getDescription());
            quizModel.setVisibility(QuizVisibilityEnum.valueOf(createQuizRequest.getVisibility()));
            quizModel.setUserId(userModel.get().getId());
            quizModel.setCreatedAt(Date.from(Instant.now()));
            quizModel.setUpdatedAt(Date.from(Instant.now()));
            quizRepository.save(quizModel);

            List<QuestionRequest> questionModelList = createQuizRequest.getQuestions();
            for (QuestionRequest questionRequest : questionModelList) {
                QuestionModel questionModel = QuestionModel.builder()
                        .quizId(quizModel.getId())
                        .question(questionRequest.getQuestion())
                        .type(questionRequest.getType())
                        .createdAt(Date.from(Instant.now()))
                        .updatedAt(Date.from(Instant.now()))
                        .build();

                questionRepository.save(questionModel);

                List<AnswerRequest> answerRequests = questionRequest.getAnswers();
                for (AnswerRequest answerRequest : answerRequests) {
                    AnswerModel answerModel = new AnswerModel();
                    answerModel.setAnswer(answerRequest.getAnswer());
                    answerModel.setQuestionId(questionModel.getId());
                    answerModel.setCorrect(answerRequest.isCorrect());
                    answerModel.setCreatedAt(Date.from(Instant.now()));
                    answerModel.setUpdatedAt(Date.from(Instant.now()));

                    answerRepository.save(answerModel);
                }
            }
        }
        else {
            throw new Exception("User not found");
        }

    }

}
