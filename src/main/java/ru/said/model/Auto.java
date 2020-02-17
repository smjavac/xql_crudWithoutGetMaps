package ru.said.model;

public class Auto {
    private String id;
    private String model;
    private String body;

    public Auto() {
    }

    public String getR_object_id() {
        return id;
    }

    public void setR_object_id(String r_object_id) {
        this.id = r_object_id;
    }

    public void setDss_model(String dss_model) {
        this.model = dss_model;
    }

    public String getDss_model() {
        return model;
    }



    public String getDss_body() {
        return body;
    }

    public void setDss_body(String dss_body) {
        this.body = dss_body;
    }
}
