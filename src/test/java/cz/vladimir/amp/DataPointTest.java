package cz.vladimir.amp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataPointTest {

    @Test
    public void constructorTest(){
        Datapoint datapoint = new Datapoint("testDevice", "testUser", 1000000, 200);

        assertEquals("testDevice", datapoint.getDevice(), "Wrong value for device");
        assertEquals("testUser", datapoint.getUser(), "Wrong value for user");
        assertEquals(1000000, datapoint.getTimestamp(), "Wrong value for device");
        assertEquals(200, datapoint.getValue(), "Wrong value for device");
    }

    @Test
    public void isDuplicateShouldReturnFalseIfDeviceUserAndTimestampAreDifferent(){
        Datapoint datapoint = new Datapoint("testDevice", "testUser", 1000000, 200);
        Datapoint differentDatepoint = new Datapoint("otherTestDevice", "testUser", 1000000, 300);

        assertFalse(datapoint.isDuplicate(differentDatepoint), "Datapoints should not be duplicate");
    }

    @Test
    public void isDuplicateShouldReturnTrueIfDeviceUserAndTimestampAreSame(){
        Datapoint datapoint = new Datapoint("testDevice", "testUser", 1000000, 200);
        Datapoint duplicateDatepoint = new Datapoint("testDevice", "testUser", 1000000, 300);

        assertTrue(datapoint.isDuplicate(duplicateDatepoint), "Datapoints should be duplicate");
    }
}
