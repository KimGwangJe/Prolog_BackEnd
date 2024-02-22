package com.prolog.prologbackend.Project.EmailTest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * Author : Kim
 * Date : 2024-02-11
 * Description : Email 전송 관련 테스트 CONTROLLER입니다.
*/
@RestController
@RequiredArgsConstructor
public class EmailTestController {
    private final EmailService emailService;

    @PostMapping("/emailsend")
    public boolean emailSend(@RequestBody EmailDTO emailDTO){
        boolean success = true;
        try{
            success = emailService.sendMailReject(emailDTO);
        } catch(Exception e){
            e.printStackTrace();
            success = false;
        }
        return success;
    }
}
