package cz.vladimir.amp;

import cz.vladimir.amp.exception.DataNotFoundException;
import cz.vladimir.amp.exception.DuplicateDatapointException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataServiceTest {

    public static final String TEST_DEVICE = "testDevice";
    public static final String TEST_USER = "testUser";
    public static final int FIRST_INTERVAL_FIRST_TIMESTAMP = 10000000;
    public static final int FIRST_INTERVAL_SECOND_TIMESTAMP = 10000001;
    public static final int SECOND_INTERVAL_FIRST_TIMESTAMP = 10900000;
    public static final int SECOND_INTERVAL_SECOND_TIMESTAMP = 10900001;
    public static final int THIRD_INTERVAL_FIRST_TIMESTAMP = 11800000;
    public static final int THIRD_INTERVAL_SECOND_TIMESTAMP = 11800001;

    DataService dataService;

    @BeforeEach
    void init() {
        dataService = new DataService();
    }

    @Test
    public void shouldThrowExceptionIfStoringDuplicateDataPoint(){
        Datapoint dataPoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        Datapoint duplicateDatapoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 200);

        dataService.storeDataPoint(dataPoint);

        assertThrows(DuplicateDatapointException.class, () -> {
            dataService.storeDataPoint(duplicateDatapoint);
        });
    }

    @Test
    public void shouldThrowExceptionIfAccessingDataForNotExistingUser(){
        assertThrows(DataNotFoundException.class, () -> {
            dataService.getAveragesForUser("notExistingUser");
        });
    }

    @Test
    public void shouldThrowExceptionIfAccessingDataForNotExistingDevice(){
        assertThrows(DataNotFoundException.class, () -> {
            dataService.getAveragesForDevice("notExistingDevice");
        });
    }

    @Test
    public void shouldReturnOneValueByUserAfterStoringOneDatapoint(){
        DataService dataService = new DataService();

        Datapoint datapoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        dataService.storeDataPoint(datapoint);

        List<AverageForInterval> averageValues = dataService.getAveragesForUser(TEST_USER);

        assertEquals(1, averageValues.size(), "Wrong number of average values");
        assertEquals(100, averageValues.get(0).getAverage(), "Wrong average value");
    }

    @Test
    public void shouldReturnOneValueByDeviceAfterStoringOneDatapoint(){
        Datapoint datapoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        dataService.storeDataPoint(datapoint);

        List<AverageForInterval> averageValues = dataService.getAveragesForDevice(TEST_DEVICE);

        assertEquals(1, averageValues.size(), "Wrong number of average values");
        assertEquals(100, averageValues.get(0).getAverage(), "Wrong average value");
    }

    @Test
    public void shouldReturnAverageValueByUserAfterStoringTwoDatapointsWithinFifteenMinutesInterval(){
        Datapoint firstDataPoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        dataService.storeDataPoint(firstDataPoint);

        Datapoint secondDataPoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_SECOND_TIMESTAMP, 300);
        dataService.storeDataPoint(secondDataPoint);

        List<AverageForInterval> averageValues = dataService.getAveragesForUser(TEST_USER);

        assertEquals(1, averageValues.size(), "Wrong number of average values");
        assertEquals(200, averageValues.get(0).getAverage(), "Wrong average value");
    }

    @Test
    public void shouldReturnAverageValueByDeviceAfterStoringTwoDatapointsWithinFifteenMinutesInterval(){
        Datapoint firstDataPoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        dataService.storeDataPoint(firstDataPoint);

        Datapoint secondDataPoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_SECOND_TIMESTAMP, 300);
        dataService.storeDataPoint(secondDataPoint);

        List<AverageForInterval> averageValues = dataService.getAveragesForDevice(TEST_DEVICE);

        assertEquals(1, averageValues.size(), "Wrong number of average values");
        assertEquals(200, averageValues.get(0).getAverage(), "Wrong average value");
    }

    @Test
    public void shouldReturnAverageValueForEachIntervalByUserAfterStoringDatapointsSpanningOverFifteenMinutesInterval(){
        Datapoint firstIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        dataService.storeDataPoint(firstIntervalDataPoint1);

        Datapoint firstIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_SECOND_TIMESTAMP, 300);
        dataService.storeDataPoint(firstIntervalDataPoint2);

        Datapoint secondIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, SECOND_INTERVAL_FIRST_TIMESTAMP, 400);
        dataService.storeDataPoint(secondIntervalDataPoint1);

        Datapoint secondIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, SECOND_INTERVAL_SECOND_TIMESTAMP, 500);
        dataService.storeDataPoint(secondIntervalDataPoint2);

        List<AverageForInterval> averageValues = dataService.getAveragesForUser(TEST_USER);

        assertEquals(2, averageValues.size(), "Wrong number of average values");
        assertEquals(200, averageValues.get(0).getAverage(), "Wrong average value");
        assertEquals(450, averageValues.get(1).getAverage(), "Wrong average value");
    }

    @Test
    public void shouldReturnAverageValueForEachIntervalByDeviceAfterStoringDatapointsSpanningOverFifteenMinutesInterval(){
        Datapoint firstIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        dataService.storeDataPoint(firstIntervalDataPoint1);

        Datapoint firstIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_SECOND_TIMESTAMP, 300);
        dataService.storeDataPoint(firstIntervalDataPoint2);

        Datapoint secondIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, SECOND_INTERVAL_FIRST_TIMESTAMP, 400);
        dataService.storeDataPoint(secondIntervalDataPoint1);

        Datapoint secondIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, SECOND_INTERVAL_SECOND_TIMESTAMP, 500);
        dataService.storeDataPoint(secondIntervalDataPoint2);

        List<AverageForInterval> averageValues = dataService.getAveragesForDevice(TEST_DEVICE);

        assertEquals(2, averageValues.size(), "Wrong number of average values");
        assertEquals(200, averageValues.get(0).getAverage(), "Wrong average value");
        assertEquals(450, averageValues.get(1).getAverage(), "Wrong average value");
    }

    @Test
    public void shouldReturnAverageValueForEachIntervalByUserAfterStoringDatapointsSpanningOverFifteenMinutesIntervalWithEmptyIntervals(){
        Datapoint firstIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        dataService.storeDataPoint(firstIntervalDataPoint1);

        Datapoint firstIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_SECOND_TIMESTAMP, 300);
        dataService.storeDataPoint(firstIntervalDataPoint2);

        //no data for second interval

        Datapoint thirdIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, THIRD_INTERVAL_FIRST_TIMESTAMP, 400);
        dataService.storeDataPoint(thirdIntervalDataPoint1);

        Datapoint thirdIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, THIRD_INTERVAL_SECOND_TIMESTAMP, 500);
        dataService.storeDataPoint(thirdIntervalDataPoint2);

        List<AverageForInterval> averageValues = dataService.getAveragesForUser(TEST_USER);

        assertEquals(3, averageValues.size(), "Wrong number of average values");
        assertEquals(200, averageValues.get(0).getAverage(), "Wrong average value");
        assertEquals(0, averageValues.get(1).getAverage(), "Wrong average value");
        assertEquals(450, averageValues.get(2).getAverage(), "Wrong average value");
    }

    @Test
    public void shouldReturnAverageValueForEachIntervalByDeviceAfterStoringDatapointsSpanningOverFifteenMinutesIntervalWithEmptyIntervals(){
        Datapoint firstIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        dataService.storeDataPoint(firstIntervalDataPoint1);

        Datapoint firstIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_SECOND_TIMESTAMP, 300);
        dataService.storeDataPoint(firstIntervalDataPoint2);

        //no data for second interval

        Datapoint thirdIntervalDataPoint1 = new Datapoint(TEST_DEVICE, TEST_USER, THIRD_INTERVAL_FIRST_TIMESTAMP, 400);
        dataService.storeDataPoint(thirdIntervalDataPoint1);

        Datapoint thirdIntervalDataPoint2 = new Datapoint(TEST_DEVICE, TEST_USER, THIRD_INTERVAL_SECOND_TIMESTAMP, 500);
        dataService.storeDataPoint(thirdIntervalDataPoint2);

        List<AverageForInterval> averageValues = dataService.getAveragesForDevice(TEST_DEVICE);

        assertEquals(3, averageValues.size(), "Wrong number of average values");
        assertEquals(200, averageValues.get(0).getAverage(), "Wrong average value");
        assertEquals(0, averageValues.get(1).getAverage(), "Wrong average value");
        assertEquals(450, averageValues.get(2).getAverage(), "Wrong average value");
    }

    @Test
    public void dataForUserShouldBeDeleted(){
        DataService dataService = new DataService();

        Datapoint datapoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        dataService.storeDataPoint(datapoint);

        dataService.deleteUserDatapoints(TEST_USER);

        assertThrows(DataNotFoundException.class, () -> {
            dataService.getAveragesForUser(TEST_USER);
        });
    }

    @Test
    public void dataForDeviceShouldBeDeleted(){
        DataService dataService = new DataService();

        Datapoint datapoint = new Datapoint(TEST_DEVICE, TEST_USER, FIRST_INTERVAL_FIRST_TIMESTAMP, 100);
        dataService.storeDataPoint(datapoint);

        dataService.deleteDeviceDatapoints(TEST_DEVICE);

        assertThrows(DataNotFoundException.class, () -> {
            dataService.getAveragesForDevice(TEST_DEVICE);
        });
    }
}
