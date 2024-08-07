package com.a2b2.plog;

import java.io.Serializable;

public class Route implements Serializable {
    private String origin;
    private String destination;
    private String distance;
    private String time;
    private String id;

    public Route(String id, String origin, String destination, String distance, String time) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.time = time;
    }

    public String getId() {
        return id;
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

    @Override
    public String toString() {
        return "Route{" +
                "id='" + id + '\'' +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", distance='" + distance + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
