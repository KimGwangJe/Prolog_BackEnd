package com.prolog.prologbackend.Notes.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(title = "NotesListResponse : 일지 리스트 반환 DTO")
public class ResponseNotesListDTO {

    @Schema(description = "프로젝트 팀멤버의 일지를 담은 리스트입니다.", nullable = true,
            example = "200 -> 리스트로 된 ResponseNotesDTO, 204 -> 빈 리스트 반환")
    private List<ResponseNotesDTO> notesList;
}
