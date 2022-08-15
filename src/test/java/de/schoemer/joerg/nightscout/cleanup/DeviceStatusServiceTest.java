package de.schoemer.joerg.nightscout.cleanup;

import de.schoemer.joerg.nightscout.cleanup.domain.DeviceStatus;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DeviceStatusServiceTest {

    Logger log = LoggerFactory.getLogger(DeviceStatusServiceTest.class);

    @Autowired
    DeviceStatusService statusService;

    @Test
    void dumpDeviceStatus() {
        List<DeviceStatus> last100 = statusService.findLast100();

        assertThat(last100).isNotEmpty();
    }

    @Test
    void findDuplicates() {
        List<DeviceStatus> duplicates = statusService.findDuplicates();

        log.debug("found {} duplicates", duplicates.size());

        duplicates.stream()
                .collect(groupingBy(item -> item.getLoop().getPredicted().getStartDate().toLocalDate(), counting()))
                .entrySet()
                .stream().sorted(Map.Entry.comparingByKey())
                .forEach(i -> log.debug("{} = {}", i.getKey(), i.getValue()));


        assertThat(duplicates).isNotEmpty();
    }
}
