package cz.vladimir.amp;

import cz.vladimir.amp.exception.DataNotFoundException;
import cz.vladimir.amp.exception.DuplicateDatapointException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AverageCalculationsServiceTest {

    public static final String TEST_DEVICE = "testDevice";
    public static final String TEST_USER = "testUser";
    public static final int FIRST_INTERVAL_FIRST_TIMESTAMP = 10000000;
    public static final int FIRST_INTERVAL_SECOND_TIMESTAMP = 10000001;
    public static final int SECOND_INTERVAL_FIRST_TIMESTAMP = 10900000;
    public static final int SECOND_INTERVAL_SECOND_TIMESTAMP = 10900001;
    public static final int THIRD_INTERVAL_FIRST_TIMESTAMP = 11800000;
    public static final int THIRD_INTERVAL_SECOND_TIMESTAMP = 11800001;

    AverageCalculationsService averageCalculationsService;

    @BeforeEach
    void init() {
        averageCalculationsService = new AverageCalculationsService();
    }

    @Test
    public void shouldThrowExceptionIfStoringDuplicateDataPoint(){
        Datapoint dataPoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        Datapoint duplicateDatapoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 200);

        averageCalculationsService.storeDataPoint(dataPoint);

        assertThrows(DuplicateDatapointException.class, () -> {
            averageCalculationsService.storeDataPoint(duplicateDatapoint);
        });
    }

    @Test
    public void shouldThrowExceptionIfAccessingDataForNotExistingUser(){
        assertThrows(DataNotFoundException.class, () -> {
            averageCalculationsService.getAveragesForUser("notExistingUser");
        });
    }

    @Test
    public void shouldThrowExceptionIfAccessingDataForNotExistingDevice(){
        assertThrows(DataNotFoundException.class, () -> {
            averageCalculationsService.getAveragesForDevice("notExistingDevice");
        });
    }

    @Test
    public void shouldReturnOneValueByUserAfterStoringOneDatapoint(){
        AverageCalculationsService averageCalculationsService = new AverageCalculationsService();

        Datapoint datapoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        averageCalculationsService.storeDataPoint(datapoint);

        List<AverageForInterval> averageValues = averageCalculationsService.getAveragesForUser(TEST_USER);

        assertEquals(1, averageValues.size(), "Wrong number of average values");
        assertEquals(100, averageValues.get(0).getAverage(), "Wrong average value");
    }

    @Test
    public void shouldReturnOneValueByDeviceAfterStoringOneDatapoint(){
        Datapoint datapoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        averageCalculationsService.storeDataPoint(datapoint);

        List<AverageForInterval> averageValues = averageCalculationsService.getAveragesForDevice(TEST_DEVICE);

        assertEquals(1, averageValues.size(), "Wrong number of average values");
        assertEquals(100, averageValues.get(0).getAverage(), "Wrong average value");
    }

    @Test
    public void shouldReturnAverageValueByUserAfterStoringTwoDatapointsWithinFifteenMinutesInterval(){
        Datapoint firstDataPoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        averageCalculationsService.storeDataPoint(firstDataPoint);

        Datapoint secondDataPoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_SECOND_TIMESTAMP, 300);
        averageCalculationsService.storeDataPoint(secondDataPoint);

        List<AverageForInterval> averageValues = averageCalculationsService.getAveragesForUser(TEST_USER);

        assertEquals(1, averageValues.size(), "Wrong number of average values");
        assertEquals(200, averageValues.get(0).getAverage(), "Wrong average value");
    }

    @Test
    public void shouldReturnAverageValueByDeviceAfterStoringTwoDatapointsWithinFifteenMinutesInterval(){
        Datapoint firstDataPoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        averageCalculationsService.storeDataPoint(firstDataPoint);

        Datapoint secondDataPoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_SECOND_TIMESTAMP, 300);
        averageCalculationsService.storeDataPoint(secondDataPoint);

        List<AverageForInterval> averageValues = averageCalculationsService.getAveragesForDevice(TEST_DEVICE);

        assertEquals(1, averageValues.size(), "Wrong number of average values");
        assertEquals(200, averageValues.get(0).getAverage(), "Wrong average value");
    }

    @Test
    public void shouldReturnAverageValueForEachIntervalByUserAfterStoringDatapointsSpanningOverFifteenMinutesInterval(){
        Datapoint firstIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        averageCalculationsService.storeDataPoint(firstIntervalDataPoint1);

        Datapoint firstIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_SECOND_TIMESTAMP, 300);
        averageCalculationsService.storeDataPoint(firstIntervalDataPoint2);

        Datapoint secondIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, SECOND_INTERVAL_FIRST_TIMESTAMP, 400);
        averageCalculationsService.storeDataPoint(secondIntervalDataPoint1);

        Datapoint secondIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, SECOND_INTERVAL_SECOND_TIMESTAMP, 500);
        averageCalculationsService.storeDataPoint(secondIntervalDataPoint2);

        List<AverageForInterval> averageValues = averageCalculationsService.getAveragesForUser(TEST_USER);

        assertEquals(2, averageValues.size(), "Wrong number of average values");
        assertEquals(200, averageValues.get(0).getAverage(), "Wrong average value");
        assertEquals(450, averageValues.get(1).getAverage(), "Wrong average value");
    }

    @Test
    public void shouldReturnAverageValueForEachIntervalByDeviceAfterStoringDatapointsSpanningOverFifteenMinutesInterval(){
        Datapoint firstIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        averageCalculationsService.storeDataPoint(firstIntervalDataPoint1);

        Datapoint firstIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_SECOND_TIMESTAMP, 300);
        averageCalculationsService.storeDataPoint(firstIntervalDataPoint2);

        Datapoint secondIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, SECOND_INTERVAL_FIRST_TIMESTAMP, 400);
        averageCalculationsService.storeDataPoint(secondIntervalDataPoint1);

        Datapoint secondIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, SECOND_INTERVAL_SECOND_TIMESTAMP, 500);
        averageCalculationsService.storeDataPoint(secondIntervalDataPoint2);

        List<AverageForInterval> averageValues = averageCalculationsService.getAveragesForDevice(TEST_DEVICE);

        assertEquals(2, averageValues.size(), "Wrong number of average values");
        assertEquals(200, averageValues.get(0).getAverage(), "Wrong average value");
        assertEquals(450, averageValues.get(1).getAverage(), "Wrong average value");
    }

    @Test
    public void shouldReturnAverageValueForEachIntervalByUserAfterStoringDatapointsSpanningOverFifteenMinutesIntervalWithEmptyIntervals(){
        Datapoint firstIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        averageCalculationsService.storeDataPoint(firstIntervalDataPoint1);

        Datapoint firstIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_SECOND_TIMESTAMP, 300);
        averageCalculationsService.storeDataPoint(firstIntervalDataPoint2);

        //no data for second interval

        Datapoint thirdIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, THIRD_INTERVAL_FIRST_TIMESTAMP, 400);
        averageCalculationsService.storeDataPoint(thirdIntervalDataPoint1);

        Datapoint thirdIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, THIRD_INTERVAL_SECOND_TIMESTAMP, 500);
        averageCalculationsService.storeDataPoint(thirdIntervalDataPoint2);

        List<AverageForInterval> averageValues = averageCalculationsService.getAveragesForUser(TEST_USER);

        assertEquals(3, averageValues.size(), "Wrong number of average values");
        assertEquals(200, averageValues.get(0).getAverage(), "Wrong average value");
        assertEquals(0, averageValues.get(1).getAverage(), "Wrong average value");
        assertEquals(450, averageValues.get(2).getAverage(), "Wrong average value");
    }

    @Test
    public void shouldReturnAverageValueForEachIntervalByDeviceAfterStoringDatapointsSpanningOverFifteenMinutesIntervalWithEmptyIntervals(){
        Datapoint firstIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        averageCalculationsService.storeDataPoint(firstIntervalDataPoint1);

        Datapoint firstIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_SECOND_TIMESTAMP, 300);
        averageCalculationsService.storeDataPoint(firstIntervalDataPoint2);

        //no data for second interval

        Datapoint thirdIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, THIRD_INTERVAL_FIRST_TIMESTAMP, 400);
        averageCalculationsService.storeDataPoint(thirdIntervalDataPoint1);

        Datapoint thirdIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, THIRD_INTERVAL_SECOND_TIMESTAMP, 500);
        averageCalculationsService.storeDataPoint(thirdIntervalDataPoint2);

        List<AverageForInterval> averageValues = averageCalculationsService.getAveragesForDevice(TEST_DEVICE);

        assertEquals(3, averageValues.size(), "Wrong number of average values");
        assertEquals(200, averageValues.get(0).getAverage(), "Wrong average value");
        assertEquals(0, averageValues.get(1).getAverage(), "Wrong average value");
        assertEquals(450, averageValues.get(2).getAverage(), "Wrong average value");
    }

    @Test
    public void shouldCalculateMovingAverageForDevice(){
        Datapoint firstIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        averageCalculationsService.storeDataPoint(firstIntervalDataPoint1);

        Datapoint firstIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_SECOND_TIMESTAMP, 300);
        averageCalculationsService.storeDataPoint(firstIntervalDataPoint2);

        Datapoint secondIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, SECOND_INTERVAL_FIRST_TIMESTAMP, 400);
        averageCalculationsService.storeDataPoint(secondIntervalDataPoint1);

        Datapoint secondIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, SECOND_INTERVAL_SECOND_TIMESTAMP, 500);
        averageCalculationsService.storeDataPoint(secondIntervalDataPoint2);

        Datapoint thirdIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, THIRD_INTERVAL_FIRST_TIMESTAMP, 600);
        averageCalculationsService.storeDataPoint(thirdIntervalDataPoint1);

        Datapoint thirdIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, THIRD_INTERVAL_SECOND_TIMESTAMP, 700);
        averageCalculationsService.storeDataPoint(thirdIntervalDataPoint2);

        List<AverageForInterval> averageValues = averageCalculationsService.getMovingAveragesForDevice(TEST_DEVICE, 2);

        assertEquals(3, averageValues.size(), "Wrong number of average values");

        assertEquals(200, averageValues.get(0).getAverage(), "Wrong average value");
        assertNull(averageValues.get(0).getMovingAverage(), "Moving average should not be calculated");

        assertEquals(450, averageValues.get(1).getAverage(), "Wrong average value");
        assertEquals(325, averageValues.get(1).getMovingAverage(), "Wrong moving average value");

        assertEquals(650, averageValues.get(2).getAverage(), "Wrong average value");
        assertEquals(550, averageValues.get(2).getMovingAverage(), "Wrong moving average value");
    }

    @Test
    public void shouldCalculateMovingAverageForUser(){
        Datapoint firstIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        averageCalculationsService.storeDataPoint(firstIntervalDataPoint1);

        Datapoint firstIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_SECOND_TIMESTAMP, 300);
        averageCalculationsService.storeDataPoint(firstIntervalDataPoint2);

        Datapoint secondIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, SECOND_INTERVAL_FIRST_TIMESTAMP, 400);
        averageCalculationsService.storeDataPoint(secondIntervalDataPoint1);

        Datapoint secondIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, SECOND_INTERVAL_SECOND_TIMESTAMP, 500);
        averageCalculationsService.storeDataPoint(secondIntervalDataPoint2);

        Datapoint thirdIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, THIRD_INTERVAL_FIRST_TIMESTAMP, 600);
        averageCalculationsService.storeDataPoint(thirdIntervalDataPoint1);

        Datapoint thirdIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, THIRD_INTERVAL_SECOND_TIMESTAMP, 700);
        averageCalculationsService.storeDataPoint(thirdIntervalDataPoint2);

        List<AverageForInterval> averageValues = averageCalculationsService.getMovingAveragesForUser(TEST_USER, 2);

        assertEquals(3, averageValues.size(), "Wrong number of average values");

        assertEquals(200, averageValues.get(0).getAverage(), "Wrong average value");
        assertNull(averageValues.get(0).getMovingAverage(), "Moving average should not be calculated");

        assertEquals(450, averageValues.get(1).getAverage(), "Wrong average value");
        assertEquals(325, averageValues.get(1).getMovingAverage(), "Wrong moving average value");

        assertEquals(650, averageValues.get(2).getAverage(), "Wrong average value");
        assertEquals(550, averageValues.get(2).getMovingAverage(), "Wrong moving average value");
    }

    @Test
    public void dataForUserShouldBeDeleted(){
        AverageCalculationsService averageCalculationsService = new AverageCalculationsService();

        Datapoint datapoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        averageCalculationsService.storeDataPoint(datapoint);

        averageCalculationsService.deleteUserDatapoints(TEST_USER);

        assertThrows(DataNotFoundException.class, () -> {
            averageCalculationsService.getAveragesForUser(TEST_USER);
        });
    }

    @Test
    public void dataForDeviceShouldBeDeleted(){
        AverageCalculationsService averageCalculationsService = new AverageCalculationsService();

        Datapoint datapoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        averageCalculationsService.storeDataPoint(datapoint);

        averageCalculationsService.deleteDeviceDatapoints(TEST_DEVICE);

        assertThrows(DataNotFoundException.class, () -> {
            averageCalculationsService.getAveragesForDevice(TEST_DEVICE);
        });
    }
}
