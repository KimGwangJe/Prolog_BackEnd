package com.prolog.prologbackend.Member.Service;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.ExceptionType.MemberExceptionType;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;


    //멤버 저장
    public void createMember(Member member){
        memberRepository.save(member);
    }

    //멤버 탈퇴 상태로 수정 : Id
    public void updateMemberStatus(Long id){
        LocalDateTime updateDate = LocalDateTime.now();
        memberRepository.updateMemberStatus(updateDate, id);
    }

    //멤버 삭제 : Entity
    public void deleteMember(Member member){
        memberRepository.delete(member);
    }

    //멤버 List 삭제 : Id List
    public void deleteMemberByIds(List<Long> deletedMembers){
        memberRepository.deleteAllByIdInBatch(deletedMembers);
    }

    //멤버 반환 : Id
    public Member getMember(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow( () -> new BusinessLogicException(MemberExceptionType.NOT_FOUND));
    }

    //탈퇴하지 않은 멤버인 경우 반환 : Email
    public Member getNotDeletedMemberByEmail(String email) {
        return memberRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new BusinessLogicException(MemberExceptionType.NOT_FOUND));
    }

    //멤버 조회 : Email
    public Member getMemberByEmailWithoutThrowingException(String email){
        return memberRepository.findByEmail(email).get();
    }

    //멤버 조회 후 탈퇴한 회원인지 확인 : Email
    public Member getMemberByEmail(String email){
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if(optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (member.isDeleted()){
                throw new BusinessLogicException(MemberExceptionType.LOCKED);
            }
            return member;
        }
        return null;
    }

    //이미 사용중인 이메일인지 여부 조회 : Email
    public boolean isPresentMemberByEmail(String email){
        return memberRepository.findByEmail(email).isPresent();
    }

    //멤버 반환 : Nickname
    public Member getNotDeletedMemberByNickname(String nickname){
        return memberRepository.findByNicknameAndIsDeletedFalse(nickname)
                .orElseThrow(() -> new BusinessLogicException(MemberExceptionType.NOT_FOUND));
    }

    //이미 사용중인 닉네임인지 여부 조회 : Nickname
    public boolean isPresentMemberByNickname(String nickname){
        return memberRepository.findByNickname(nickname).isPresent();
    }

    //특정 시점을 기준으로 탈퇴한 멤버 List 조회 : LocalDateTime
    public List<Member> findDeletedMemberByModifiedDate(LocalDateTime dateTime){
        return memberRepository.findAllByIsDeletedTrueAndModifiedDateBefore(dateTime);
    }
}
