package n26.utils;

import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.*;

public class TimeUtilsTest {


    @Test
    public void shouldReturnFalseForTimestampLowerThan60Seconds() {
        long timestamp = Instant.now().getEpochSecond();

        boolean result = TimeUtils.isOlderThan60Seconds(timestamp);

        assertEquals(false, result);
    }

    @Test
    public void shouldReturnTrueForTimestampOlderThan60Seconds() {
        long timestamp = Instant.now().getEpochSecond() - 61;

        boolean result = TimeUtils.isOlderThan60Seconds(timestamp);

        assertEquals(true, result);
    }
}