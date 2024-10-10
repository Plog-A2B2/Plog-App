package com.a2b2.plog;

import java.util.UUID;


public class LocationDTO {
    private final double latitude;
    private final double longitude;
    private final UUID uuid;

    public LocationDTO(double latitude, double longitude, UUID uuid) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.uuid = uuid;
    }

    // Getter와 Setter 생략 가능
}
