package com.prolog.prologbackend.Project.Service;

import com.prolog.prologbackend.Project.DTO.Request.InvitationEmailDTO;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


/**
 * Author : Kim
 * Date : 2024-02-17
 * Description : 이메일 전송 관련 Service입니다.
 * 나중에 Prolog의 이메일을 생성하게 된다면 setFrom의 이메일을 변경해야 됩니다.
 * 02-17 Thymeleaf 사용으로 결정
 */

@Service
@RequiredArgsConstructor
public class InvitationEmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async
    public boolean sendMailReject(InvitationEmailDTO emailDTO) throws Exception {
        boolean msg = false;

        Context context = new Context();
        context.setVariable("nickname", emailDTO.getNickname());
        context.setVariable("message", "초대를 승락하기 위해서는 링크를 눌러주세요."); //이부분은 저희가 틀을 잡아서 보내는게 나을것 같습니다.
        context.setVariable("link", "http://localhost:8080/invitation"); // 리다이렉션 링크
        context.setVariable("data", "?userId="+emailDTO.getTargetId()+"&projectId="+emailDTO.getProjectId());
        context.setVariable("linkName", "여기를 클릭해주세요"); // 위 링크를 덧씌울 텍스트

        String message = templateEngine.process("InviteEmail.html", context);

        MimeMessage mail = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mail, false, "UTF-8"); // 2번째 인자는 Multipart여부 결정
        mimeMessageHelper.setFrom("gwangjeg14@gmail.com"); //누가
        mimeMessageHelper.setTo(emailDTO.getTargetMail()); //누구에게?
        mimeMessageHelper.setSubject("Prolog [" + emailDTO.getProjectName() + "] 프로젝트에 초대되었습니다"); //제목
        mimeMessageHelper.setText(message, true);

        try {
            javaMailSender.send(mail);
            msg = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }
}