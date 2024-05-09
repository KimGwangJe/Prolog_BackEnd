package com.prolog.prologbackend.Member.Service.Other;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    @Value("${mail.url.verification}")
    private String VERIFICATION_URL;
    @Value("${spring.mail.username}")
    private String EMAIL;


    public void sendIssueCertificationNumber(String email, String code) throws MessagingException {
        Context context = new Context();
        context.setVariable("code",code);


            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setFrom(EMAIL);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[prolog] 인증번호 발급");
            mimeMessageHelper.setText(templateEngine.process("issueCertificationNumber", context), true);

            javaMailSender.send(mimeMessage);
    }

    public void sendVerificationEmail(String email, String verificationToken) throws MessagingException {
        Context context = new Context();
        context.setVariable("link",VERIFICATION_URL+verificationToken);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setFrom(EMAIL);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[prolog] 이메일 인증");
            mimeMessageHelper.setText(templateEngine.process("verificationEmail", context), true);

            javaMailSender.send(mimeMessage);
    }
}
