package project.hhn_mobile;

/**
 * Created by jay on 3/12/18.
 * This class was made so that the dropdown spinner in the CreateAppointmentActivity
 * could be populated with the correct information from the services branch of the database.
 * @author jay
 * @version 1.0
 */
public class Service {

    private long cost;
    private long duration;
    private long id;
    private String service;

    Service() {
        cost = 0;
        duration = 0;
        id = 0;
        service = null;
    }

    Service (long cost, long duration, long id, String service) {
        this.cost = cost;
        this.duration = duration;
        this.id = id;
        this.service = service;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }


    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }


    public long getID() {
        return id;
    }

    public void setID(long id) {
        this.id = id;
    }


    public String getService() {
        return service;
    }

    public void setService(String serviceName) {
        this.service = serviceName;
    }

    /**
     * The purpose of this function is to take each variable of a service object
     * and concatenate it into a single string.
     * @return full, a string that contains the full service.
     */
    public String getFullService() {
        String s = service;
        String d = Long.toString(duration);
        String c = Long.toString(cost);
        String full = s + " for " + d + "min at $" + c;
        return full;
    }
}
