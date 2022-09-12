package com.example.realtimebustrackingsystem.Model;

public class BusData {
    private String Bname;
    private String Bnumber;
    private String Bdriver;
    private String Bstops;


    public BusData(String Name, String Number, String Driver, String Stops) {
        this.Bname = Name;
        this.Bnumber = Number;
        this.Bdriver = Driver;
        this.Bstops = Stops;
    }

    public String getBname() {
        return Bname;
    }

    public String getBnumber() {
        return Bnumber;
    }

    public String getBdriver() {
        return Bdriver;
    }

    public String getBstops() {
        return Bstops;
    }

    public BusData() {
    }
}
