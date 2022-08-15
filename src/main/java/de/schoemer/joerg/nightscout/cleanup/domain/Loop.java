package de.schoemer.joerg.nightscout.cleanup.domain;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class Loop {

    private ZonedDateTime timestamp;

    private String version;

    private String name;

    private Double recommendedBolus;

    private IOB iob;

    private COB cob;

    private Predicted predicted;

}
