package de.schoemer.joerg.nightscout.cleanup;


import de.schoemer.joerg.nightscout.cleanup.domain.DeviceStatus;
import de.schoemer.joerg.nightscout.cleanup.domain.Predicted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.*;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
@Slf4j
public class DeviceStatusService {

    private final DeviceStatusRepository repository;

    private final NightscoutCleanUpProperties properties;

    public List<DeviceStatus> findLast100() {

        return repository.findAll(PageRequest.of(0, 100, Sort.by(Sort.Order.desc("created_at")))).getContent();
    }

    public List<DeviceStatus> findDuplicates() {

        Page<DeviceStatus> page = null;
        List<DeviceStatus> dups = new LinkedList<>();
        DeviceStatus lastStatus = null;
        do {
            if (page == null) {
                page = repository.findAll(PageRequest.of(0, 5000, Sort.by(Sort.Order.asc("created_at"))));
            } else {
                page = repository.findAll(page.nextPageable());
            }

            List<DeviceStatus> pageContent = page.getContent();
            if (pageContent.isEmpty()) {
                continue;
            }

            Collection<DeviceStatus> pageDups = duplicatesInPage(pageContent, lastStatus);
            dups.addAll(pageDups);

            log.info("found {} duplications in {} on page {} of {}", pageDups.size(), pageContent.size(), page.getPageable().getPageNumber() + 1, page.getTotalPages());
            lastStatus = pageContent.get(pageContent.size() - 1);
        } while (page.hasNext());

        return dups;
    }

    public void deleteDuplicates(List<DeviceStatus> statuses) {
        if (statuses.isEmpty()) {
            return;
        }
        Set<String> ids = statuses.stream().map(DeviceStatus::getId).collect(Collectors.toSet());
        log.info("deleting {} documents from devicestatus", ids.size());
        repository.deleteAllById(ids);
    }

    private Collection<DeviceStatus> duplicatesInPage(List<DeviceStatus> status, DeviceStatus last) {
        if (status.isEmpty()) {
            return Collections.emptyList();
        }
        DeviceStatus lastDeviceStatus;
        List<DeviceStatus> list;
        if (last != null) {
            lastDeviceStatus = last;
            list = status;
        } else {
            lastDeviceStatus = status.get(0);
            list = status.subList(1, status.size());
        }

        List<DeviceStatus> dups = new LinkedList<>();
        for (DeviceStatus currentDeviceStatus : list) {

            long betweenCreation = SECONDS.between(lastDeviceStatus.getCreated_at(), currentDeviceStatus.getCreated_at());

            Predicted lastPredicted = getPredicted(lastDeviceStatus);
            Predicted currentPredicted = getPredicted(currentDeviceStatus);
            Optional<Long> between = getSecondsBetween(lastPredicted, currentPredicted);

            // same start date for prediction
            if (between.isPresent() && between.get() == 0 && betweenCreation <= 120) {

                List<Double> lastValues = lastPredicted.getValues();
                List<Double> currentValues = currentPredicted.getValues();

                // distance between vectors
                double euclideanDistance = euclideanDistance(currentValues, lastValues);
                if (euclideanDistance <= properties.getDistance()) {
                    log.debug("last       created_at {}", lastDeviceStatus.getCreated_at());
                    log.debug("current    created_at {}", currentDeviceStatus.getCreated_at());
                    log.debug("time between creation {}'", betweenCreation);
                    log.debug("last     {}", lastValues);
                    log.debug("current  {}", currentValues);
                    log.debug("distance {}", euclideanDistance);

                    dups.add(lastDeviceStatus);
                }
            }
            lastDeviceStatus = currentDeviceStatus;
        }

        return dups;
    }

    private Optional<Long> getSecondsBetween(Predicted lastPredicted, Predicted currentPredicted) {
        if (lastPredicted != null && currentPredicted != null) {

            return Optional.of(ChronoUnit.SECONDS.between(lastPredicted.getStartDate(), currentPredicted.getStartDate()));
        }

        return Optional.empty();
    }

    private Predicted getPredicted(DeviceStatus deviceStatus) {
        if (deviceStatus.getLoop() == null) {

            return null;
        }

        return deviceStatus.getLoop().getPredicted();
    }

    private double euclideanDistance(List<Double> values1, List<Double> values2) {
        int longestVector = max(values1.size(), values2.size());
        List<Double> vector1 = new ArrayList<>(values1);
        List<Double> vector2 = new ArrayList<>(values2);

        // make vectors of same size
        while (vector1.size() < longestVector) {
            vector1.add(0.);
        }
        while (vector2.size() < longestVector) {
            vector2.add(0.);
        }

        double sum = zipped(vector1, vector2, (v1, v2) -> Math.pow(v1 - v2, 2))
                .mapToDouble(Double::doubleValue)
                .sum();

        return sqrt(sum);
    }

    <A, B, C> Stream<C> zipped(List<A> listA, List<B> listB, BiFunction<A, B, C> zipper) {
        int shortestLength = min(listA.size(), listB.size());

        return range(0, shortestLength).mapToObj(i -> zipper.apply(listA.get(i), listB.get(i)));
    }
}
