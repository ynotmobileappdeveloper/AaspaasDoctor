package com.ynot.aaspaasdoctor.Model;

public class TimeSlot {
    String slot_id,time;

    public String getSlot_id() {
        return slot_id;
    }

    public void setSlot_id(String slot_id) {
        this.slot_id = slot_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public TimeSlot(String slot_id, String time) {
        this.slot_id = slot_id;
        this.time = time;
    }
}
