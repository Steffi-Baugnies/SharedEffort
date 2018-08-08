package com.example.steff.sharedeffort;

import java.util.List;

public class ConnectedUserInfo {
    private static final ConnectedUserInfo ourInstance = new ConnectedUserInfo();
    private int connectedUser;
    private int familyId;
    private int connectedMember;
    private List<FamilyMember> familyMembers;

    public static ConnectedUserInfo getInstance() {
        return ourInstance;
    }

    private ConnectedUserInfo() {
    }

    public int getConnectedUser(){
        return connectedUser;
    }
    public void setConnectedUser(int userId){
        this.connectedUser = userId;
    }

    public int getFamilyId(){ return familyId; }
    public void setFamilyId(int famId){ this.familyId = famId; }

    public int getConnectedMember(){
        return connectedMember;
    }
    public void setConnectedMember(int id){
        this.connectedMember = id;
    }

    public List<FamilyMember> getFamilyMembers(){ return familyMembers; }
    public void setFamilyMembers(List<FamilyMember> familyMembers){
        this.familyMembers = familyMembers;
    }
}
