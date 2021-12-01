package com.ynot.aaspaasdoctor.Model;

public class SlotModel {
    private String id;
    private String time;

    public SlotModel(String id, String time) {
        this.id = id;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
