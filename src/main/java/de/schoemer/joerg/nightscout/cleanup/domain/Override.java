package de.schoemer.joerg.nightscout.cleanup.domain;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class Override {

    private Long duration;

    private Double multiplier;

    private Boolean active;

    private String name;

    private ZonedDateTime timestamp;

    private Range currentCorrectionRange;

}
