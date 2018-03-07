package com.ggp.theclub.model;

import lombok.Getter;

/**
 * Created by john.curtis on 7/3/17.
 */
public enum GGPLeaseStatusComingSoon {
    /*Are officially open*/
    O("O"),
    /*Coming soon*/
    P("P"),
    /*Coming soon*/
    Q("Q");

    @Getter private final String mStatus;

    GGPLeaseStatusComingSoon(String status) {
        mStatus = status;
    }
}
