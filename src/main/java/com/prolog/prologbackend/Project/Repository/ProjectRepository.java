package com.prolog.prologbackend.Project.Repository;

import com.prolog.prologbackend.Project.Domain.Project;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {
    @Transactional
    void deleteAllByProjectIdIn(List<Long> projectIds);
}