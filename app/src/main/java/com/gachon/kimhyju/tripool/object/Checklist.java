package com.gachon.kimhyju.tripool.object;

public class Checklist {
    String ChecklistName;
    String trip_Id;
    int user_Id;
    int ChecklistId;
    int isChecked;

    public Checklist(String ChecklistName, String trip_Id, int user_Id, int isChecked) {
        this.ChecklistName = ChecklistName;
        this.trip_Id = trip_Id;
        this.user_Id = user_Id;
        this.isChecked = isChecked;
    }

    public Checklist(String ChecklistName, String trip_Id, int user_Id) {
        this.ChecklistName = ChecklistName;
        this.trip_Id = trip_Id;
        this.user_Id = user_Id;
    }

    public String getTrip_Id() {
        return trip_Id;
    }

    public void setTrip_Id(String trip_Id) {
        this.trip_Id = trip_Id;
    }

    public int getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(int user_Id) {
        this.user_Id = user_Id;
    }
    public String getChecklistName() {
        return ChecklistName;
    }

    public void setChecklistName(String checklistName) {
        ChecklistName = checklistName;
    }

    public int getChecklistId() {
        return ChecklistId;
    }

    public void setChecklistId(int checklistId) {
        ChecklistId = checklistId;
    }

    public int getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }
}
