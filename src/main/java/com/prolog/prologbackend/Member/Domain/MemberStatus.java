package com.prolog.prologbackend.Member.Domain;

public enum MemberStatus {
    VERIFIED(true), UNVERIFIED(false);

    private boolean statusValue;

    MemberStatus(boolean statusValue){
        this.statusValue = statusValue;
    }

    public boolean isStatusValue(){
        return statusValue;
    }
}
