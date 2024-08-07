package com.a2b2.plog;

import java.io.Serializable;

public class Route implements Serializable {
    private String origin;
    private String destination;
    private String distance;
    private String time;

    public Route(String origin, String destination, String distance, String time) {
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.time = time;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getDistance() {
        return distance;
    }

    public String getTime() {
        return time;
    }
}
