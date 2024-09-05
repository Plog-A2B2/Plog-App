package com.a2b2.plog;

import java.io.Serializable;

public class Route implements Serializable {
    private String origin;
    private String destination;
    private double distance;
    private int time;
    private int id;

    public Route(int id, String origin, String destination, double distance, int time) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public double getDistance() {
        return distance;
    }

    public int getTime() {
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
