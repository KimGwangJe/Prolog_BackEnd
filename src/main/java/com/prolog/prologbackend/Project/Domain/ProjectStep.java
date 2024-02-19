package com.prolog.prologbackend.Project.Domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "project_step")
@Getter
@NoArgsConstructor
public class ProjectStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long stepId;

    @Column(name = "step_name")
    private String stepName;

    @Column(name = "started_date")
    private Date startedDate;

    @Column(name = "ended_date")
    private Date endedDate;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder // 수정에 사용
    public ProjectStep(String stepName, Date startedDate, Date endedDate, Project project) {
        this.stepName = stepName;
        this.startedDate = startedDate;
        this.endedDate = endedDate;
        this.project = project;
    }
}