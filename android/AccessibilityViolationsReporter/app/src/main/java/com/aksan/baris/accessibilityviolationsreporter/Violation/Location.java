package com.aksan.baris.accessibilityviolationsreporter.Violation;

/**
 * Created by baris on 02.01.2016.
 */
public class Location {

    @Override
    public String toString() {
        return name;
    }

    String name;
    String coordinates;

    public String getName() {
        return name;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }
}
