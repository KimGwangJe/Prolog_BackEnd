package com.prolog.prologbackend.Member.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.DTO.Request.KaKaoInfoDto;
import com.prolog.prologbackend.Member.DTO.Request.MemberJoinDto;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.Domain.MemberStatus;
import com.prolog.prologbackend.Member.ExceptionType.MemberExceptionType;
import com.prolog.prologbackend.Member.Repository.MemberRepository;
import com.prolog.prologbackend.Security.Jwt.JwtProvider;
import com.prolog.prologbackend.Security.Jwt.JwtType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnyMemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final String ContentType = "application/x-www-form-urlencoded;charset=utf-8";
    @Value("${kakao.id}")
    private String CLIENT_ID;
    @Value("${kakao.uri}")
    private String REDIRECT_URI;


    /**
     * 일반 회원가입
     * : 존재하지 않는 경우 받은 사용자 정보로 회원가입
     * : 이미 일반 회원인 경우 예외 처리, 소셜 회원의 경우 정보 추가
     *
     * @param joinDto : 회원 가입을 원하는 사용자의 정보
     */
    @Transactional
    public void joinMember(MemberJoinDto joinDto){
        Optional<Member> getMember = memberRepository.findByEmail(joinDto.getEmail());
        if(getMember.isPresent()){
            if(getMember.get().getStatus().isBasicMember())
                throw new BusinessLogicException(MemberExceptionType.MEMBER_CONFLICT);
            Member member = getMember.get();
            member.joinToBasic(passwordEncoder.encode(joinDto.getPassword()), joinDto.getPhone());
        } else {
            Member newMember = Member.builder()
                    .email(joinDto.getEmail())
                    .password(passwordEncoder.encode(joinDto.getPassword()))
                    .phone(joinDto.getPhone())
                    .nickname(joinDto.getNickname())
                    .isDeleted(false)
                    .isVerified(false)
                    .status(MemberStatus.BASIC)
                    .profileImage("profileImageUrl")
                    .profileName("basicProfileImage")
                    .roles("ROLE_USER")
                    .build();
            memberRepository.save(newMember);
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
    public Map<String, String> loginToKaKao(String code){
        String kakaoToken = getKakaoToken(code);

        KaKaoInfoDto infos = getKakaoInfo("Bearer "+kakaoToken);

        Long socialId = infos.getId();
        String email =  infos.getKakao_account().getEmail();
        String nickname = infos.getKakao_account().getProfile().getNickname();

        Optional<Member> getMember = memberRepository.findByEmail(email);
        if(!getMember.isPresent()){
            Member newMember = Member.builder()
                    .email(email)
                    .socialId(socialId)
                    .nickname(nickname)
                    .isDeleted(false)
                    .isVerified(false)
                    .status(MemberStatus.SOCIAL)
                    .profileImage("profileImageUrl")
                    .profileName("basicProfileImage")
                    .roles("ROLE_USER")
                    .build();

            memberRepository.save(newMember);
        } else if(!getMember.get().getStatus().isSocialMember()) {
            Member member = getMember.get();
            member.joinToSocial(socialId);
        }

        String accessToken = jwtProvider.createToken(JwtType.ACCESS_TOKEN,email);
        String refreshToken = jwtProvider.createToken(JwtType.REFRESH_TOKEN,email);

        Map<String, String> tokens = new HashMap<>();
        tokens.put(JwtType.ACCESS_TOKEN.getTokenType(), "Bearer "+accessToken);
        tokens.put(JwtType.REFRESH_TOKEN.getTokenType(), "Bearer "+refreshToken);

        return tokens;
    }

    /**
     * 이메일 중복 확인
     * : 이메일 수정 시 이미 사용중인 이메일인지 확인하여 수정 가능 여부 반환
     *
     * @param email : 중복 확인할 이메일
     * @return : 자신의 이메일인 경우 false, 변경 가능한 이메일인 경우 true 반환
     * @throws : 이미 존재하는 이메일의 경우 에러 발생 (409)
     */
    public boolean validateEmail(String email){
        return memberRepository.findByEmail(email).isPresent();
    }

    private String getKakaoToken(String code){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", ContentType);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", CLIENT_ID);
        body.add("redirect_uri", REDIRECT_URI);
        body.add("code", code);

        HttpEntity entity = new HttpEntity(body, headers);
        String requestUrl = "https://kauth.kakao.com/oauth/token";

        ResponseEntity<Map> response =
                restTemplate.exchange(requestUrl, HttpMethod.POST,entity, Map.class);

        return response.getBody().get("access_token").toString();
    }

    private KaKaoInfoDto getKakaoInfo(String kakaoToken){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", ContentType);
        headers.add("Authorization", kakaoToken);

        HttpEntity sec_entity = new HttpEntity(headers);
        String requestUrl = "https://kapi.kakao.com/v2/user/me";

        ResponseEntity<String> sec_response =
                restTemplate.exchange(requestUrl,HttpMethod.POST,sec_entity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        KaKaoInfoDto responseBody = null;
        try {
            responseBody = objectMapper.readValue(sec_response.getBody(), KaKaoInfoDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return responseBody;
    }
}
