package com.ynot.aaspaasdoctor.Model;

public class Labtest {
    private String test_id;
    private String date;
    private String content;

    public Labtest(String test_id, String date, String content) {
        this.test_id = test_id;
        this.date = date;
        this.content = content;
    }

    public String getTest_id() {
        return test_id;
    }

    public void setTest_id(String test_id) {
        this.test_id = test_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
