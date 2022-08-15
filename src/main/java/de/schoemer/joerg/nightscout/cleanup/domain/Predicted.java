package de.schoemer.joerg.nightscout.cleanup.domain;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@Data
public class Predicted {

    private ZonedDateTime startDate;

    private List<Double> values = Collections.emptyList();

}
