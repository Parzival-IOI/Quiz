package com.group1.quiz.service;


import com.group1.quiz.dataTransferObject.AnswerDTO.AnswerRequest;
import com.group1.quiz.dataTransferObject.AnswerDTO.AnswerResponse;
import com.group1.quiz.dataTransferObject.PlayDTO.PlaysPlayerResponse;
import com.group1.quiz.dataTransferObject.QuestionDTO.QuestionResponse;
import com.group1.quiz.dataTransferObject.QuizDTO.CreateQuizRequest;
import com.group1.quiz.dataTransferObject.QuestionDTO.QuestionRequest;
import com.group1.quiz.dataTransferObject.QuizDTO.UpdateQuizRequest;
import com.group1.quiz.enums.OrderEnum;
import com.group1.quiz.enums.PlayOrderByEnum;
import com.group1.quiz.enums.QuizOrderByEnum;
import com.group1.quiz.dataTransferObject.QuizDTO.QuizResponse;
import com.group1.quiz.dataTransferObject.TableResponse;
import com.group1.quiz.dataTransferObject.QuizDTO.QuizzesResponse;
import com.group1.quiz.enums.UserRoleEnum;
import com.group1.quiz.model.AnswerModel;
import com.group1.quiz.model.PlayModel;
import com.group1.quiz.model.QuestionModel;
import com.group1.quiz.model.QuizModel;
import com.group1.quiz.enums.QuizVisibilityEnum;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
public class QuizService {
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final MongoTemplate mongoTemplate;
    private final PlayRepository playRepository;

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
            throw new ResponseStatusException("No Quiz", HttpStatus.NO_CONTENT);
        }
    }

    public TableResponse<QuizzesResponse> getQuizzes(QuizOrderByEnum orderBy, OrderEnum order, int page, int size, String search) throws Exception {
        long count;
        TableQueryBuilder tableQueryBuilder = new TableQueryBuilder(search, "name", orderBy.getValue(), order, page, size);

        List<QuizModel> quizModels = mongoTemplate.find(tableQueryBuilder.getQuery(), QuizModel.class);

        if (!StringUtils.isEmpty(search)) {
            Query query = Query.query(Criteria.where("name").regex(".*"+search+".*", "i"));
            count = mongoTemplate.find(query, QuizModel.class).size();
        } else {
            count = quizRepository.countAllDocuments();
        }
        return TableResponse.<QuizzesResponse>builder()
                .data(quizModels.stream().map(this::quizResponseMapping).toList())
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

    public QuizResponse getQuizById(String id, Principal principal) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        if(userModel.isPresent()) {
            Optional<QuizModel> quizModel = quizRepository.findById(id);
            if(quizModel.isPresent()) {
                if(quizModel.get().getUserId().equals(userModel.get().getId()) || userModel.get().getRole().equals(UserRoleEnum.ADMIN)) {
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
                    throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
                }
            }
            else {
                throw new ResponseStatusException("Quiz Not Found", HttpStatus.NOT_FOUND);
            }
        }
        else {
            throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
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

    public QuizResponse createQuiz(CreateQuizRequest createQuizRequest, Principal principal) throws Exception {
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

            Optional<QuizModel> quizModel1 = quizRepository.findById(quizModel.getId());
            if(quizModel1.isPresent()) {
                List<QuestionModel> questionModels1 = questionRepository.findByQuizId(quizModel1.get().getId());
                List<QuestionResponse> questionResponses = new ArrayList<>();
                for (QuestionModel questionModel : questionModels1) {
                    List<AnswerModel> answerModels = answerRepository.findByQuestionId(questionModel.getId());
                    List<AnswerResponse> answerResponses = answerModels.stream().map(this::answerResponseMapping).toList();
                    questionResponses.add(questionResponseMapping(questionModel, answerResponses));
                }
                return quizResponseMapping(quizModel1.get(), questionResponses);
            }
            return null;
        }
        else {
            throw new ResponseStatusException("User not found", HttpStatus.NOT_FOUND);
        }

    }

    public void updateQuiz(String id, UpdateQuizRequest updateQuizRequest, Principal principal) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        if(userModel.isPresent()) {
            Optional<QuizModel> quizModel = quizRepository.findById(id);
            if (quizModel.isPresent()) {
                if (quizModel.get().getUserId().equals(userModel.get().getId()) || userModel.get().getRole().equals(UserRoleEnum.ADMIN)) {
                    quizModel.get().setName(updateQuizRequest.getName());
                    quizModel.get().setDescription(updateQuizRequest.getDescription());
                    quizModel.get().setVisibility(updateQuizRequest.getVisibility());

                    quizRepository.save(quizModel.get());

                    List<PlayModel> playModels = playRepository.findByQuizId(quizModel.get().getId());
                    List<PlayModel> playModelsUpdate = playModels.stream().map(e -> this.UpdatePlayQuizName(e, quizModel.get().getName())).toList();
                    playRepository.saveAll(playModelsUpdate);
                } else {
                    throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
                }
            } else {
                throw new ResponseStatusException("Quiz Not Found", HttpStatus.NOT_FOUND);
            }
        }
        else {
            throw new ResponseStatusException("User not found", HttpStatus.NOT_FOUND);
        }
    }

    private PlayModel UpdatePlayQuizName(PlayModel playModel, String quizName) {
        playModel.setQuizName(quizName);
        return playModel;
    }

    public void deleteQuiz(String id, Principal principal) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        if(userModel.isPresent()) {
            Optional<QuizModel> quizModel = quizRepository.findById(id);
            if (quizModel.isPresent()) {
                // only Allow owner of quiz or admin
                if (quizModel.get().getUserId().equals(userModel.get().getId()) || userModel.get().getRole().equals(UserRoleEnum.ADMIN)) {
                    // delete all ( answers, questions, quiz, play)
                    List<PlayModel> playModels = playRepository.findByQuizId(id);
                    playRepository.deleteAll(playModels);
                    List<QuestionModel> questionModels = questionRepository.findByQuizId(id);
                    for (QuestionModel questionModel : questionModels) {
                        List<AnswerModel> answerModels = answerRepository.findByQuestionId(questionModel.getId());
                        answerRepository.deleteAll(answerModels);
                    }
                    questionRepository.deleteAll(questionModels);
                    quizRepository.deleteById(id);
                }
                else {
                    throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
                }
            }
            else {
                throw new ResponseStatusException("Quiz Not Found", HttpStatus.NOT_FOUND);
            }
        } else {
            throw new ResponseStatusException("User not found", HttpStatus.NOT_FOUND);
        }
    }

    public TableResponse<QuizzesResponse> getSelfQuiz2(QuizOrderByEnum orderBy, OrderEnum order, int page, int size, String search, Principal principal) throws Exception {
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

        List<QuizModel> quizModels = mongoTemplate.find(query, QuizModel.class);

        if (!StringUtils.isEmpty(search)) {
            Query queryCount = new Query().query(Criteria.where("userId").is(userModel.get().getId()))
                    .addCriteria(Criteria.where("name").regex(".*"+search+".*", "i"));
            count = mongoTemplate.find(queryCount, QuizModel.class).size();
        } else {
            count = quizRepository.findAllByUserId(userModel.get().getId()).size();
        }
        return TableResponse.<QuizzesResponse>builder()
                .data(quizModels.stream().map(this::quizResponseMapping).toList())
                .columns(count)
                .build();
    }

    public TableResponse<PlaysPlayerResponse> getSelfQuizPlayer(PlayOrderByEnum orderBy, OrderEnum order, int page, int size, String search, String quizId, Principal principal) throws Exception {
        if(quizId.isEmpty())
            throw new ResponseStatusException("Quiz Id Required", HttpStatus.BAD_REQUEST);

        long count;
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        if(userModel.isEmpty()) {
            throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("quizId").is(quizId));
        if(!StringUtils.isEmpty(search)) {
            query.addCriteria(Criteria.where("username").regex(".*"+search+".*", "i"));
        }
        if(order.equals(OrderEnum.DESC)) {
            query.with(Sort.by(Sort.Direction.DESC, orderBy.getValue()));
        } else if(order.equals(OrderEnum.ASC)) {
            query.with(Sort.by(Sort.Direction.ASC, orderBy.getValue()));
        }
        query.with(PageRequest.of(page, size));

        List<PlayModel> playModels = mongoTemplate.find(query, PlayModel.class);

        if (!StringUtils.isEmpty(search)) {
            Query queryCount = new Query().query(Criteria.where("quizId").is(quizId))
                    .addCriteria(Criteria.where("username").regex(".*"+search+".*", "i"));
            count = mongoTemplate.find(queryCount, PlayModel.class).size();
        } else {
            count = playRepository.findAllByQuizId(quizId).size();
        }
        return TableResponse.<PlaysPlayerResponse>builder()
                .data(playModels.stream().map(this::playsResponseMapping).toList())
                .columns(count)
                .build();
    }

    private PlaysPlayerResponse playsResponseMapping(PlayModel playModel) {
        return PlaysPlayerResponse.builder()
                .id(playModel.getId())
                .score(playModel.getScore())
                .quizId(playModel.getQuizId())
                .quizName(playModel.getQuizName())
                .username(playModel.getUsername())
                .createdAt(playModel.getCreatedAt())
                .updatedAt(playModel.getUpdatedAt())
                .build();
    }
}
