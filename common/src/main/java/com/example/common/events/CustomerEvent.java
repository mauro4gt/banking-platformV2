package com.example.common.events;

public class CustomerEvent {

    private final String eventId;
    private final String eventType;
    private final String customerId;
    private final Boolean active;

    public CustomerEvent(String eventId, String eventType, String customerId, Boolean active) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.customerId = customerId;
        this.active = active;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Boolean getActive() {
        return active;
    }
}
