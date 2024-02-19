package com.prolog.prologbackend.Project.Repository;

import com.prolog.prologbackend.Project.Domain.Project;
import com.prolog.prologbackend.Project.Domain.ProjectStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectStepRepository extends JpaRepository<ProjectStep, Long> {
    List<ProjectStep> findByProjectProjectId(Long projectId);

    void deleteByProject(Project project);
}

