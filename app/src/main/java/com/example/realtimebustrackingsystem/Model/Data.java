package com.example.realtimebustrackingsystem.Model;

public class Data {
    private String email;
    private String cmsid;
    private String fullname;
    private String password;

    //bus
    private String bname;
    private String bnumber;
    private String bdriver;
    private String bstops;


    public Data(String email, String cmsid, String fullname, String password) {
        this.email = email;
        this.cmsid = cmsid;
        this.fullname = fullname;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getCmsid() {
        return cmsid;
    }

    public String getFullname() {
        return fullname;
    }

    public String getPassword() {
        return password;
    }

    //bus get function


    public String getBname() {
        return bname;
    }

    public String getBnumber() {
        return bnumber;
    }

    public String getBdriver() {
        return bdriver;
    }

    public String getBstops() {
        return bstops;
    }

    public Data() {
    }
}
