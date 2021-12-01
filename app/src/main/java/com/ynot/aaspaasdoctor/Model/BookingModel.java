package com.ynot.aaspaasdoctor.Model;

public class BookingModel {
    private String id;
    private String name;
    private String gender;
    private String age;
    private String date;
    private String time;
    private String location;
    private String status;

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    String symptom;

    public BookingModel(String id, String name, String gender, String age, String date, String time, String location, String status,String symptom) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.symptom=symptom;
        this.age = age;
        this.date = date;
        this.time = time;
        this.location = location;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
