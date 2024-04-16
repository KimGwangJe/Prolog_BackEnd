package com.prolog.prologbackend.Project.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(title = "StackImageResponse : 프로젝트 스택 이미지 반환 DTO")
public class ResponseStackImageListDTO {

    @Schema(description = "스택 이미지 리스트", nullable = false, example = "리스트로 된 ResponseStackImageDTO")
    private List<ResponseStackImageDTO> stackImageList;
}
