package com.prolog.prologbackend.Project.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Author : Kim
 * Date : 2024-02-18
 * Description : project에 사용되는 stack 들이 저장되어있습니다.
 */

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