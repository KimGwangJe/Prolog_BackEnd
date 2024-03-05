package com.prolog.prologbackend.Project.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "project_stack")
@Getter
@NoArgsConstructor
public class ProjectStack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stack_id")
    private Long stackId;

    @Column(name = "stack_name")
    private String stackName;

    @Column(name = "image")
    private String image;
}