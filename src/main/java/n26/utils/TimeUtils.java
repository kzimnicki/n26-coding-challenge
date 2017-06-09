package n26.utils;

import java.time.Instant;

public final class TimeUtils {

    private TimeUtils() {}

    public static boolean isOlderThan60Seconds(long timestampInSeconds){
        return (Instant.now().getEpochSecond() - timestampInSeconds) > 60;
    }

}
