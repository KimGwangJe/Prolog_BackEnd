package com.prolog.prologbackend.Project.Repository;

import com.prolog.prologbackend.Project.Domain.Project;
import com.prolog.prologbackend.Project.Domain.ProjectStep;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectStepRepository extends JpaRepository<ProjectStep, Long> {
    List<ProjectStep> findByProjectProjectId(Long projectId);

    void deleteByProject(Project project);

    @Transactional
    @Modifying
    @Query("DELETE FROM ProjectStep ps WHERE ps.project.id IN :projectIds")
    void deleteAllByProjectIdIn(@Param("projectIds") List<Long> projectIds);
}

