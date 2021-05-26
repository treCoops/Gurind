package com.dtinnovation.gurind.models;

public class MessageInfo {
    private String subject,body,date,id,read_status;

    public MessageInfo(){}

    public MessageInfo(String id, String subject, String body, String date, String read_status) {
        this.id = id;
        this.subject = subject;
        this.body = body;
        this.date = date;
        this.read_status = read_status;
    }

    public String getBody() {
        return body;
    }
    public String getSubject() {
        return subject;
    }
    public String getDate() {
        return date;
    }
    public String getID() {
        return id;
    }
    public String getRead_Status() {
        return read_status;
    }
}
