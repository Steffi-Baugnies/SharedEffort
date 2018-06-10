package com.example.steff.sharedeffort;

public class ConnectedUser {
    private static final ConnectedUser ourInstance = new ConnectedUser();
    private int connectedUser;

    public static ConnectedUser getInstance() {
        return ourInstance;
    }

    private ConnectedUser() {
    }

    public int getConnectedUser(){
        return connectedUser;
    }

    public void setConnectedUser(int userId){
        this.connectedUser = userId;
    }
}
