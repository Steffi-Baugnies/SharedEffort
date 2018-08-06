package com.example.steff.sharedeffort;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FamilyMember {

    private int id;
    private String fname;
    private String lname;
    private LocalDate birthdate;
    private int points;
    private String pswd;
    private Boolean isAdmin;

    public FamilyMember(int id, String fname, String lname, String birthdate, int points, String pswd, int isAdmin){

        this.id = id;
        this.fname = fname;
        this.lname = lname;

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");
        this.birthdate = LocalDate.parse(birthdate, dateTimeFormatter);

        this.points = points;
        this.pswd = pswd;
        this.isAdmin = isAdmin == 0?false:true; 
    }

    public int getId() {
        return id;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

}
