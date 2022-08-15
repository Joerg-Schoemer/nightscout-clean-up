package de.schoemer.joerg.nightscout.cleanup;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
public class NightscoutCleanUpConfiguration {

    @Bean
    DeviceStatusService deviceStatusService(DeviceStatusRepository repository, NightscoutCleanUpProperties properties) {

        return new DeviceStatusService(repository, properties);
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions() {

        return new MongoCustomConversions(List.of(new StringToZonedDateTime()));
    }
}
