package de.schoemer.joerg.nightscout.cleanup;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StringToZonedDateTimeTest {

    StringToZonedDateTime sut = new StringToZonedDateTime();

    @Test
    void convertWithMillis() {
        ZonedDateTime convert = sut.convert("2022-08-08T22:00:00.010Z");

        Assertions.assertThat(convert).isNotNull();
    }

    @Test
    void convertWithoutMillis() {
        ZonedDateTime convert = sut.convert("2022-08-08T22:00:00Z");

        Assertions.assertThat(convert).isNotNull();
    }
}