package com.example.app.entity;

import java.sql.Timestamp;

public class FlightPrice {
    private Long id;
    private String flightNumber;
    private String departureAirportCode;
    private String arrivalAirportCode;
    private String cabinClass;
    private int price;
    private Timestamp recordAt;

    public FlightPrice() {
    }

    public FlightPrice(String flightNumber, String departureAirportCode, String arrivalAirportCode, String cabinClass, int price, Timestamp recordAt) {
        this.flightNumber = flightNumber;
        this.departureAirportCode = departureAirportCode;
        this.arrivalAirportCode = arrivalAirportCode;
        this.cabinClass = cabinClass;
        this.price = price;
        this.recordAt = recordAt;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getFlightNumber(){
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber){
        this.flightNumber = flightNumber;
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

    public String getCabinClass(){
        return cabinClass;
    }

    public void setCabinClass(String cabinClass){
        this.cabinClass = cabinClass;
    }

    public int getPrice(){
        return price;
    }

    public void setPrice(int price){
        this.price = price;
    }

    public Timestamp getRecordAt(){
        return recordAt;
    }

    public void setRecordAt(Timestamp recordAt){
        this.recordAt = recordAt;
    }
}
