package com.prolog.prologbackend.Member.Service;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.DTO.Request.MemberUpdateDto;
import com.prolog.prologbackend.Member.ExceptionType.MemberExceptionType;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.Repository.MemberRepository;
import com.prolog.prologbackend.TeamMember.Service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeamMemberService teamMemberService;


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

    /**
     * 회원 탈퇴
     * : 회원 상태 변경 전 관련된 팀멤버 엔티티 모두 삭제 선행
     *
     * @param member : 요청 보낸 (탈퇴를 진행할) 사용자의 정보
     */
    @Transactional
    public void removeMember(Member member){
        teamMemberService.removeTeamMemberByMember(member);
        LocalDateTime updateDate = LocalDateTime.now();
        memberRepository.updateMemberStatus(updateDate, member.getId());
    }

    public Member getMember(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow( () -> new BusinessLogicException(MemberExceptionType.MEMBER_NOT_FOUND));
    }
}
