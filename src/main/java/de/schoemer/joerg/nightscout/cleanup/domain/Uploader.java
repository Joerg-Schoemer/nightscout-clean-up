package de.schoemer.joerg.nightscout.cleanup.domain;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class Uploader {

    private ZonedDateTime timestamp;

    private String name;

    private Integer battery;

}
