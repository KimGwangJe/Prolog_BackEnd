package com.prolog.prologbackend.Notes.DTO.Response;

import com.prolog.prologbackend.Notes.DTO.NotesType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ResponseNotesDTO {

    @Schema(description = "일지를 구분하는 번호입니다.", example = "1")
    private Long notesId;

    @Schema(description = "프로젝트 일지의 날짜입니다.", nullable = false, example = "2024-03-11")
    private Date date;

    @NotBlank
    @Schema(description = "일지 제목입니다.",nullable = false, example = "~~~ 회고")
    private String title;

    @Schema(description = "일지의 요약입니다.", example = "~~ 프로젝트 회고")
    private String summary;

    @Schema(description = "일지 생성 날짜입니다.", nullable = false, example = "2024-03-11")
    private Date createdDate;

    @Schema(description = "일지 수정 날짜입니다.", nullable = false, example = "2024-03-11")
    private Date modifiedDate;

    @NotNull
    @Schema(description = "일지 타입입니다.", nullable = false, example = "Blog")
    private NotesType type;

    @Schema(description = "일지의 내용입니다.", example = "~~~")
    private String content;

    @Builder
    public ResponseNotesDTO(Long notesId, Date date, String title,
                            String summary, Date createdDate, Date modifiedDate,
                            NotesType type, String content
                            ){
        this.notesId = notesId;
        this.date = date;
        this.title = title;
        this.summary = summary;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.type = type;
        this.content = content;
    }
}