package com.prolog.prologbackend.Member.Service;

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
     public boolean joinMember(MemberJoinDto joinDto){
        if(memberRepository.findByEmail(joinDto.getEmail()).isPresent())
            return false;
        Member newMember = Member.builder()
                        .email(joinDto.getEmail())
                .password(passwordEncoder.encode(joinDto.getPassword()))
                .phone(joinDto.getPhone())
                .nickname(joinDto.getNickname())
                .isDeleted(false)
                .status(MemberStatus.UNVERIFIED)
                .profileImage("profileImageUrl")
                .profileName("basicProfileImage")
                .role("USER")
                .build();
        memberRepository.save(newMember);
        return true;
    }

}
