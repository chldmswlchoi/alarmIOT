package com.example.alamr_iot;

public class DTO_alarm {

    private String time;
    private String alarmTitle,hourOfDay;
    private Boolean IsOn;
    private String minute;


    public DTO_alarm(String time, String alarmTitle, Boolean isOn, String hourOfDay, String minute) {

        this.time =time;
        this.alarmTitle = alarmTitle;
        this.IsOn = isOn;
        this.hourOfDay = hourOfDay;
        this.minute= minute;
    }

    public String getAlarmTitle() {
        return alarmTitle;
    }

    public void setAlarmTitle(String alarmTitle) {
        this.alarmTitle = alarmTitle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getOn() {
        return IsOn;
    }

    public void setOn(Boolean on) {
        IsOn = on;
    }

    public String getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(String hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }
}
