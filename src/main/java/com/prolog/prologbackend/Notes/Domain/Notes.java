package com.prolog.prologbackend.Notes.Domain;

import com.prolog.prologbackend.TeamMember.Domain.TeamMember;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "daily_log")
@Getter
@NoArgsConstructor
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

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "team_member_id")
    private TeamMember teamMember;

    @Builder
    public Notes(
            Long notesId, Date date, String title,
            String summary, Date createdDate, Date modifiedDate,
            String type, String content, TeamMember teamMember
    ){
        this.notesId = notesId;
        this.date = date;
        this.title = title;
        this.summary = summary;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.type = type;
        this.content = content;
        this.teamMember = teamMember;
    }
}