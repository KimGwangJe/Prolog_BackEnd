package com.prolog.prologbackend.Project.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseStackImageListDTO {

    @Schema(description = "스택 이미지 리스트", nullable = false, example = "리스트로 된 ResponseStackImageDTO")
    private List<ResponseStackImageDTO> stackImageList;

    @Getter
    @Setter
    public static class ResponseStackImageDTO{

        @Schema(description = "스택 이미지의 ID", nullable = false, example = "1")
        private Long stackId;

        @Schema(description = "스택 이미지의 이름", nullable = false, example = "React or 리액트")
        private String stackName;

        @Schema(description = "스택 이미지의 링크", nullable = false, example = "www.~~~")
        private String image;
    }

}
