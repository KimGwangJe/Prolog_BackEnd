package com.prolog.prologbackend.Member.Domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private String roles;

    @Builder
    Member(String email, String password, String nickname, String phone, String profileImage,
                      String profileName, boolean isDeleted, MemberStatus status, String roles){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
        this.profileImage = profileImage;
        this.profileName = profileName;
        this.isDeleted = isDeleted;
        this.status = status;
        this.roles = roles;
    }

    @Override
    public String toString(){
        return "Member{id:"+id+"}";
    }

    public List<String> getRoleList(){
        if(this.roles.length() > 0){
            return Arrays.asList(this.roles.split(","));
        }
        return new ArrayList<>();
    }

    public void updateEmail(String email){
        if(!this.email.equals(email))
            this.email = email;
    }

    public void updatePassword(String password){
        this.password = password;
    }

    public void updateNickname(String nickname){
        if(!this.nickname.equals(nickname))
            this.nickname = nickname;
    }
}
