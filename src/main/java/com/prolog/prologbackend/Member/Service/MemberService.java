package com.prolog.prologbackend.Member.Service;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.DTO.Request.MemberUpdateDto;
import com.prolog.prologbackend.Member.ExceptionType.MemberExceptionType;
import com.prolog.prologbackend.Member.DTO.Request.MemberJoinDto;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.Domain.MemberStatus;
import com.prolog.prologbackend.Member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
     public void joinMember(MemberJoinDto joinDto){
        memberRepository.findByEmail(joinDto.getEmail())
                .ifPresent(m -> {throw new BusinessLogicException(MemberExceptionType.MEMBER_CONFLICT);});
        Member newMember = Member.builder()
                        .email(joinDto.getEmail())
                .password(passwordEncoder.encode(joinDto.getPassword()))
                .phone(joinDto.getPhone())
                .nickname(joinDto.getNickname())
                .isDeleted(false)
                .status(MemberStatus.UNVERIFIED)
                .profileImage("profileImageUrl")
                .profileName("basicProfileImage")
                .roles("ROLE_USER")
                .build();
        memberRepository.save(newMember);
    }

    /**
     * 회원 정보 수정
     * : 이메일, 비밀번호, 사용자명을 받아 현재 값과 비교하고 수정
     *
     * @param email : 요청 보낸 사용자의 이메일
     * @param dto : 요청 시 받은 변경을 원하는 사용자의 정보
     * @throws : 변경을 원하는 이메일이 이미 사용중인 경우 에러 발생 (409)
     */
    @Transactional
    public void updateMember(String email, MemberUpdateDto dto){
        Member member = memberRepository.findByEmail(email).get();

        if(!member.getEmail().equals(dto.getEmail()))
            memberRepository.findByEmail(dto.getEmail()).ifPresent(
                    t -> { throw new BusinessLogicException(MemberExceptionType.MEMBER_CONFLICT); }
            );

        member.updateEmail(dto.getEmail());
        member.updateNickname(dto.getNickname());
        if(!passwordEncoder.matches(dto.getPassword(),member.getPassword())) {
            String encodePassword = passwordEncoder.encode(dto.getPassword());
            member.updatePassword(encodePassword);
        }
    }
}
