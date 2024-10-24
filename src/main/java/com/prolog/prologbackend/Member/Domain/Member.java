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
    private String profileName;
    @Column(nullable = false)
    private boolean isBasicImage;
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
           String profileName, boolean isBasicImage, boolean isDeleted, boolean isVerified, MemberStatus status, String roles){
        this.email = email;
        this.password = password;
        this.socialId = socialId;
        this.nickname = nickname;
        this.phone = phone;
        this.profileImage = profileImage;
        this.profileName = profileName;
        this.isBasicImage = isBasicImage;
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

    public void updatePassword(String password){
        this.password = password;
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    public void updatePhoneNumber(String phone){
        if(!this.phone.equals(phone))
            this.phone = phone;
    }

    public void setVerified(){
        this.isVerified = true;
    }

    public void joinToBasic(String password, String phone) {
        this.password = password;
        this.phone = phone;
        this.status = MemberStatus.BOTH;
    }

    public void joinToSocial() {
        this.status = MemberStatus.BOTH;
    }

    public void resetJoinToBasic() {
        this.password = null;
        this.phone = null;
        this.status = MemberStatus.SOCIAL;
    }

    public void updateProfile(String profileImage, String profileName) {
        this.profileImage = profileImage;
        if(profileName != null){
            this.isBasicImage = false;
            this.profileName = profileName;
        }else{
            this.isBasicImage = true;
            this.profileName = null;
        }
    }
}
