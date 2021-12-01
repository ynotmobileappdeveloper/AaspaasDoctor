package com.ynot.aaspaasdoctor.Model;

public class ConsultationModel {
    public String getConsult_id() {
        return consult_id;
    }

    public void setConsult_id(String consult_id) {
        this.consult_id = consult_id;
    }

    public ConsultationModel(String consult_id, String latitiude, String longititude, String type, String name, String district, String city, String pincode) {
        this.consult_id = consult_id;
        this.latitiude = latitiude;
        this.longititude = longititude;
        this.type = type;
        this.name = name;
        this.district = district;
        this.city = city;
        this.pincode = pincode;
    }

    String consult_id,latitiude,longititude,type,name,district,city,pincode;




    public String getLatitiude() {
        return latitiude;
    }

    public void setLatitiude(String latitiude) {
        this.latitiude = latitiude;
    }

    public String getLongititude() {
        return longititude;
    }

    public void setLongititude(String longititude) {
        this.longititude = longititude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
