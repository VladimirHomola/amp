package cz.vladimir.amp;

import cz.vladimir.amp.exception.DataNotFoundException;
import cz.vladimir.amp.exception.DuplicateDatapointException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.*;

@Component
public class AverageCalculationsService implements AverageCalculations {

    private static final long INTERVAL_IN_MILLIS = java.time.Duration.ofMinutes(15).toMillis();

    private Map<String, List<Datapoint>> dataByUser = new HashMap<>();
    private Map<String, List<Datapoint>> dataByDevice = new HashMap<>();


    public synchronized void storeDataPoint(Datapoint dataPoint) {
        storeDatapointByUser(dataPoint);
        storeDatapointByDevice(dataPoint);
    }

    public synchronized List<AverageForInterval> getAveragesForDevice(String device) {
        List<Datapoint> dataPointsForDevice = dataByDevice.get(device);
        if (dataPointsForDevice == null) {
            throw new DataNotFoundException();
        }
        return getAverages(dataPointsForDevice);
    }

    public synchronized List<AverageForInterval> getMovingAveragesForDevice(String device, long windowSize) {
        List<AverageForInterval> averageForIntervals = getAveragesForDevice(device);
        addMovingAverage(averageForIntervals, windowSize);
        return averageForIntervals;
    }

    public synchronized List<AverageForInterval> getAveragesForUser(String user) {
        List<Datapoint> dataPointsForUser = dataByUser.get(user);
        if (dataPointsForUser == null) {
            throw new DataNotFoundException();
        }
        return getAverages(dataPointsForUser);
    }

    public synchronized List<AverageForInterval> getMovingAveragesForUser(String user, long windowSize) {
        List<AverageForInterval> averageForIntervals = getAveragesForUser(user);
        addMovingAverage(averageForIntervals, windowSize);
        return averageForIntervals;
    }

    public synchronized void deleteUserDatapoints(String user) {
        dataByUser.remove(user);
    }

    public synchronized void deleteDeviceDatapoints(String device) {
        dataByDevice.remove(device);
    }

    private List<AverageForInterval> getAverages(List<Datapoint> datapoints) {

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
                })
                .sorted(Comparator.comparingLong(AverageForInterval::getStartTime))
                .collect(toList());

        return averageForIntervals;
    }

    private void addMovingAverage(List<AverageForInterval> listOfIntervals, long windowSize) {
        Deque<AverageForInterval> window = new ArrayDeque();

        listOfIntervals.forEach(intervalAverage -> {
            window.addFirst(intervalAverage);
            if(window.size() == windowSize){
                double movingAverage = window.stream()
                        .mapToDouble(v -> v.getAverage())
                        .average()
                        .orElseThrow(DataNotFoundException::new);
                intervalAverage.setMovingAverage(movingAverage);
                window.removeLast();
            }
        });
    }

    private long getHighestIntervalSequenceNumber(Map<Long, Double> averagePerInterval) {
        return averagePerInterval.keySet().stream().mapToLong(v -> v).max().orElseThrow(DataNotFoundException::new);
    }

    private Map<Long, Double> calculateAverageValuesPerInterval(List<Datapoint> datapoints, long firstTimestamp) {
        Map<Long, Double> averagePerInterval = datapoints.stream()
                .collect(groupingBy(d -> getIntervalSequenceNumber(firstTimestamp, d.getTimestamp()), averagingLong(Datapoint::getValue)));

        return averagePerInterval;
    }

    private long getTimestampOfFirstDataPoint(List<Datapoint> datapoints) {
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

    private void storeToMultimap(Map<String, List<Datapoint>> mapToStore, String key, Datapoint dataPoint) {
        if (mapToStore.containsKey(key)) {
            List<Datapoint> existingDatapoints = mapToStore.get(key);

            boolean isDuplicate = existingDatapoints.stream().anyMatch(existing -> {
                return existing.isDuplicate(dataPoint);
            });

            if (isDuplicate) {
                throw new DuplicateDatapointException();
            }

            existingDatapoints.add(dataPoint);
        } else {
            List<Datapoint> datapoints = new ArrayList<>();
            datapoints.add(dataPoint);
            mapToStore.put(key, datapoints);
        }
    }
}
