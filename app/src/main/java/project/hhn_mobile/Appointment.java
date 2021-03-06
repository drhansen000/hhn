package project.hhn_mobile;

/**
 * Created by jay on 3/14/18.
 * This class was made so that the list view in the FutureAppointmentActivity
 * could be populated with the correct information from the user's appointments.
 * @author jay
 * @version 1.0
 */
public class Appointment {

    private String cancelled;
    private String date;
    private String info;
    private String service;
    private String time;

    Appointment() {
        cancelled = null;
        date = null;
        info = null;
        service = null;
        time = null;
    }

    Appointment(String cancelled, String service, String date, String time, String info) {
        this.cancelled = cancelled;
        this.date = date;
        this.info = info;
        this.service = service;
        this.time = time;
    }

    public String getCancelled() {
        return cancelled;
    }

    public void setCancelled(String cancelled) {
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
