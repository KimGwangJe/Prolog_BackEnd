package com.prolog.prologbackend.Notes.Domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Author : Kim
 * Date : 2024-02-27
 * Description : 우선은 Notes/Domain에 생성 위치가 여기가 맞는지는 의문
*/

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

    @Builder
    public Image(Long imageId, String imageName, String imageUrl){
        this.imageId = imageId;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
    }
}
