package com.prolog.prologbackend.Member.Service.Facade;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.DTO.Request.MemberUpdateDto;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.ExceptionType.MemberExceptionType;
import com.prolog.prologbackend.Member.Service.Other.ImageService;
import com.prolog.prologbackend.Member.Service.MemberService;
import com.prolog.prologbackend.Notes.Service.NotesService;
import com.prolog.prologbackend.Project.Service.ProjectService;
import com.prolog.prologbackend.TeamMember.Domain.TeamMember;
import com.prolog.prologbackend.TeamMember.Service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberFacadeService {
    private final MemberService memberService;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final NotesService notesService;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;
    @Value("${image.profileImage}")
    private String PROFILE_IMAGE_URL;


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
        Member member = memberService.getMemberByEmailWithoutThrowingException(email);

        if(!member.getNickname().equals(dto.getNickname())){
            if(!memberService.isPresentMemberByNickname(dto.getNickname()))
                member.updateNickname(dto.getNickname());
        }
        if(!passwordEncoder.matches(dto.getPassword(),member.getPassword())) {
            String encodePassword = passwordEncoder.encode(dto.getPassword());
            member.updatePassword(encodePassword);
        }
        member.updatePhoneNumber(dto.getPhone());
    }

    /**
     * 회원 탈퇴
     * : 회원 및 프로젝트 상태 변경
     * : 관련된 팀멤버 및 노트 엔티티 모두 삭제
     *
     * @param member : 요청 보낸 (탈퇴를 진행할) 사용자의 정보
     */
    @Transactional
    public void deleteMember(Member member){
        List<TeamMember> teamMembers = teamMemberService.getListByMember(member);

        List<Long> projectIds = teamMembers.stream().filter(t -> t.getPart().contains("Leader"))
                .map(t -> t.getProject().getProjectId()).toList();
        for(Long projectId : projectIds){
            projectService.deleteProject(projectId,member);
        }

        List<Long> teamMembersIds = teamMembers.stream().map(t -> t.getId()).toList();
        notesService.deleteImageAndNotes(teamMembersIds);
        teamMemberService.deleteTeamMemberByIds(teamMembersIds);

        memberService.updateMemberStatus(member.getId());
    }

    /**
     * 이미지 변경
     * : 기존 이미지를 삭제하고 새 이미지로 설정
     *
     * @param email : 요청 보낸 사용자의 이메일
     * @param multipartFile : 변경할 이미지 파일
     */
    @Transactional
    public void updateProfileImage(String email, MultipartFile multipartFile){
        Member member = memberService.getMemberByEmailWithoutThrowingException(email);

        if(!member.isBasicImage() && !member.getProfileName().isBlank())
            imageService.deleteProfileImage(member.getProfileName());

        String fileName = imageService.createProfileImage(multipartFile);
        String imageUrl = imageService.getImageUrl(fileName);
        member.updateProfile(imageUrl, fileName);
    }

    /**
     * 이미지 초기화
     * : 기존 이미지를 삭제한 후 기본 이미지로 설정
     *
     * @param email : 요청 보낸 사용자의 이메일
     */
    @Transactional
    public void resetProfileImage(String email) {
        Member member = memberService.getMemberByEmailWithoutThrowingException(email);
        if(member.isBasicImage())
            throw new BusinessLogicException(MemberExceptionType.IMAGE_CONFLICT);
        imageService.deleteProfileImage(member.getProfileName());
        member.updateProfile(PROFILE_IMAGE_URL,null);
    }
}
