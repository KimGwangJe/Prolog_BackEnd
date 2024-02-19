package com.prolog.prologbackend.Project.Repository;

import com.prolog.prologbackend.Project.Domain.ProjectStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectStackRepository extends JpaRepository<ProjectStack, Long> {
}