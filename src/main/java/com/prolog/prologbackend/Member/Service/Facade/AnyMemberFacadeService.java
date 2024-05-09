package com.prolog.prologbackend.Member.Service.Facade;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.DTO.Request.KaKaoInfoDto;
import com.prolog.prologbackend.Member.DTO.Request.MemberJoinDto;
import com.prolog.prologbackend.Member.DTO.Response.SimpleMemberDto;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.Domain.MemberStatus;
import com.prolog.prologbackend.Member.ExceptionType.MemberExceptionType;
import com.prolog.prologbackend.Member.Service.Other.KakaoService;
import com.prolog.prologbackend.Member.Service.MemberService;
import com.prolog.prologbackend.Member.Service.Other.MailService;
import com.prolog.prologbackend.Security.Jwt.JwtProvider;
import com.prolog.prologbackend.Security.Jwt.JwtType;
import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AnyMemberFacadeService {
    private final MemberService memberService;
    private final MailService mailService;
    private final KakaoService kakaoService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    @Value("${image.profileImage}")
    private String PROFILE_IMAGE_URL;


    /**
     * 일반 회원가입
     * : 존재하지 않는 경우 받은 사용자 정보로 회원가입
     * : 이미 일반 회원인 경우 예외 처리, 소셜 회원의 경우 정보 추가
     * : 이메일 인증을 위한 메일 전송
     *
     * @param joinDto : 회원 가입을 원하는 사용자의 정보
     */
    @Transactional
    public void joinMember(MemberJoinDto joinDto){
        Member getMember = memberService.getMemberByEmail(joinDto.getEmail());
        if(getMember != null){
            if(getMember.getStatus().isBasicMember())
                throw new BusinessLogicException(MemberExceptionType.CONFLICT);
            getMember.joinToBasic(passwordEncoder.encode(joinDto.getPassword()), joinDto.getPhone());
        } else {
            getMember = Member.builder()
                    .email(joinDto.getEmail())
                    .password(passwordEncoder.encode(joinDto.getPassword()))
                    .phone(joinDto.getPhone())
                    .nickname(joinDto.getNickname())
                    .isBasicImage(true)
                    .isDeleted(false)
                    .isVerified(false)
                    .status(MemberStatus.BASIC)
                    .profileImage(PROFILE_IMAGE_URL)
                    .roles("ROLE_USER")
                    .build();
            memberService.createMember(getMember);
        }

        String token = jwtProvider.createToken(JwtType.EMAIL_VERIFICATION, joinDto.getEmail());

        try {
            mailService.sendVerificationEmail(joinDto.getEmail(), token);
        } catch (MessagingException e){
            if(getMember.getStatus().isSocialMember())
                getMember.resetJoinToBasic();
            else
                memberService.deleteMember(getMember);
            throw new BusinessLogicException(MemberExceptionType.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 카카오 소셜 회원가입 및 로그인
     * : 받은 인가 코드로 토큰을 발급받아 사용자 정보 조회
     * : 회원가입이 되어있지 않은 경우 회원가입 후 로그인
     * : 이미 회원인 경우 소셜 회원 여부 확인 후 로그인
     *
     * @param code : 카카오 로그인에 사용할 인가코드
     * @return : 성공 시 발급 될 액세스 토큰과 리프레시 토큰
     */
    @Transactional
    public String[] loginToKaKao(String code){
        String kakaoToken = kakaoService.getKakaoToken(code);

        KaKaoInfoDto infos = kakaoService.getKakaoInfo("Bearer "+kakaoToken);

        String email =  infos.getKakao_account().getEmail();
        String nickname = infos.getKakao_account().getProfile().getNickname();

        Member getMember = memberService.getMemberByEmail(email);
        if(getMember == null){
            Member newMember = Member.builder()
                    .email(email)
                    .nickname(nickname)
                    .isBasicImage(true)
                    .isDeleted(false)
                    .isVerified(false)
                    .status(MemberStatus.SOCIAL)
                    .profileImage(PROFILE_IMAGE_URL)
                    .roles("ROLE_USER")
                    .build();

            memberService.createMember(newMember);
        } else if(!getMember.getStatus().isSocialMember()) {
            getMember.joinToSocial();
        }

        String accessToken = jwtProvider.createToken(JwtType.ACCESS_TOKEN,email);
        String refreshToken = jwtProvider.createToken(JwtType.REFRESH_TOKEN,email);

        String[] tokens = new String[2];
        tokens[0] = "Bearer "+accessToken;
        tokens[1] = "Bearer "+refreshToken;
        return tokens;
    }

    /**
     * 이메일 중복 확인
     * : 회원가입 시 이미 사용중인 이메일인지 확인하여 여부 반환
     *
     * @param email : 사용 여부를 확인할 이메일
     * @return : 사용 가능한 이메일인 경우 false, 이미 사용중인 이메일인 경우 true
     * @throws : 이미 존재하는 이메일의 경우 에러 발생 (409)
     */
    public boolean validateEmail(String email){
        return memberService.isPresentMemberByEmail(email);
    }

    /**
     * 닉네임 중복 확인
     * : 회원가입 및 회원 수정 시 사용 가능한 닉네임인지 확인
     *
     * @param nickname : 사용 여부를 확인할 닉네임
     * @return : 사용 가능한 닉네임인 경우 false, 이미 사용중인 닉네임인 경우 true
     * @throws : 이미 존재하는 이메일의 경우 에러 발생 (409)
     */
    public boolean validateNickname(String nickname){
        return memberService.isPresentMemberByNickname(nickname);
    }

    /**
     * 이메일 인증
     * : 토큰을 확인하여 회원의 이메일 인증을 진행
     *
     * @param token : 인증을 위해 발급된 토큰
     * @throws : 이미 인증한 경우 에러 발생 (409)
     */
    @Transactional
    public void verificationEmail(String token){
        Claims claims = jwtProvider.parseToken(token);
        jwtProvider.verifyType(JwtType.EMAIL_VERIFICATION, claims);
        String email = jwtProvider.getEmail(claims);
        Member member = memberService.getNotDeletedMemberByEmail(email);
        if(member.isVerified())
            throw new BusinessLogicException(MemberExceptionType.VERIFICATION_CONFLICT);
        member.setVerified();
    }

    /**
     * 초대할 팀원의 정보 조회
     * : 토큰을 확인하여 회원의 이메일 인증을 진행
     *
     * @param email : 조회할 팀원의 email
     * @throws : 이미 인증한 경우 에러 발생 (409)
     */
    public SimpleMemberDto getMemberByEmail(String email){
        Member member = memberService.getNotDeletedMemberByEmail(email);
        return SimpleMemberDto.of(member);
    }
}
