package com.vogella.android.navigationwidgetattempt;

/**
 * Created by nezuma on 10/22/16.
 */

public class request {
    public String request_id;
    public String pickup;
    public String dest;
    public String time;
    public String price;
    public String status;
    public String notes;
    public String passenger_name;
    public String passenger_phone;

    public request(){
        this.dest = null;
        this.passenger_name = null;
        this.pickup = null;
        this.time = null;
        this.price = null;
        this.request_id = null;
        this.status = null;
        this.notes = null;
        this.passenger_phone = null;
    }
    public request(String request_id, String pickup, String dest, String passenger_name,
                   String passenger_phone, String time, String price, String notes, String status){
        this.dest = dest;
        this.passenger_name = passenger_name;
        this.pickup = pickup;
        this.time = time;
        this.price = price;
        this.request_id = request_id;
        this.status = status;
        this.notes = notes;
        this.passenger_phone = passenger_phone;
    }

/*    public void setDest(String dest) {
        this.dest = dest;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setPassenger_name(String passenger_name) {
        this.passenger_name = passenger_name;
    }

    public void setPassenger_phone(String passenger_phone) {
        this.passenger_phone = passenger_phone;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTime(String time) {
        this.time = time;
    }*/

    public void nextStatus(){
        switch (this.status) {
            case "on the way":
                this.status = "arrived at pickup point";
                break;
            case "arrived at pickup point":
                this.status = "passenger picked up";
                break;
            case "passenger picked up":
                this.status = "arrived at destination";
                break;
            case "arrived at destination":
                this.status = "completed";
                break;
        }
/*
        else if (request.status.equals("on the way"))
            request.status = "pickup";
        else if (request.status.equals("on the way"))
            request.status = "pickup";
        else
*/
    }
}
