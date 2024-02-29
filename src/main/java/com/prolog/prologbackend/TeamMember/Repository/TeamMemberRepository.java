package com.prolog.prologbackend.TeamMember.Repository;

import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Project.Domain.Project;
import com.prolog.prologbackend.TeamMember.Domain.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    Optional<List<TeamMember>> findAllByProject(Project project);
    List<TeamMember> findAllByMember(Member member);
    Optional<TeamMember> findByMemberAndProject(Member member, Project project);
}
