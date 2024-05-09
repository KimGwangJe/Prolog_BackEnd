package com.prolog.prologbackend.Member.Service.Facade;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.DTO.Request.PasswordUpdateDto;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.ExceptionType.MemberExceptionType;
import com.prolog.prologbackend.Member.Repository.SearchRedisRepository;
import com.prolog.prologbackend.Member.Service.MemberService;
import com.prolog.prologbackend.Member.Service.Other.MailService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchMemberFacadeService {
    private final MemberService memberService;
    private final MailService mailService;
    private final SearchRedisRepository searchRedisRepository;
    private final PasswordEncoder passwordEncoder;


    /**
     * 이메일 찾기
     * : 받은 정보로 회원 조회 후 이메일 반환
     *
     * @param nickname : 회원의 닉네임
     * @param phone : 회원의 핸드폰 번호
     * @return : 일치하는 회원의 이메일이 담긴 map
     * @throws : 일치하는 회원이 없는 경우 에러 발생 (404)
     */
    public Map<String, String> findEmail(String nickname, String phone){
        Member member = memberService.getNotDeletedMemberByNickname(nickname);
        if(!member.getPhone().equals(phone))
            throw new BusinessLogicException(MemberExceptionType.NOT_FOUND);
        Map<String, String> email = new HashMap<>();
        email.put("email", member.getEmail());
        return email;
    }

    /**
     * 비밀번호 재발급 시 인증 번호 발급
     * : 사용자 인증을 위해 등록된 회원 이메일로 인증번호 메일 전송
     *
     * @param email : 회원의 이메일
     */
    public void issueCertificationNumber(String email){
        Member member = memberService.getNotDeletedMemberByEmail(email);
        if(!member.getStatus().isBasicMember())
            throw new BusinessLogicException(MemberExceptionType.NOT_FOUND);

        int num = (int) (Math.random() * 9000) + 1000;
        String code = String.valueOf(num);

        try {
            mailService.sendIssueCertificationNumber(member.getEmail(), code);
        } catch (MessagingException e){
            throw new BusinessLogicException(MemberExceptionType.INTERNAL_SERVER_ERROR);
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
        Member member = memberService.getNotDeletedMemberByEmail(email);
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
        Member member = memberService.getNotDeletedMemberByEmail(email);
        if(!member.getNickname().equals(nickname))
            throw new BusinessLogicException(MemberExceptionType.BAD_REQUEST);
        searchRedisRepository.checkCertificationStatus(member.getEmail());
    }

    /**
     * 비밀번호 재설정
     *
     * @param passwordUpdateDto : 재설정에 필요한 회원의 정보가 담긴 클래스
     */
    @Transactional
    public void updatePassword(PasswordUpdateDto passwordUpdateDto){
        Member member = memberService.getNotDeletedMemberByEmail(passwordUpdateDto.getEmail());
        searchRedisRepository.checkCertificationStatus(member.getEmail());
        String encodePassword = passwordEncoder.encode(passwordUpdateDto.getPassword());
        member.updatePassword(encodePassword);
    }
}
