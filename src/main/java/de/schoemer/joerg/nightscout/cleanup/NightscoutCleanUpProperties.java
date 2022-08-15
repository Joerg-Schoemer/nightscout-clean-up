package de.schoemer.joerg.nightscout.cleanup;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "cleanup")
public class NightscoutCleanUpProperties {

    /**
     * the threshold to decide if a vector has the same direction.
     */
    private Double distance = 10.;

}
