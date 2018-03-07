package com.ggp.theclub.event;

import lombok.Getter;

/**
 * Created by avishek.das on 2/29/16.
 */
public class MapSelectEvent {
    @Getter private int leaseId;
    @Getter private boolean isAnchor;

    public MapSelectEvent(int leaseId, boolean isAnchor) {
        this.leaseId = leaseId;
        this.isAnchor = isAnchor;
    }
}
