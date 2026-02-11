package com.grandvista.backend.presentation.dto;

public class ReservationDetailsResponse {
    private String id;
    private String clientName;
    private String clientContact;
    private String roomType;
    private String stayDuration; // e.g. "2023-10-25 to 2023-10-28"
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientContact() {
        return clientContact;
    }

    public void setClientContact(String clientContact) {
        this.clientContact = clientContact;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getStayDuration() {
        return stayDuration;
    }

    public void setStayDuration(String stayDuration) {
        this.stayDuration = stayDuration;
    }

    private int age;
    private String address;
    private String identificationId;
    private int people;
    private java.time.LocalDate checkInDate;
    private java.time.LocalDate checkOutDate;
    private String bedType;
    private boolean breakfastIncluded;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public java.time.LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(java.time.LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public java.time.LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(java.time.LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public boolean isBreakfastIncluded() {
        return breakfastIncluded;
    }

    public void setBreakfastIncluded(boolean breakfastIncluded) {
        this.breakfastIncluded = breakfastIncluded;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
