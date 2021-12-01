package com.ynot.aaspaasdoctor.Model;

public class HistoryModel {
    private String id;
    private String name;
    private String gender;
    private String age;
    private String date;
    private String time;
    private String location;
    private String symptoms;

    public HistoryModel(String id, String name, String gender, String age, String date, String time, String location, String symptoms) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.date = date;
        this.time = time;
        this.location = location;
        this.symptoms = symptoms;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
}
