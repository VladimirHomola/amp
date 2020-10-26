package cz.vladimir.amp;

import cz.vladimir.amp.exception.DataNotFoundException;
import cz.vladimir.amp.exception.DuplicateDatapointException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.*;

@Component
public class DataService {

    private static final long INTERVAL_IN_MILLIS = java.time.Duration.ofMinutes(15).toMillis();

    private Map<String, Set<Datapoint>> dataByUser = new HashMap<>();
    private Map<String, Set<Datapoint>> dataByDevice = new HashMap<>();

    public void storeDataPoint(Datapoint dataPoint) {
        storeDatapointByUser(dataPoint);
        storeDatapointByDevice(dataPoint);
    }

    public List<AverageForInterval> getAveragesForDevice(String device) {
        Set<Datapoint> dataPointsForDevice = dataByDevice.get(device);
        if (dataPointsForDevice == null) {
            throw new DataNotFoundException();
        }
        return getAverages(dataPointsForDevice);
    }

    //TODO
    public List<AverageForInterval> getMovingAveragesForDevice(String device, long windowSize) {
        Set<Datapoint> dataPointsForDevice = dataByDevice.get(device);
        if (dataPointsForDevice == null) {
            throw new DataNotFoundException();
        }
        return getAverages(dataPointsForDevice);
    }

    public List<AverageForInterval> getAveragesForUser(String user) {
        Set<Datapoint> dataPointsForUser = dataByUser.get(user);
        if (dataPointsForUser == null) {
            throw new DataNotFoundException();
        }
        return getAverages(dataPointsForUser);
    }

    public List<AverageForInterval> getMovingAveragesForUser(String user, long windowSize) {
        Set<Datapoint> dataPointsForUser = dataByUser.get(user);
        if (dataPointsForUser == null) {
            throw new DataNotFoundException();
        }
        return getAverages(dataPointsForUser);
    }

    public void deleteUserDatapoints(String user) {
        dataByUser.remove(user);
    }

    public void deleteDeviceDatapoints(String device) {
        dataByDevice.remove(device);
    }

    private List<AverageForInterval> getAverages(Set<Datapoint> datapoints) {

        long firstTimestamp = getTimestampOfFirstDataPoint(datapoints);

        Map<Long, Double> averageValuePerInterval = calculateAverageValuesPerInterval(datapoints, firstTimestamp);

        long highestIntervalSequenceNumber = getHighestIntervalSequenceNumber(averageValuePerInterval);

        List<AverageForInterval> averageForIntervals = LongStream.rangeClosed(0, highestIntervalSequenceNumber)
                .mapToObj(sequenceNumber -> {
                    long intervalStartTime = getIntervalStartTime(firstTimestamp, sequenceNumber);
                    if (averageValuePerInterval.containsKey(sequenceNumber)) {
                        return new AverageForInterval(intervalStartTime, averageValuePerInterval.get(sequenceNumber));
                    } else {
                        return new AverageForInterval(intervalStartTime, 0);
                    }
                }).collect(toList());

        return averageForIntervals;
    }

    private long getHighestIntervalSequenceNumber(Map<Long, Double> averagePerInterval) {
        return averagePerInterval.keySet().stream().mapToLong(v -> v).max().orElseThrow(DataNotFoundException::new);
    }

    private Map<Long, Double> calculateAverageValuesPerInterval(Set<Datapoint> datapoints, long firstTimestamp) {
        Map<Long, Double> averagePerInterval = datapoints.stream()
                .collect(groupingBy(d -> getIntervalSequenceNumber(firstTimestamp, d.getTimestamp()), averagingLong(Datapoint::getValue)));


        return averagePerInterval;
    }

    private long getTimestampOfFirstDataPoint(Set<Datapoint> datapoints) {
        return datapoints.stream().mapToLong(Datapoint::getTimestamp).min().orElseThrow(DataNotFoundException::new);
    }

    private long getIntervalSequenceNumber(long firstTimestamp, long currentTimestamp) {
        return (currentTimestamp - firstTimestamp) / INTERVAL_IN_MILLIS;
    }

    private long getIntervalStartTime(long firstTimestamp, long intervalNumber) {
        return firstTimestamp + intervalNumber * INTERVAL_IN_MILLIS;
    }

    private void storeDatapointByUser(Datapoint dataPoint) {
        storeToMultimap(dataByUser, dataPoint.getUser(), dataPoint);
    }

    private void storeDatapointByDevice(Datapoint dataPoint) {
        storeToMultimap(dataByDevice, dataPoint.getDevice(), dataPoint);
    }

    private void storeToMultimap(Map<String, Set<Datapoint>> mapToStore, String key, Datapoint dataPoint) {
        if (mapToStore.containsKey(key)) {
            Set<Datapoint> existingDatapoints = mapToStore.get(key);

            boolean isDuplicate = existingDatapoints.stream().anyMatch(existing -> {
                return existing.isDuplicate(dataPoint);
            });

            if (isDuplicate) {
                throw new DuplicateDatapointException();
            }

            existingDatapoints.add(dataPoint);
        } else {
            Set<Datapoint> datapoints = new HashSet<>();
            datapoints.add(dataPoint);
            mapToStore.put(key, datapoints);
        }
    }


}
