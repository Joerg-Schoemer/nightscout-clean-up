package de.schoemer.joerg.nightscout.cleanup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.schoemer.joerg.nightscout.cleanup.domain.DeviceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.lang.Math.min;

@SpringBootApplication
@ConfigurationPropertiesScan
@Slf4j
public class NightscoutCleanUpApplication implements ApplicationRunner {

    private final DeviceStatusService service;

    public NightscoutCleanUpApplication(DeviceStatusService service) {
        this.service = service;
    }

    public static void main(String[] args) {
        SpringApplication.run(NightscoutCleanUpApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {

        if (args.containsOption("help")) {
            printUsage();
            return;
        }

        int num = 10;
        if (args.containsOption("num")) {
            num = Integer.parseInt(args.getOptionValues("num").get(0));
        }

        List<DeviceStatus> duplicates = service.findDuplicates();

        if (args.containsOption("out")) {
            String out = args.getOptionValues("out").get(0);
            log.info("writing duplicates to {}", out);
            try (OutputStream writer = getOutputStream(out)) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.setSerializationInclusion(NON_NULL);
                mapper.enable(INDENT_OUTPUT);
                mapper.disable(WRITE_DATES_AS_TIMESTAMPS);
                mapper.writeValue(writer, duplicates);
            } catch (Exception e) {
                log.error("could not write to file {}", out, e);
            }
        }

        if (args.containsOption("dry-run")) {
            if (log.isInfoEnabled()) {
                log.info("found {} duplicate documents in devicestatus collection.", duplicates.size());
                num = min(num, duplicates.size());
                log.info("first {} documents:", num);
                for (DeviceStatus duplicate : duplicates.subList(0, num)) {
                    log.info("duplicate: {}", duplicate);
                }
            } else {
                System.out.printf("found %d duplicate documents in devicestatus collection.%n", duplicates.size());
            }

            return;
        }

        service.deleteDuplicates(duplicates);
    }

    private void printUsage() {
        System.out.println();
        System.out.println();
        System.out.println("Usage: nightscout-clean-up [options] [commands]");
        System.out.println();
        System.out.println("Options:");
        System.out.println();
        System.out.println("\t--dry-run: do not delete duplicates just show a number of records defined by --num");
        System.out.println("\t           or write the duplicates to file with --out");
        System.out.println();
        System.out.println("\t--num: number of records to show: defaults to 10");
        System.out.println();
        System.out.println("\t--out: specifies a filename to store the duplicates as json.");
        System.out.println("\t       If the filename ends with .gz it will be gzipped.");
        System.out.println();
        System.out.println("\t--cleanup.distance: the threshold to treat vectors as equal. ");
        System.out.println("\t                    If the euclidean distance is less than the threshold,");
        System.out.println("\t                    the vector is considered identical: defaults to 10");
        System.out.println();
        System.out.println("Commands:");
        System.out.println();
        System.out.println("\t--help: show this screen");
        System.out.println();
    }

    private OutputStream getOutputStream(String out) throws IOException {
        if (out.endsWith(".gz")) {
            return new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(out)));
        }

        return new BufferedOutputStream(new FileOutputStream(out));
    }
}
