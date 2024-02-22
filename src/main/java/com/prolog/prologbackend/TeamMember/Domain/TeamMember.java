package com.prolog.prologbackend.TeamMember.Domain;

import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Project.Domain.Project;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class TeamMember {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="team_member_id")
    private Long id;
    private String part;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Builder
    TeamMember(String part, Member member, Project project){
        this.part = part;
        this.member = member;
        this.project = project;
    }

    @Override
    public String toString(){
        return "TeamMember{id:"+id+"}";
    }
}
