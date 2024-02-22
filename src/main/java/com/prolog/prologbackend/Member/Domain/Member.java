package com.prolog.prologbackend.Member.Domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity(name="member")
public class Member extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name= "member_id")
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String phone;
    private String profileImage;
    private String profileName;
    private boolean isDeleted;
    @Enumerated(value= EnumType.STRING)
    private MemberStatus status;
    private String role;

    @Builder
    Member(String email, String password, String nickname, String phone, String profileImage,
                      String profileName, boolean isDeleted, MemberStatus status, String role){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
        this.profileImage = profileImage;
        this.profileName = profileName;
        this.isDeleted = isDeleted;
        this.status = status;
        this.role = role;
    }

    @Override
    public String toString(){
        return "Member{id:"+id+"}";
    }
}
