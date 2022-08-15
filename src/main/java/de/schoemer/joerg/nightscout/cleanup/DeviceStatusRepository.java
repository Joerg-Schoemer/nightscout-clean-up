package de.schoemer.joerg.nightscout.cleanup;

import de.schoemer.joerg.nightscout.cleanup.domain.DeviceStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceStatusRepository extends MongoRepository<DeviceStatus, String> {
}
