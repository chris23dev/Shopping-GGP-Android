package com.ggp.theclub.util;

import com.ggp.theclub.model.CarLocation;
import com.ggp.theclub.model.ParkingGarage;
import com.ggp.theclub.model.ParkingLevel;
import com.ggp.theclub.model.ParkingZone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import java8.lang.Integers;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class ParkingSiteUtils {

    public static List<CarLocation> validCarLocations(List<CarLocation> carLocations) {
        return carLocations == null ?
                new ArrayList<>() :
                StreamSupport.stream(carLocations).filter(c -> c.getZoneName() != null).collect(Collectors.toList());
    }

    public static ParkingLevel findParkingLevelByZoneName(String zoneName, List<ParkingLevel> levels) {
        if (levels == null || zoneName == null) {
            return new ParkingLevel();
        }

        return StreamSupport.stream(levels)
                .filter(l -> l.getZoneName() != null && l.getZoneName().equals(zoneName))
                .findFirst().orElse(new ParkingLevel());
    }

    public static ParkingGarage findGarage(int garageId, List<ParkingGarage> garages) {
        if (garages == null) {
            return new ParkingGarage();
        }

        return StreamSupport.stream(garages)
                .filter(g -> g.getGarageId() == garageId)
                .findFirst().orElse(new ParkingGarage());
    }

    public static List<ParkingLevel> findLevelsByGarageId(int garageId, List<ParkingLevel> levels) {
        return levels == null ?
                new ArrayList<>() :
                StreamSupport.stream(levels).filter(l -> l.getGarageId() == garageId)
                        .sorted((l1, l2) -> Integers.compare(l1.getSort(), l2.getSort())).collect(Collectors.toList());
    }

    public static HashMap<Integer, ParkingZone> mapLevelsToZones(List<ParkingLevel> levels, List<ParkingZone> zones) {
        if (levels == null) {
            return new HashMap<>();
        }

        HashMap<Integer, ParkingZone> lookup = new HashMap<>();
        StreamSupport.stream(levels).forEach(level -> lookup.put(level.getLevelId(), findParkingZone(level.getZoneName(), zones)));
        return lookup;
    }

    public static int availableSpotsFromZones(Collection<ParkingZone> zones) {
        return zones == null ? 0 :
                StreamSupport.stream(zones)
                        .collect(Collectors.summingInt(z -> z.getCounts() == null ? 0 : z.getCounts().getAvailable()));
    }

    public static int occupiedSpotsFromZones(Collection<ParkingZone> zones) {
        return zones == null ? 0 :
                StreamSupport.stream(zones)
                        .collect(Collectors.summingInt(z -> z.getCounts() == null ? 0 : z.getCounts().getOccupied()));
    }

    private static ParkingZone findParkingZone(String zoneName, List<ParkingZone> zones) {
        if (zoneName == null || zones == null) {
            return new ParkingZone();
        }

        return StreamSupport.stream(zones)
                .filter(z -> z.getZoneName().equals(zoneName))
                .findFirst().orElse(new ParkingZone());
    }
}
