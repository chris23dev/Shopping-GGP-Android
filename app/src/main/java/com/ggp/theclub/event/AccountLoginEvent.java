package com.ggp.theclub.event;

import lombok.Getter;

public class AccountLoginEvent {
    @Getter boolean isLoggedIn;

    public AccountLoginEvent(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }
}