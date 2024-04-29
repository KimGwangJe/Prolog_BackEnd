package com.prolog.prologbackend.Project.Repository;

import com.prolog.prologbackend.Project.Domain.Project;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Project p WHERE p.id IN :projectIds")
    void deleteAllByProjectIdIn(@Param("projectIds") List<Long> projectIds);


    @Transactional
    @Modifying
    @Query("SELECT p FROM Project p WHERE p.isDeleted = true AND p.modifiedDate < :oneWeekAgoTimestamp")
    List<Project> findIdsByIsDeletedTrueAndModifiedDateBefore(@Param("oneWeekAgoTimestamp") Timestamp oneWeekAgoTimestamp);

}