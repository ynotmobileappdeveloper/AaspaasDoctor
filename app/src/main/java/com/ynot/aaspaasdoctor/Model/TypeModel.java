package com.ynot.aaspaasdoctor.Model;

public class TypeModel {
    private String id;
    private String name;
    private String qty;
    private String days;

    public TypeModel(String id, String name, String qty, String days) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.days = days;
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
}
