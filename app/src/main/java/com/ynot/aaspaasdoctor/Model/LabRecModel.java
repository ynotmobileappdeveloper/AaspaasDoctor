package com.ynot.aaspaasdoctor.Model;

public class LabRecModel {
    private String id;
    private String date;
    private String name;
    private String gender;
    private String age;
    private String test;

    public LabRecModel(String id, String date, String name, String gender, String age, String test) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.test = test;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
