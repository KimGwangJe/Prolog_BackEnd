package com.prolog.prologbackend.Project.Service;

import com.prolog.prologbackend.Project.DTO.Request.InvitationEmailDTO;
import com.prolog.prologbackend.TeamMember.Domain.Part;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;


@Service
@RequiredArgsConstructor
public class InvitationEmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${Redirection}")
    private String redirectionLink;

    @Async
    public void sendMail(InvitationEmailDTO emailDTO) throws Exception {
        List<Part> partList = emailDTO.getPartList();
        StringBuilder partListString = new StringBuilder();

        for (Part part : partList) {
            if (partListString.length() > 0) {
                partListString.append("&");
            }
            partListString.append(part.toString());
        }

        Context context = new Context();
        context.setVariable("nickname", emailDTO.getNickname());
        context.setVariable("message", "초대를 승락하기 위해서는 링크를 눌러주세요."); //이부분은 저희가 틀을 잡아서 보내는게 나을것 같습니다.
        context.setVariable("link", redirectionLink+"/invitation"); // 리다이렉션 링크
        context.setVariable("data", "?userId="+emailDTO.getTargetId()+"&projectId="+emailDTO.getProjectId()+"&part=" + partListString.toString());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}