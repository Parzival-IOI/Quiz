package com.group1.quiz.service;

import com.group1.quiz.dataTransferObject.PlayDTO.PlayAnswerResponse;
import com.group1.quiz.dataTransferObject.PlayDTO.PlayQuestionRequest;
import com.group1.quiz.dataTransferObject.PlayDTO.PlayQuestionResponse;
import com.group1.quiz.dataTransferObject.PlayDTO.PlayQuizRequest;
import com.group1.quiz.dataTransferObject.PlayDTO.PlayQuizResponse;
import com.group1.quiz.dataTransferObject.PlayDTO.PlayResponse;
import com.group1.quiz.dataTransferObject.PlayDTO.PlaysResponse;
import com.group1.quiz.dataTransferObject.QuizDTO.QuizzesResponse;
import com.group1.quiz.dataTransferObject.TableResponse;
import com.group1.quiz.enums.OrderEnum;
import com.group1.quiz.enums.PlayOrderByEnum;
import com.group1.quiz.enums.QuizOrderByEnum;
import com.group1.quiz.enums.QuizVisibilityEnum;
import com.group1.quiz.enums.UserRoleEnum;
import com.group1.quiz.model.AnswerModel;
import com.group1.quiz.model.PlayModel;
import com.group1.quiz.model.QuestionModel;
import com.group1.quiz.model.QuizModel;
import com.group1.quiz.model.UserModel;
import com.group1.quiz.repository.AnswerRepository;
import com.group1.quiz.repository.PlayRepository;
import com.group1.quiz.repository.QuestionRepository;
import com.group1.quiz.repository.QuizRepository;
import com.group1.quiz.repository.UserRepository;
import com.group1.quiz.util.ResponseStatusException;
import com.group1.quiz.util.TableQueryBuilder;
import java.security.Principal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    private final UserRepository userRepository;

    public PlayQuizResponse playQuiz(String id) throws Exception {
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

    public int playQuizSummit(PlayQuizRequest playQuizRequest, Principal principal) throws Exception {
        int point = 0;
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        if (quizRepository.existsById(playQuizRequest.getId()) && userModel.isPresent()) {
            List<PlayQuestionRequest> questionRequests = playQuizRequest.getQuestions();
            for (PlayQuestionRequest questionRequest : questionRequests) {
                Optional<QuestionModel> questionModel = questionRepository.findById(questionRequest.getQuestionId());
                if(questionModel.isPresent()) {
//                    List<AnswerModel> answerModels = answerRepository.findByQuestionId(questionRequest.getQuestionId());
//                    for (AnswerModel answerModel : answerModels) {
//                        for(String answerId : questionRequest.getAnswerId()) {
//                            if(answerModel.getId().equals(answerId) && answerModel.isCorrect()) {
//                                point++;
//                            }
//                        }
//                    }
                    Optional<AnswerModel> answerModel = answerRepository.findById(questionRequest.getAnswerId());
                    if(answerModel.isPresent()) {
                        if(answerModel.get().isCorrect()) {
                            point++;
                        }
                    }
                }
            }
            Query query = Query.query(Criteria.where("userId").is(userModel.get().getId())).addCriteria(Criteria.where("quizId").is(playQuizRequest.getId()));
            PlayModel playModel = mongoTemplate.findOne(query, PlayModel.class);

            if(playModel != null) {
                playRepository.save(
                        PlayModel.builder()
                                .id(playQuizRequest.getId())
                                .score(point)
                                .userId(userModel.get().getId())
                                .quizId(playQuizRequest.getId())
                                .createdAt(Date.from(Instant.now()))
                                .updatedAt(Date.from(Instant.now()))
                                .build()
                );
            }
            else {
                playRepository.insert(
                        PlayModel.builder()
                                .score(point)
                                .userId(userModel.get().getId())
                                .quizId(playQuizRequest.getId())
                                .createdAt(Date.from(Instant.now()))
                                .updatedAt(Date.from(Instant.now()))
                                .build()
                );
            }

            return point;
        }
        else {
            throw new ResponseStatusException("Quiz/User Not Found", HttpStatus.NOT_FOUND);
        }
    }

    public PlayResponse findPlay(String id, Principal principal) throws Exception {
        Optional<PlayModel> playModel = playRepository.findById(id);
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        if(playModel.isPresent() && userModel.isPresent()) {
            if(playModel.get().getUserId().equals(userModel.get().getId()))
                return playResponseMapping(playModel.get());
            throw new ResponseStatusException("Permission Denied", HttpStatus.BAD_REQUEST);
        }
        else {
            throw new ResponseStatusException("Play Not Found", HttpStatus.NOT_FOUND);
        }
    }

    public PlayResponse playResponseMapping(PlayModel playModel) {
        return PlayResponse.builder()
                .id(playModel.getId())
                .score(playModel.getScore())
                .quizId(playModel.getQuizId())
                .createdAt(playModel.getCreatedAt())
                .updatedAt(playModel.getUpdatedAt())
                .build();
    }

    public TableResponse<PlaysResponse> getPlaysAdmin(PlayOrderByEnum orderBy, OrderEnum order, int page, int size, String search, Principal principal) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        if(userModel.isPresent()) {
            if (userModel.get().getId().equals(UserRoleEnum.ADMIN.getValue()))
                throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
        }
        long count;
        TableQueryBuilder tableQueryBuilder = new TableQueryBuilder(search, orderBy.getValue(), order, page, size);

        List<PlayModel> playModels = mongoTemplate.find(tableQueryBuilder.getQuery(), PlayModel.class);

        if (!StringUtils.isEmpty(search)) {
            count = playModels.size();
        } else {
            count = playRepository.countAllDocuments();
        }
        return TableResponse.<PlaysResponse>builder()
                .quizzes(playModels.stream().map(this::playsResponseMapping).toList())
                .columns(count)
                .build();
    }

    private PlaysResponse playsResponseMapping(PlayModel playModel) {
        return PlaysResponse.builder()
                .id(playModel.getId())
                .score(playModel.getScore())
                .quizId(playModel.getQuizId())
                .createdAt(playModel.getCreatedAt())
                .updatedAt(playModel.getUpdatedAt())
                .build();
    }

    public TableResponse<PlaysResponse> getPlays(PlayOrderByEnum orderBy, OrderEnum order, int page, int size, String search, Principal principal) throws Exception {
        long count;
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        if(userModel.isEmpty()) {
            throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userModel.get().getId()));
        if(!StringUtils.isEmpty(search)) {
            query.addCriteria(Criteria.where("name").regex(".*"+search+".*", "i"));
        }
        if(order.equals(OrderEnum.DESC)) {
            query.with(Sort.by(Sort.Direction.DESC, orderBy.getValue()));
        } else if(order.equals(OrderEnum.ASC)) {
            query.with(Sort.by(Sort.Direction.ASC, orderBy.getValue()));
        }
        query.with(PageRequest.of(page, size));

        List<PlayModel> playModels = mongoTemplate.find(query, PlayModel.class);

        if (!StringUtils.isEmpty(search)) {
            count = playModels.size();
        } else {
            count = playRepository.countAllDocuments();
        }
        return TableResponse.<PlaysResponse>builder()
                .quizzes(playModels.stream().map(this::playsResponseMapping).toList())
                .columns(count)
                .build();
    }

    public void deletePlay(String id, Principal principal) throws Exception {
        Optional<PlayModel> playModel = playRepository.findById(id);
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        if(playModel.isPresent() && userModel.isPresent()) {
            if(playModel.get().getUserId().equals(userModel.get().getId()) || userModel.get().getRole().equals( UserRoleEnum.ADMIN)) {
                playRepository.deleteById(id);
            }
            else {
                throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
            }
        }
        else {
            throw new ResponseStatusException("Play/User Not Found", HttpStatus.NOT_FOUND);
        }
    }
}
