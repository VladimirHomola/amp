package cz.vladimir.amp;

import cz.vladimir.amp.AverageForInterval;
import cz.vladimir.amp.Datapoint;

import java.util.List;

public interface AverageCalculations {

    void storeDataPoint(Datapoint dataPoint);
    List<AverageForInterval> getAveragesForDevice(String device);
    List<AverageForInterval> getMovingAveragesForDevice(String device, long windowSize);
    List<AverageForInterval> getAveragesForUser(String user);
    List<AverageForInterval> getMovingAveragesForUser(String user, long windowSize);
    void deleteUserDatapoints(String user);
    void deleteDeviceDatapoints(String device);
}
