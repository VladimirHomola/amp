package cz.vladimir.amp;

public class AverageForInterval {
    private long startTime;
    private double average;

    public AverageForInterval(long startTime, double average) {
        this.startTime = startTime;
        this.average = average;
    }

    public long getStartTime() {
        return startTime;
    }

    public double getAverage() {
        return average;
    }

}
