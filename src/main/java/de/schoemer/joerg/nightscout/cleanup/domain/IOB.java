package de.schoemer.joerg.nightscout.cleanup.domain;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class IOB {

    private ZonedDateTime timestamp;

    private Double iob;

}
