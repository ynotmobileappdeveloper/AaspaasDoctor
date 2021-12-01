package com.ynot.aaspaasdoctor.Model;

public class Medipriscription {


    private String id;
    private String date;
    private String name;
    private String gender;
    private String age;
    private String medicine;
    private String status;
    private String medicine_name;
    private String qty;
    private String days;
    private String medi_id;

    public Medipriscription(String medicine_name, String qty, String days, String medi_id) {
        this.medicine_name = medicine_name;
        this.qty = qty;
        this.days = days;
        this.medi_id = medi_id;
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

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMedicine_name() {
        return medicine_name;
    }

    public void setMedicine_name(String medicine_name) {
        this.medicine_name = medicine_name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getMedi_id() {
        return medi_id;
    }

    public void setMedi_id(String medi_id) {
        this.medi_id = medi_id;
    }
}
