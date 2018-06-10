package com.example.steff.sharedeffort;

public class PreviousToast {
    private static final PreviousToast ourInstance = new PreviousToast();

    public static PreviousToast getInstance() {
        return ourInstance;
    }

    private String message;

    private PreviousToast() {
        message = null;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        String tmp = message;
        message = null;
        return tmp;
    }
}
