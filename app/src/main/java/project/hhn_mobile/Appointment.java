package project.hhn_mobile;

/**
 * Created by joshua on 3/14/18.
 */

public class Appointment {

    private String date;
    private String info;
    private long service;
    private String time;

    Appointment() {
        date = null;
        info = null;
        service = 0;
        time = null;
    }

    Appointment(long service, String date, String time, String info) {
        this.date = date;
        this.info = info;
        this.service = service;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


    public long getService() {
        return service;
    }

    public void setService(long service) {
        this.service = service;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
