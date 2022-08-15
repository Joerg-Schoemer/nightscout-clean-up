package de.schoemer.joerg.nightscout.cleanup.domain;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class Pump {

    private String pumpID;

    private String manufacturer;

    private String model;

    private Boolean suspended;

    private Boolean bolusing;

    private ZonedDateTime clock;

    private Long secondsFromGMT;

}
