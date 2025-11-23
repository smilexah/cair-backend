package ai.lab.cair.security;

import lombok.Data;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class ErrorDto {
    public static final ZoneId ZONE_ID = ZoneId.of("UTC+00:00");

    private String error;

    private String message;

    private String stackTrace;

    private long timestamp;

    private String requestId;

    public ErrorDto(String error, String message, String stackTrace) {
        this.error = error;
        this.message = message;
        this.stackTrace = stackTrace;

        this.timestamp = LocalDateTime.now(ZONE_ID).atZone(ZONE_ID).toEpochSecond();

        this.requestId = MDC.get("traceId");
    }
}
