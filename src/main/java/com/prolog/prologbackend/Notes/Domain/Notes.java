package com.prolog.prologbackend.Notes.Domain;

import jakarta.persistence.*;

import java.util.Date;

/**
 * Author : Kim
 * Date : 2024-02-18
 * Description : teamMember Entity 생성시 주석 제거 필요
 */

@Entity
@Table(name = "daily_log")
public class Notes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notes_id")
    private Long notesId;

    @Column(name = "date")
    private Date date;

    @Column(name = "title")
    private String title;

    @Column(name = "summary")
    private String summary;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "modified_date")
    private Date modifiedDate;

    @Column(name = "type")
    private String type;

    @Column(name = "content")
    private String content;

//    @ManyToOne
//    @JoinColumn(name = "team_member_id")
//    private TeamMember teamMember;

}