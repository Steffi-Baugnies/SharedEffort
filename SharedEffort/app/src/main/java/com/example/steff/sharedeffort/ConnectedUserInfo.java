package com.example.steff.sharedeffort;

public class ConnectedUserInfo {
    private static final ConnectedUserInfo ourInstance = new ConnectedUserInfo();
    private int connectedUser;
    private int familyId;

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
}
