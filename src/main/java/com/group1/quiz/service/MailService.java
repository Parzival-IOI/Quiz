package com.group1.quiz.service;

import com.group1.quiz.util.OTPGenerator;
import com.group1.quiz.util.ResponseStatusException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final OTPGenerator otpGenerator;
    private final TemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String mailUsername;

    public void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailUsername);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);

        System.out.println("send mail to " + to);
    }

    public String sendOTP(String to, String subject) throws Exception {
//        SimpleMailMessage message = new SimpleMailMessage();
        String otp = OTPGenerator.generate().get();

        Context context = new Context();
        context.setVariable("OTP", otp);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            mimeMessageHelper.setFrom(mailUsername);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            String MailTemplate = templateEngine.process("MailTemplate", context);
            mimeMessageHelper.setText(MailTemplate, true);

            mailSender.send(mimeMessage);

            System.out.println("send mail to " + to);
        } catch (Exception ex) {
            throw new ResponseStatusException("Can't send Mail", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return otp;
    }
}
