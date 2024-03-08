package com.prolog.prologbackend.Member.Domain;

public enum MemberStatus {
    BASIC(true,false),
    SOCIAL(false,true),
    BOTH(true,true);

    private boolean basicMember;
    private boolean socialMember;

    MemberStatus(boolean basicMember, boolean socialMember){
        this.basicMember = basicMember;
        this.socialMember = socialMember;
    }

    public boolean isBasicMember(){
        return basicMember;
    }
    public boolean isSocialMember() {return socialMember;}
}
