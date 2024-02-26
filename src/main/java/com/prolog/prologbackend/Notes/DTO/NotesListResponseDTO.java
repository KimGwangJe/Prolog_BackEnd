package com.prolog.prologbackend.Notes.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NotesListResponseDTO {

    @Schema(description = "프로젝트 팀멤버의 일지를 담은 리스트입니다.", nullable = true, example = "notesList:[]")
    private List<NotesDetaillDTO> notesList;
}
