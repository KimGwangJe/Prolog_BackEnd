package com.prolog.prologbackend.Project.Domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "project")
@Getter
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "ended_date")
    private Date endedDate;

    @Column(name = "description")
    private String description;

    @Column(name = "stack")
    private String stack;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "modified_date")
    private Date modifiedDate;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public boolean getIsDeleted(){
        return isDeleted;
    }

    @Builder
    public Project(
            Long projectId, String projectName, Date startDate, Date endedDate,
            String description, String stack, Date createdDate,
            Date modifiedDate, boolean isDeleted)
    {
        this.projectId = projectId;
        this.projectName = projectName;
        this.startDate = startDate;
        this.endedDate = endedDate;
        this.description = description;
        this.stack = stack;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.isDeleted = isDeleted;
    }
}
