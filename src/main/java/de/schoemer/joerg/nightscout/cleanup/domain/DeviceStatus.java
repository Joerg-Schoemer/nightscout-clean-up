package de.schoemer.joerg.nightscout.cleanup.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;

@Data
@Document("devicestatus")
public class DeviceStatus {

    @Id
    private final String id;

    /**
     * Device type and hostname for example openaps://hostnam
     */
    private String device;

    /**
     * dateString, prefer ISO 8601
     */
    private final ZonedDateTime created_at;

    /**
     * Loop devicestatus record
     */
    private Loop loop;

    /**
     * Pump devicestatus record
     */
    private Pump pump;

    private Override override;

    private Uploader uploader;

    private Long utcOffset;
}
