package com.prolog.prologbackend.Notes.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class NotesDetaillDTO {

    @Schema(description = "팀 멤버를 구분하는 번호입니다. 일지 수정에 용이", nullable = false, example = "1")
    private Long memberId;

    @Schema(description = "일지를 구분하는 번호입니다.", nullable = false, example = "1")
    private Long notesId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "프로젝트 일지의 날짜입니다.", nullable = false, example = "2024-03-11")
    private Date date;

    @Schema(description = "일지 제목입니다.", nullable = false, example = "~~~ 회고")
    private String title;

    @Schema(description = "일지의 요약?입니다.", nullable = false, example = "~~ 프로젝트 회고")
    private String summary;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "프로젝트 종료 날짜입니다.", nullable = false, example = "2024-03-11")
    private Date createdDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "프로젝트 종료 날짜입니다.", nullable = false, example = "2024-03-11")
    private Date modifiedDate;

    @Schema(description = "일지 타입입니다.", nullable = false, example = "Blog")
    private NotesType type;

    @Schema(description = "일지의 내용입니다.", nullable = false, example = "~~~")
    private String content;
}
