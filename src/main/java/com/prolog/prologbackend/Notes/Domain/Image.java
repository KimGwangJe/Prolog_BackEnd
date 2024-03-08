package com.prolog.prologbackend.Notes.Domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="image")
@Getter
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(targetEntity = Notes.class)
    @JoinColumn(name = "notes_id")
    private Notes notes;

    @Builder
    public Image(Long imageId, String imageName, String imageUrl, Notes notes){
        this.imageId = imageId;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.notes = notes;
    }
}
