package com.aksan.baris.accessibilityviolationsreporter.Violation;

/**
 * Created by baris on 17.11.2015.
 */
public class Violation {

    MongodbId _id;
    String type;
    Location location;
    String description;
    String reporter;

    @Override
    public String toString() {
        String str = type + " violation " + " | " + location.toString() + " | " + description;
        return str;
    }

    public MongodbId get_id() {
        return _id;
    }

    public void set_id(MongodbId _id) {
        this._id = _id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getReporter() {
        return reporter;
    }

    public String getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }
}









