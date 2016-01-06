package net.azurewebsites.fishprice.feeder3;

/**
 * Created by Saral on 04-01-2016.
 */
public class FeedResults {
    private String destinationName = "";
    private String scheduledDeparture = "";
    private String status = "";

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setScheduledDeparture(String scheduledDeparture) {
        this.scheduledDeparture = scheduledDeparture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getScheduledDeparture() {
        return scheduledDeparture;
    }
}
