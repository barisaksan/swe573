package com.aksan.baris.accessibilityviolationsreporter.Violation;

import org.json.JSONException;

/**
 * Created by baris on 17.11.2015.
 */
public class Violation {
    /*
        public Violation(JSONObject jsonViolation) throws JSONException {
            id = jsonViolation.getJSONObject("_id").getString("$oid");
            type = jsonViolation.getString("type");
            description = jsonViolation.getString("description");
            reporter = jsonViolation.getString("reporter");
        }
    */

    //JSONObject jsonViolation; id = jsonViolation.getJSONObject("_id").getString("$oid");

    @Override
    public String toString() {
        String str = "[" + type + "]" + " @" + location.toString() + ": " + description;
        return str;
    }

    public MongodbId get_id() {
        return _id;
    }

    public void set_id(MongodbId _id) {
        this._id = _id;
    }

    MongodbId _id;
    String type;
    Location location;
    String description;
    String reporter;

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

    //String id;
    //ViolationType type;
    //Location location;
    //Rating rating;
    //ViolationProperty[] properties;
    //Photo[] Photos;
    //User reporter;
/*
    //TODO: implement classes
    class Rating {}
    class ViolationProperty {

    }

    class Photo {
        String alt;
        String name;
        String url;
        String violationId;
    }

    public static enum ViolationType {
        RAMP,
        SIDEWALK,
        LIGHT,
        OTHER
    }
    */
}









