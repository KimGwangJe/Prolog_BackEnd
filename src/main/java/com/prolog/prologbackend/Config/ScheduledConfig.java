package com.prolog.prologbackend.Config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.Service.MemberService;
import com.prolog.prologbackend.Notes.Domain.Image;
import com.prolog.prologbackend.Notes.Repository.ImageRepository;
import com.prolog.prologbackend.Notes.Service.NotesService;
import com.prolog.prologbackend.Project.Domain.Project;
import com.prolog.prologbackend.Project.Repository.ProjectRepository;
import com.prolog.prologbackend.Project.Service.ProjectService;
import com.prolog.prologbackend.TeamMember.Domain.TeamMember;
import com.prolog.prologbackend.TeamMember.Service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduledConfig {

    private final ImageRepository imageRepository;

    private final AmazonS3 s3Client;

    private final ProjectRepository projectRepository;

    private final TeamMemberService teamMemberService;

    private final ProjectService projectService;

    private final NotesService notesService;

    private final MemberService memberService;

    @Value("${S3Bucket}")
    private String bucket;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 새벽 12시에 실행
    @Transactional(rollbackFor = Exception.class)
    public void deleteImageToS3AndDB() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String todayFormatted = today.format(formatter);
        String yesterdayFormatted = yesterday.format(formatter);

        // 오늘과 어제의 이미지를 제외한 나머지 중 notes_id가 null인 이미지 가져오기
        List<Image> imagesToDelete = imageRepository.findByNotesIsNullAndImageNameNotStartingWith(todayFormatted, yesterdayFormatted);

        for (Image image : imagesToDelete) {
            try {
                s3Client.deleteObject(new DeleteObjectRequest(bucket, image.getImageName()));
                imageRepository.delete(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //isDeleted가 true가 된지 일주일이 지난 프로젝트를 삭제
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void deleteProject() {
        LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);
        Timestamp oneWeekAgoTimestamp = Timestamp.valueOf(oneWeekAgo.atStartOfDay());

        List<Project> projects = projectRepository.findIdsByIsDeletedTrueAndModifiedDateBefore(oneWeekAgoTimestamp);

        List<Long> teamMemberIds = projects.stream()
                .flatMap(project -> project.getTeamMembers().stream())
                .map(TeamMember::getId)
                .toList();

        List<Long> projectIds = projects.stream()
                .map(Project::getProjectId)
                .collect(Collectors.toList());

        notesService.deleteImageAndNotes(teamMemberIds);
        teamMemberService.deleteTeamMemberByIds(teamMemberIds);
        projectService.deleteProjectAndProjectStep(projectIds);
    }

    //회원 탈퇴 후 일주일이 경과된 회원 정보를 DB에서 삭제
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deleteMember(){
        LocalDateTime dateTime = LocalDateTime.now().minusWeeks(1);

        List<Member> members = memberService.findDeletedMemberByModifiedDate(dateTime);

        if(members != null){
            List<Long> deletedMembers = members.stream().map(t -> t.getId()).toList();
            memberService.deleteMemberByIds(deletedMembers);
        }
    }
}

