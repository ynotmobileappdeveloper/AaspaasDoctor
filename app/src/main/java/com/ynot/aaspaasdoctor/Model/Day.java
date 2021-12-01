package com.ynot.aaspaasdoctor.Model;

public class Day {
    public String day;
    public String startime;
    public String endtime;

    public Day(String day, String startime, String endtime) {
        this.day = day;
        this.startime = startime;
        this.endtime = endtime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartime() {
        return startime;
    }

    public void setStartime(String startime) {
        this.startime = startime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }
}
