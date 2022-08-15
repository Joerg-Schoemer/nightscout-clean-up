package de.schoemer.joerg.nightscout.cleanup;

import org.springframework.core.convert.converter.Converter;

import java.time.ZonedDateTime;

public class StringToZonedDateTime implements Converter<String, ZonedDateTime> {

    @Override
    public ZonedDateTime convert(String source) {
        return ZonedDateTime.parse(source);
    }
}
