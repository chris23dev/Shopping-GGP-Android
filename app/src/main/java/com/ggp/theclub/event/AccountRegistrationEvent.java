package com.ggp.theclub.event;

import lombok.Getter;

public class AccountRegistrationEvent {
    @Getter boolean sweetpstakesEntry;

    public AccountRegistrationEvent(boolean sweetpstakesEntry) {
        this.sweetpstakesEntry = sweetpstakesEntry;
    }
}