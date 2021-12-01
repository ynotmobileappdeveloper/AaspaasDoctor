package com.ynot.aaspaasdoctor.Webservice;

public class User {

    private String id;
    private String username;
    private String phone;
    private String email;
    private String qualfication;

    public User(String id, String username, String phone, String email, String qualfication) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.qualfication = qualfication;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQualfication() {
        return qualfication;
    }

    public void setQualfication(String qualfication) {
        this.qualfication = qualfication;
    }
}
