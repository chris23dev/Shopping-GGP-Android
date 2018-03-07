package com.ggp.theclub.util;

import com.ggp.theclub.model.CarLocation;
import com.ggp.theclub.model.ParkingGarage;
import com.ggp.theclub.model.ParkingLevel;
import com.ggp.theclub.model.ParkingZone;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ParkingSiteUtilsTest {

    @Test
    public void testValidCarLocations() {
        CarLocation valid = new CarLocation();
        CarLocation invalid = new CarLocation();
        valid.setZoneName("zoneName");

        assertEquals(0, ParkingSiteUtils.validCarLocations(null).size());
        assertEquals(0, ParkingSiteUtils.validCarLocations(new ArrayList<>()).size());
        assertEquals(0, ParkingSiteUtils.validCarLocations(Arrays.asList(invalid)).size());
        assertEquals(1, ParkingSiteUtils.validCarLocations(Arrays.asList(invalid, valid)).size());
        assertTrue(ParkingSiteUtils.validCarLocations(Arrays.asList(invalid, valid)).contains(valid));
    }

    @Test
    public void testFindParkingLevelByZoneName() {
        ParkingLevel level1 = new ParkingLevel();
        ParkingLevel level2 = new ParkingLevel();
        level1.setZoneName("zone1");
        level2.setZoneName("zone2");
        List<ParkingLevel> allLevels = Arrays.asList(level1, level2);

        assertEquals(0, ParkingSiteUtils.findParkingLevelByZoneName(null, null).getLevelId());
        assertEquals(0, ParkingSiteUtils.findParkingLevelByZoneName(null, new ArrayList<>()).getLevelId());
        assertEquals(0, ParkingSiteUtils.findParkingLevelByZoneName("zone1", null).getLevelId());
        assertEquals(0, ParkingSiteUtils.findParkingLevelByZoneName("zone3", allLevels).getLevelId());
        assertEquals(level1, ParkingSiteUtils.findParkingLevelByZoneName("zone1", allLevels));
        assertEquals(level2, ParkingSiteUtils.findParkingLevelByZoneName("zone2", allLevels));
    }

    @Test
    public void testFindGarage() {
        ParkingGarage garage1 = new ParkingGarage();
        ParkingGarage garage2 = new ParkingGarage();
        garage1.setGarageId(1);
        garage2.setGarageId(2);
        List<ParkingGarage> garages = Arrays.asList(garage1, garage2);

        assertEquals(0, ParkingSiteUtils.findGarage(1, null).getGarageId());
        assertEquals(0, ParkingSiteUtils.findGarage(1, new ArrayList<>()).getGarageId());
        assertEquals(0, ParkingSiteUtils.findGarage(3, garages).getGarageId());
        assertEquals(1, ParkingSiteUtils.findGarage(1, garages).getGarageId());
        assertEquals(2, ParkingSiteUtils.findGarage(2, garages).getGarageId());
    }

    @Test
    public void testFindLevelsByGarageId() {
        ParkingLevel level1 = new ParkingLevel();
        ParkingLevel level2 = new ParkingLevel();
        ParkingLevel level3 = new ParkingLevel();
        level1.setGarageId(10);
        level2.setGarageId(20);
        level3.setGarageId(20);
        List<ParkingLevel> allLevels = Arrays.asList(level1, level2, level3);

        assertEquals(0, ParkingSiteUtils.findLevelsByGarageId(1, null).size());
        assertEquals(0, ParkingSiteUtils.findLevelsByGarageId(1, new ArrayList<>()).size());

        List<ParkingLevel> levels = ParkingSiteUtils.findLevelsByGarageId(10, allLevels);
        assertEquals(1, levels.size());
        assertTrue(levels.contains(level1));

        levels = ParkingSiteUtils.findLevelsByGarageId(20, allLevels);
        assertEquals(2, levels.size());
        assertTrue(levels.contains(level2));
        assertTrue(levels.contains(level3));

        levels = ParkingSiteUtils.findLevelsByGarageId(30, allLevels);
        assertEquals(0, levels.size());
    }

    @Test
    public void testMapLevelsToZones() {
        ParkingLevel level1 = new ParkingLevel();
        ParkingLevel level2 = new ParkingLevel();
        level1.setLevelId(1);
        level2.setLevelId(2);
        level1.setZoneName("zone1");
        level2.setZoneName("zone2");

        ParkingZone zone1 = new ParkingZone();
        ParkingZone zone2 = new ParkingZone();
        zone1.setZoneName("zone1");
        zone2.setZoneName("zone2");

        List<ParkingLevel> allLevels = Arrays.asList(level1, level2);
        List<ParkingZone> allZones = Arrays.asList(zone1, zone2);

        assertEquals(0, ParkingSiteUtils.mapLevelsToZones(null, null).size());
        assertEquals(0, ParkingSiteUtils.mapLevelsToZones(null, allZones).size());

        HashMap<Integer, ParkingZone> lookup = ParkingSiteUtils.mapLevelsToZones(allLevels, null);
        assertEquals(2, lookup.size());
        assertEquals("", lookup.get(level1.getLevelId()).getZoneName());
        assertEquals("", lookup.get(level2.getLevelId()).getZoneName());

        lookup = ParkingSiteUtils.mapLevelsToZones(allLevels, allZones);
        assertEquals(2, lookup.size());
        assertEquals(zone1, lookup.get(level1.getLevelId()));
        assertEquals(zone2, lookup.get(level2.getLevelId()));
    }

    @Test
    public void testAvailableSpotsFromZones() {
        ParkingZone zone1 = new ParkingZone();
        ParkingZone zone2 = new ParkingZone();
        ParkingZone zone3 = new ParkingZone();
        zone1.getCounts().setAvailable(1);
        zone2.getCounts().setAvailable(3);
        zone3.getCounts().setAvailable(5);

        assertEquals(0, ParkingSiteUtils.availableSpotsFromZones(null));
        assertEquals(0, ParkingSiteUtils.availableSpotsFromZones(new ArrayList<>()));
        assertEquals(9, ParkingSiteUtils.availableSpotsFromZones(Arrays.asList(zone1, zone2, zone3)));
    }

    @Test
    public void testOccupiedSpotsFromZones() {
        ParkingZone zone1 = new ParkingZone();
        ParkingZone zone2 = new ParkingZone();
        ParkingZone zone3 = new ParkingZone();
        zone1.getCounts().setOccupied(2);
        zone2.getCounts().setOccupied(4);
        zone3.getCounts().setOccupied(8);

        assertEquals(0, ParkingSiteUtils.occupiedSpotsFromZones(null));
        assertEquals(0, ParkingSiteUtils.occupiedSpotsFromZones(new ArrayList<>()));
        assertEquals(14, ParkingSiteUtils.occupiedSpotsFromZones(Arrays.asList(zone1, zone2, zone3)));
    }
}
