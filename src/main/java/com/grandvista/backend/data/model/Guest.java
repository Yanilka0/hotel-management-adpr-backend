package com.grandvista.backend.data.model;

public class Guest {
    private String id;
    private String fullName;
    private int age;
    private String contactNumber;
    private String address;
    private String identificationId;

    public Guest() {
    }

    public Guest(String fullName, int age, String contactNumber, String address, String identificationId) {
        this.fullName = fullName;
        this.age = age;
        this.contactNumber = contactNumber;
        this.address = address;
        this.identificationId = identificationId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdentificationId() {
        return identificationId;
    }

    public void setIdentificationId(String identificationId) {
        this.identificationId = identificationId;
    }
}
