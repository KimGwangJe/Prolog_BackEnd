package com.prolog.prologbackend.Member.Service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.DTO.Request.MemberUpdateDto;
import com.prolog.prologbackend.Member.ExceptionType.MemberExceptionType;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.Repository.MemberRepository;
import com.prolog.prologbackend.TeamMember.Service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeamMemberService teamMemberService;
    private final AmazonS3Client amazonS3Client;
    @Value("${S3Bucket}")
    private String BUCKET;
    @Value("${BasicImageUrl}")
    private String IMAGE_URL;


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
                    t -> { throw new BusinessLogicException(MemberExceptionType.CONFLICT); }
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

    /**
     * 이미지 변경
     * : 기존 이미지를 삭제하고 새 이미지로 설정
     * 
     * @param email : 요청 보낸 사용자의 이메일
     * @param multipartFile : 변경할 이미지 파일
     */
    @Transactional
    public void updateProfileImage(String email, MultipartFile multipartFile){
        Member member = memberRepository.findByEmail(email).get();
        verifyFileType(multipartFile.getContentType());

        if(!member.isBasicImage() && !member.getProfileName().isBlank())
            deleteProfileImage(member.getProfileName());

        String fileName = createFileName(multipartFile.getOriginalFilename());
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(multipartFile.getContentType());
        objectMetaData.setContentLength(multipartFile.getSize());
        
        try {
            amazonS3Client.putObject(BUCKET, fileName, multipartFile.getInputStream(), objectMetaData);
        } catch (IOException e){
            throw new BusinessLogicException(MemberExceptionType.IMAGE_BAD_REQUEST);
        }
        
        String imageUrl = amazonS3Client.getUrl(BUCKET, fileName).toString();
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
        Member member = memberRepository.findByEmail(email).get();
        if(member.isBasicImage())
            throw new BusinessLogicException(MemberExceptionType.IMAGE_CONFLICT);
        deleteProfileImage(member.getProfileName());
        member.updateProfile(IMAGE_URL,null);
    }

    public Member getMember(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow( () -> new BusinessLogicException(MemberExceptionType.NOT_FOUND));
    }


    private void deleteProfileImage(String fileName){
        amazonS3Client.deleteObject(BUCKET,fileName);
    }

    private void verifyFileType(String type) {
        String[] typeList = {"image/jpeg","image/jpg","image/png"};
        List<String> strList = new ArrayList<>(Arrays.asList(typeList));
        if(!strList.contains(type))
            throw new BusinessLogicException(MemberExceptionType.IMAGE_BAD_REQUEST);
    }

    private String createFileName(String originalFileName){
        String type = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString().concat(type);
        return fileName;
    }
}
