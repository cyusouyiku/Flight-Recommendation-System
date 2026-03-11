package com.example.app.entity;

import java.sql.Timestamp;

public class UserFlightHistory {
    private Long id;
    private Long userId;
    private String departureAirportCode;
    private String arrivalAirportCode;
    private String behaviorType;
    private String flightNumber;
    private Timestamp createdAt;

    public UserFlightHistory() {
    }

    public UserFlightHistory(Long id, Long userId, String departureAirportCode, String arrivalAirportCode, String behaviorType, String flightNumber, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.departureAirportCode = departureAirportCode;
        this.arrivalAirportCode = arrivalAirportCode;
        this.behaviorType = behaviorType;
        this.flightNumber = flightNumber;
        this.createdAt = createdAt;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getUserId(){
        return userId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public String getDepartureAirportCode(){
        return departureAirportCode;
    }

    public void setDepartureAirportCode(String departureAirportCode){
        this.departureAirportCode = departureAirportCode;
    }

    public String getArrivalAirportCode(){
        return arrivalAirportCode;
    }

    public void setArrivalAirportCode(String arrivalAirportCode){
        this.arrivalAirportCode = arrivalAirportCode;
    }

    public String getBehaviorType(){
        return behaviorType;
    }

    public void setBehaviorType(String behaviorType){
        this.behaviorType = behaviorType;
    }

    public String getFlightNumber(){
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber){
        this.flightNumber = flightNumber;
    }

    public Timestamp getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt){
        this.createdAt = createdAt;
    }
}
