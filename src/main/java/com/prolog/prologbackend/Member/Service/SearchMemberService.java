package com.prolog.prologbackend.Member.Service;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.DTO.Request.PasswordUpdateDto;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.ExceptionType.MemberExceptionType;
import com.prolog.prologbackend.Member.Repository.MemberRepository;
import com.prolog.prologbackend.Member.Repository.SearchRedisRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class SearchMemberService {
    private final MemberRepository memberRepository;
    private final SearchRedisRepository searchRedisRepository;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;

    /**
     * 이메일 찾기
     * : 받은 정보로 회원 조회 후 이메일 반환
     *
     * @param nickname : 회원의 닉네임
     * @param phone : 회원의 핸드폰 번호
     * @return : 일치하는 회원의 이메일
     * @throws : 일치하는 회원이 없는 경우 에러 발생 (404)
     */
    public String findEmail(String nickname, String phone){
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new BusinessLogicException(MemberExceptionType.NOT_FOUND));
        if(!member.getPhone().equals(phone))
            throw new BusinessLogicException(MemberExceptionType.NOT_FOUND);
        return member.getEmail();
    }

    /**
     * 비밀번호 재발급 시 인증 번호 발급
     * : 사용자 인증을 위해 등록된 회원 이메일로 인증번호 메일 전송
     *
     * @param email : 회원의 이메일
     */
    public void issueCertificationNumber(String email){
        Member member = findMemberByEmail(email);
        if(!member.getStatus().isBasicMember())
            throw new BusinessLogicException(MemberExceptionType.NOT_FOUND);

        int num = (int) (Math.random() * 9000) + 1000;
        String code = String.valueOf(num);

        Context context = new Context();
        context.setVariable("code",code);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setFrom("mailaddress@gmail.com");
            mimeMessageHelper.setTo(member.getEmail());
            mimeMessageHelper.setSubject("[prolog] 인증번호 발급");
            mimeMessageHelper.setText(templateEngine.process("issueCertificationNumber", context), true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e){
            e.printStackTrace();
        }

        searchRedisRepository.saveCertificationNumber(member.getEmail(), code);
    }

    /**
     * 인증번호 확인
     * : 발급받은 인증번호와 일치하는지 확인 후 인증 여부 저장
     * 
     * @param email : 회원의 이메일
     * @param code : 입력한 인증번호
     */
    public void checkCertificationNumber(String email, int code){
        Member member = findMemberByEmail(email);
        searchRedisRepository.validateCertificationNumberByEmail(member.getEmail(), String.valueOf(code));
        searchRedisRepository.savePasswordCertification(member.getEmail());
    }

    /**
     * 비밀번호 재설정을 위한 인증 여부 확인
     * : 비밀번호 재설정에 필요한 정보 및 인증 절차를 거쳤는지 확인
     *
     * @param nickname : 회원의 닉네임
     * @param email : 회원의 이메일
     */
    public void checkCertificationStatus(String nickname, String email){
        Member member = findMemberByEmail(email);
        if(!member.getNickname().equals(nickname))
            throw new BusinessLogicException(MemberExceptionType.BAD_REQUEST);
        findCertificationStatusInRedis(member.getEmail());
    }

    /**
     * 비밀번호 재설정
     *
     * @param passwordUpdateDto : 재설정에 필요한 회원의 정보가 담긴 클래스
     */
    @Transactional
    public void updatePassword(PasswordUpdateDto passwordUpdateDto){
        Member member = findMemberByEmail(passwordUpdateDto.getEmail());
        findCertificationStatusInRedis(member.getEmail());
        String encodePassword = passwordEncoder.encode(passwordUpdateDto.getPassword());
        member.updatePassword(encodePassword);
    }


    private Member findMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(MemberExceptionType.NOT_FOUND));
        return member;
    }

    private void findCertificationStatusInRedis(String email){
        if(!searchRedisRepository.findCertificationStatus(email))
            throw new BusinessLogicException(MemberExceptionType.CODE_UNAUTHORIZED);
    }
}
