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
    @Column(nullable = false)
    private String email;
    private String password;
    private Long socialId;
    @Column(nullable = false)
    private String nickname;
    private String phone;
    @Column(nullable = false)
    private String profileImage;
    @Column(nullable = false)
    private String profileName;
    @Column(nullable = false)
    private boolean isDeleted;
    @Column(nullable = false)
    private boolean isVerified;
    @Column(nullable = false)
    @Enumerated(value= EnumType.STRING)
    private MemberStatus status;
    @Column(nullable = false)
    private String roles;

    @Builder
    Member(String email, String password, Long socialId, String nickname, String phone, String profileImage,
                      String profileName, boolean isDeleted, boolean isVerified, MemberStatus status, String roles){
        this.email = email;
        this.password = password;
        this.socialId = socialId;
        this.nickname = nickname;
        this.phone = phone;
        this.profileImage = profileImage;
        this.profileName = profileName;
        this.isDeleted = isDeleted;
        this.isVerified = isVerified;
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

    public void joinToBasic(String password, String phone) {
        this.password = password;
        this.phone = phone;
        this.status = MemberStatus.BOTH;
    }
    public void joinToSocial(Long socialId) {
        this.socialId = socialId;
        this.status = MemberStatus.BOTH;
    }
}
