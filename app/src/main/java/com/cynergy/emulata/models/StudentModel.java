package com.cynergy.emulata.models;

public class StudentModel {
    String userEmail, topic, subject;

    public StudentModel(String userEmail, String topic, String subject) {
        this.userEmail = userEmail;
        this.topic = topic;
        this.subject = subject;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
