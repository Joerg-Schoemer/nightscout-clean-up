package de.schoemer.joerg.nightscout.cleanup.domain;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class COB {

    private ZonedDateTime timestamp;

    private Double cob;

}
