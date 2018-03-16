package project.hhn_mobile;

/**
 * Created by joshua on 3/14/18.
 */

public class Appointment {

    private long cancelled;
    private String date;
    private String info;
    private String service;
    private String time;

    Appointment() {
        cancelled = 0;
        date = null;
        info = null;
        service = null;
        time = null;
    }

    Appointment(long cancelled, String service, String date, String time, String info) {
        this.cancelled = cancelled;
        this.date = date;
        this.info = info;
        this.service = service;
        this.time = time;
    }

    public long getCancelled() {
        return cancelled;
    }

    public void setCancelled(long cancelled) {
        this.cancelled = cancelled;
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


    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
