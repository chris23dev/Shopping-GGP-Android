package com.ggp.theclub.event;

import com.ggp.theclub.model.Tenant;

import lombok.Getter;

public class TenantSelectEvent {
    @Getter private Tenant tenant;
    @Getter private EventType eventType;

    public enum EventType { WAYFINDING, PARKING}

    public TenantSelectEvent(Tenant tenant, EventType eventType) {
        this.tenant = tenant;
        this.eventType = eventType;
    }
}