package com.gachon.kimhyju.tripool.object;

public class Checklist {
    String item_name;
    String trip_id;
    int user_id;
    int item_id;
    int isChecked;

    public Checklist(String item_name, String trip_id, int user_id, int isChecked) {
        this.item_name = item_name;
        this.trip_id = trip_id;
        this.user_id = user_id;
        this.isChecked = isChecked;
    }

    public Checklist(String item_name, String trip_id, int user_id) {
        this.item_name = item_name;
        this.trip_id = trip_id;
        this.user_id = user_id;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }
}
