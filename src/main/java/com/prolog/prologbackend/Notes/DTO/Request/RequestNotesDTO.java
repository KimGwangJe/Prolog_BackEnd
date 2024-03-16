package com.prolog.prologbackend.Notes.DTO.Request;

import com.prolog.prologbackend.Notes.DTO.NotesType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RequestNotesDTO {
    @Schema(description = "일지 Id 업데이트 API시 사용. 생성시에는 null", example = "1")
    private Long notesId;

    @Schema(description = "팀 멤버 Id", example = "1")
    private Long memberId;

    @Schema(description = "프로젝트 일지의 날짜입니다.", nullable = false, example = "2024-03-11")
    private Date date;

    @NotBlank
    @Schema(description = "일지 제목입니다.",nullable = false, example = "~~~ 회고")
    private String title;

    @Schema(description = "일지의 요약입니다.", example = "~~ 프로젝트 회고")
    private String summary;

    @NotNull
    @Schema(description = "일지 타입입니다.", nullable = false, example = "Blog 또는 Section (대소문자 구분)")
    private NotesType type;

    @Schema(description = "일지의 내용입니다.", example = "HTML")
    private String content;
}

