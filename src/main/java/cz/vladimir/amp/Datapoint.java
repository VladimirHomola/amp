package cz.vladimir.amp;

public class Datapoint {
    private String device;
    private String user;
    private long timestamp;
    private long value;

    public Datapoint(String device, String user, long timestamp, long value) {
        this.device = device;
        this.user = user;
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getDevice() {
        return device;
    }

    public String getUser() {
        return user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getValue() {
        return value;
    }

    public boolean isDuplicate(Datapoint toCheck){
        return this.getTimestamp() == toCheck.getTimestamp() &&
                this.getUser().equals(toCheck.getUser()) &&
                this.getDevice().equals(toCheck.getDevice());
    }
}
