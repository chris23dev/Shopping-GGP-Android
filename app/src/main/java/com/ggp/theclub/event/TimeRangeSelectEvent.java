package com.ggp.theclub.event;

import com.ggp.theclub.model.ParkingTimeRange;

import lombok.Getter;
import lombok.Setter;

public class TimeRangeSelectEvent {
    @Getter @Setter private ParkingTimeRange timeRange;

    public TimeRangeSelectEvent(ParkingTimeRange timeRange) {
        this.timeRange = timeRange;
    }
}
