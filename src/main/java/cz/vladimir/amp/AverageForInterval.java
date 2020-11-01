package cz.vladimir.amp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class AverageForInterval {
    private long startTime;
    private double average;
    @JsonInclude(Include.NON_NULL)
    private Double movingAverage;

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

    public Double getMovingAverage() {
        return movingAverage;
    }

    public void setMovingAverage(double movingAverage) {
        this.movingAverage = movingAverage;
    }
}
