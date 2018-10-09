package com.niucong.punchcardclient.table;

//@SmartTable(name = "校历")
public class Course {

//    @SmartColumn(id = 1, name = "时段", autoMerge = true)
    private String timeRank;
//    @SmartColumn(id = 2, name = "节次")
    private String sectionName;
//    @SmartColumn(id = 3, name = "一", autoMerge = true, width = 60)
    private String monday;
//    @SmartColumn(id = 4, name = "二", autoMerge = true, width = 60)
    private String tuesday;
//    @SmartColumn(id = 5, name = "三", autoMerge = true, width = 60)
    private String wednesday;
//    @SmartColumn(id = 6, name = "四", autoMerge = true, width = 60)
    private String thursday;
//    @SmartColumn(id = 7, name = "五", autoMerge = true, width = 60)
    private String friday;
//    @SmartColumn(id = 8, name = "六", autoMerge = true, width = 60)
    private String saturday;
//    @SmartColumn(id = 9, name = "日", autoMerge = true, width = 60)
    private String sunday;

    public Course(String timeRank, String sectionName, String monday, String tuesday,
                  String wednesday, String thursday, String friday, String saturday, String sunday) {

        this.timeRank = timeRank;
        this.sectionName = sectionName;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }

    public String getTimeRank() {
        return timeRank;
    }

    public void setTimeRank(String timeRank) {
        this.timeRank = timeRank;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public String getSaturday() {
        return saturday;
    }

    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    public String getSunday() {
        return sunday;
    }

    public void setSunday(String sunday) {
        this.sunday = sunday;
    }
}
