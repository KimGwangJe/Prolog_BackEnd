package com.prolog.prologbackend.Project.Repository;

import com.prolog.prologbackend.Project.Domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

}