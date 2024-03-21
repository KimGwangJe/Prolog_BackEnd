package com.prolog.prologbackend.TeamMember.DTO.Request;

import com.prolog.prologbackend.TeamMember.Domain.Part;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Schema(title = "TeamMemberRequest : 팀멤버 등록 DTO")
@Getter
public class CreateTeamMemberDto {
    @Schema(description = "회원 id", example = "1")
    @Min(1)
    private Long memberId;
    @Schema(description = "프로젝트 id", example = "2")
    @Min(1)
    private Long projectId;
    @Schema(description = "역할 목록", example = "[Leader, Backend]")
    @NotNull
    private List<Part> parts;
}
