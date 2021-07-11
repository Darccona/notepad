package org.darccona.model;

public class RecordEntity {
    private String name;
    private String date;
    private String record;

    public RecordEntity(String name, String date, String record) {
        this.name = name;
        this.date = date;
        this.record = record;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getDate() {
        return date;
    }

    public String getRecord() {
        return record;
    }
    public void setRecord(String record) {
        this.record = record;
    }
}