package com.prolog.prologbackend.Project.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * Author : Kim
 * Date : 2024-02-11
 * Description : DB에 있는 생성일과 수정일은 사용자가 직접 수정이 불가능합니다.
*/
@Getter
@Setter
public class ProjectDetailRequestDTO {

    @Schema(description = "프로젝트를 구분하는 번호입니다.", nullable = false, example = "1")
    private int projectNum;

    @Schema(description = "프로젝트 이름입니다.", nullable = false, example = "prolog")
    private String projectName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "프로젝트 시작 날짜입니다.", nullable = false, example = "2024-02-11")
    private Date projectStartDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "프로젝트 종료 날짜입니다.", nullable = false, example = "2024-03-11")
    private Date projectFinishDate;

    @Schema(description = "프로젝트에 대한 설명입니다.", nullable = true, example = "자유형식")
    private String description;

    @Schema(description = "프로젝트에 사용되는 스택입니다.", nullable = true, example = "items: [{id: 1,name: Item 1}, ~~]")
    private List<String> projectStack;
}
